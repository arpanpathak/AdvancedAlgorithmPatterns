# 18.3 Thread-Safe Sharded LRU Cache

> **Source:** [`src/main/kotlin/cache/ThreadSafeLruCache.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/cache/ThreadSafeLruCache.kt)
> **Pattern:** sharding + locks · **Core page**

## The Problem

Make the [LRU cache](lru-cache.md) safe for **concurrent access**: many threads calling `get`/`put`/`remove` without corrupting state.

- Constraints: generic `K, V`; shard count a power of 2.

## Intuition — one lock serializes everything; *sharding* parallelizes

The naive fix — lock the whole cache — is correct but single-threaded in effect: every thread waits on one mutex. **Sharding** splits the cache into `shardCount` independent LRU caches, each with **its own lock**, and routes each key to one shard by hash. Concurrent accesses to *different* shards run in parallel; only same-shard accesses serialize.

**Why is the per-shard LRU unchanged?** Each shard is a complete little LRU cache (map + recency + capacity). The global capacity is split across shards (the repo distributes `totalCapacity / shardCount`, remainder to the first shards). The LRU semantics hold *within* a shard — which is the standard approximation: eviction is per-shard, not global.

**The hash routing** (`shardOf`): `hashCode` spread (`h xor (h ushr 16)`) then masked by `shardCount - 1` — the mask works because the shard count is required to be a power of two. The spread step guards against weak hashCodes clustering (the same reason `HashMap` spreads hashes).

**Why `removeEldestEntry` instead of manual eviction?** The repo's shard uses an *access-ordered* LinkedHashMap with `removeEldestEntry(size > maxCapacity)` — the map evicts automatically on the next insert past capacity. That's the [18.1](lru-cache.md) design in its most compact form, wrapped in a `ReentrantLock`.

## Approach 1 — One global lock (correct, serialized)

Lock every operation: safe but throughput collapses under contention.

## Approach 2 — Sharded with per-shard locks (the repo's version, optimal for contention)

```kotlin
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ShardedLruCache<K, V>(
    private val totalCapacity: Int,
    private val shardCount: Int = 16
) {
    private val shards: Array<LruShard<K, V>>

    init {
        require(totalCapacity > 0) { "Capacity must be positive" }
        require(shardCount > 0 && (shardCount and (shardCount - 1)) == 0) { "Shard count must be a power of 2" }

        val base = totalCapacity / shardCount
        var remainder = totalCapacity % shardCount

        shards = Array(shardCount) {
            val cap = base + if (remainder-- > 0) 1 else 0
            LruShard<K, V>(cap)
        }
    }

    fun get(key: K): V? = shardOf(key).get(key)
    fun put(key: K, value: V) = shardOf(key).put(key, value)
    fun remove(key: K): V? = shardOf(key).remove(key)
    fun clear() = shards.forEach { it.clear() }
    fun size(): Int = shards.sumOf { it.size() }

    // Route a key to one shard by a spread hash
    private fun shardOf(key: K): LruShard<K, V> {
        val h = key?.hashCode() ?: 0
        val hash = h xor (h ushr 16)            // spread: prevents poor hash-code clustering
        return shards[hash and (shardCount - 1)]  // mask works because shardCount is a power of 2
    }

    private class LruShard<K, V>(private val maxCapacity: Int) {
        private val lock = ReentrantLock()

        // Access-ordered LinkedHashMap: get() refreshes recency; auto-evict past capacity
        private val map = object : LinkedHashMap<K, V>(maxCapacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
                return size > maxCapacity
            }
        }

        fun get(key: K): V? = lock.withLock { map[key] }
        fun put(key: K, value: V) { lock.withLock { map[key] = value } }
        fun remove(key: K): V? = lock.withLock { map.remove(key) }
        fun clear() = lock.withLock { map.clear() }
        fun size(): Int = lock.withLock { map.size }
    }
}
```

```java
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ShardedLruCache<K, V> {
    private final LruShard<K, V>[] shards;

    /** @param totalCapacity total entries @param shardCount power of 2 */
    @SuppressWarnings("unchecked")
    public ShardedLruCache(int totalCapacity, int shardCount) {
        if ((shardCount & (shardCount - 1)) != 0) throw new IllegalArgumentException();

        int base = totalCapacity / shardCount, rem = totalCapacity % shardCount;
        shards = new LruShard[shardCount];
        for (int i = 0; i < shardCount; i++) shards[i] = new LruShard<>(base + (rem-- > 0 ? 1 : 0));
    }

    public V get(K key) { return shardOf(key).get(key); }
    public void put(K key, V value) { shardOf(key).put(key, value); }
    public V remove(K key) { return shardOf(key).remove(key); }

    private LruShard<K, V> shardOf(K key) {
        int h = key.hashCode();
        int hash = h ^ (h >>> 16);                        // spread
        return shards[hash & (shards.length - 1)];        // power-of-2 mask
    }

    private static class LruShard<K, V> {
        private final ReentrantLock lock = new ReentrantLock();
        private final LinkedHashMap<K, V> map;

        LruShard(int cap) {
            map = new LinkedHashMap<>(cap, 0.75f, true) { // access-ordered
                @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > cap;                  // auto-evict past capacity
                }
            };
        }

        V get(K key) { lock.lock(); try { return map.get(key); } finally { lock.unlock(); } }
        void put(K key, V value) { lock.lock(); try { map.put(key, value); } finally { lock.unlock(); } }
        V remove(K key) { lock.lock(); try { return map.remove(key); } finally { lock.unlock(); } }
    }
}
```

```cpp
#include <list>
#include <mutex>
#include <unordered_map>

template <typename K, typename V>
class ShardedLruCache {
    struct Shard {
        std::mutex mtx;
        int cap;
        std::list<std::pair<K, V>> order;                       // recency (head = LRU)
        std::unordered_map<K, typename std::list<std::pair<K, V>>::iterator> map;

        explicit Shard(int c) : cap(c) {}

        void touch(typename std::unordered_map<K, typename std::list<std::pair<K, V>>::iterator>::iterator it) {
            order.splice(order.end(), order, it->second);
        }

        V get(const K& key) {
            std::lock_guard<std::mutex> lock(mtx);
            auto it = map.find(key);
            if (it == map.end()) return V{};
            touch(it);                                          // refresh recency
            return it->second->second;
        }

        void put(const K& key, const V& value) {
            std::lock_guard<std::mutex> lock(mtx);
            auto it = map.find(key);
            if (it != map.end()) { it->second->second = value; touch(it); return; }
            if ((int)map.size() == cap) {                       // evict the LRU (head)
                map.erase(order.front().first);
                order.pop_front();
            }
            order.emplace_back(key, value);
            map[key] = std::prev(order.end());
        }
    };

    std::vector<Shard> shards;

    Shard& shardOf(const K& key) {
        size_t h = std::hash<K>{}(key);
        size_t hash = h ^ (h >> 16);                            // spread
        return shards[hash & (shards.size() - 1)];              // power-of-2 mask
    }

public:
    /** @param totalCapacity total entries @param shardCount power of 2 */
    ShardedLruCache(int totalCapacity, int shardCount)
        : shards([&] {
              std::vector<Shard> v;
              int base = totalCapacity / shardCount, rem = totalCapacity % shardCount;
              for (int i = 0; i < shardCount; i++) v.emplace_back(base + (rem-- > 0 ? 1 : 0));
              return v;
          }()) {}

    V get(const K& key) { return shardOf(key).get(key); }
    void put(const K& key, const V& value) { shardOf(key).put(key, value); }
};
```

```python
import threading
from collections import OrderedDict

class ShardedLruCache:
    """@param total_capacity: total entries  @param shard_count: power of 2"""

    def __init__(self, total_capacity: int, shard_count: int = 16):
        assert shard_count > 0 and (shard_count & (shard_count - 1)) == 0
        self.shard_count = shard_count
        base, rem = divmod(total_capacity, shard_count)
        self.shards = [_Shard(base + (1 if i < rem else 0)) for i in range(shard_count)]

    def _shard_of(self, key):
        h = hash(key)                        # Python ints hash to themselves
        h ^= h >> 16                         # spread
        return self.shards[h & (self.shard_count - 1)]   # power-of-2 mask

    def get(self, key): return self._shard_of(key).get(key)
    def put(self, key, value): self._shard_of(key).put(key, value)
    def remove(self, key): return self._shard_of(key).remove(key)

class _Shard:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.lock = threading.Lock()
        self.cache = OrderedDict()           # access-order by move_to_end

    def get(self, key):
        with self.lock:
            if key not in self.cache:
                return None
            self.cache.move_to_end(key)      # refresh recency
            return self.cache[key]

    def put(self, key, value):
        with self.lock:
            if key in self.cache:
                self.cache.move_to_end(key)  # refresh recency on update
            self.cache[key] = value
            while len(self.cache) > self.capacity:
                self.cache.popitem(last=False)   # auto-evict the LRU (head)

    def remove(self, key):
        with self.lock:
            return self.cache.pop(key, None)
```

```rust
use std::collections::HashMap;
use std::hash::Hash;
use std::sync::Mutex;

struct ShardedLruCache<K: Hash + Eq + Clone, V: Clone> {
    shards: Vec<Mutex<LruShard<K, V>>>,
}

struct LruShard<K: Hash + Eq + Clone, V: Clone> {
    cap: usize,
    order: Vec<K>,                                  // recency (front = LRU)
    map: HashMap<K, (V, usize)>,                    // key -> (value, position in order)
}

impl<K: Hash + Eq + Clone, V: Clone> LruShard<K, V> {
    fn get(&mut self, key: &K) -> Option<V> {
        let (v, pos) = self.map.get(key)?;
        let pos = *pos;
        // move to the back (most recent): remove from order, re-push
        self.order.remove(pos);
        self.order.push(key.clone());
        let last = self.order.len() - 1;
        self.map.get_mut(key).unwrap().1 = last;
        Some(v.clone())
    }

    fn put(&mut self, key: K, value: V) {
        if let Some(entry) = self.map.get_mut(&key) {
            entry.0 = value;
            let pos = entry.1;
            self.order.remove(pos);
            self.order.push(key.clone());
            entry.1 = self.order.len() - 1;
            return;
        }
        if self.map.len() == self.cap {             // evict the LRU (front of order)
            let lru = self.order.remove(0);
            self.map.remove(&lru);
        }
        self.order.push(key.clone());
        self.map.insert(key, (value, self.order.len() - 1));
    }
}

impl<K: Hash + Eq + Clone, V: Clone> ShardedLruCache<K, V> {
    /// @param total_capacity total entries  @param shard_count power of 2
    fn new(total_capacity: usize, shard_count: usize) -> Self {
        let base = total_capacity / shard_count;
        let rem = total_capacity % shard_count;
        let shards = (0..shard_count)
            .map(|i| Mutex::new(LruShard { cap: base + (i < rem) as usize, order: Vec::new(), map: HashMap::new() }))
            .collect();
        ShardedLruCache { shards }
    }

    fn shard_of(&self, key: &K) -> usize {
        let mut h = std::collections::hash_map::DefaultHasher::new();
        std::hash::Hash::hash(key, &mut h);
        let h = std::hash::Hasher::finish(&h);
        ((h ^ (h >> 16)) as usize) & (self.shards.len() - 1)   // power-of-2 mask
    }

    fn get(&self, key: &K) -> Option<V> { self.shards[self.shard_of(key)].lock().unwrap().get(key) }
    fn put(&self, key: K, value: V) { self.shards[self.shard_of(&key)].lock().unwrap().put(key, value) }
}
```

## Dry run

**Input:** `ShardedLruCache(4, 2)` — two shards, capacity 2 each.

```
capacity split: base = 2, rem = 0 -> shard 0: cap 2, shard 1: cap 2.

put("a",1): shardOf("a") = hash-spread & 1.  Say shard 0.  shard0 = {a:1}
put("b",2): shard 1 (say).  shard1 = {b:2}
put("c",3): shard 0 (say).  shard0 = {a:1, c:3}
put("d",4): shard 1.  shard1 = {b:2, d:4}
get("a"):   shard 0: move a to tail.  shard0 order = {c, a}.  -> 1
put("e",5): shard 0 -> at cap (2) -> evict the LRU = c.  shard0 = {a:1, e:5}
get("c"):   -1 (evicted from its shard) ✓
```

The concurrency property is structural: `get("a")` and `get("b")` touch *different shards* (0 and 1), so their locks never contend — two threads can serve both simultaneously. Only keys landing in the same shard serialize. The per-shard LRU is exact; the *global* LRU is approximated by the hash split.

## Complexity

**Time.** O(1) per operation (hash + lock + list moves):

$$
T(n) = O(1) \text{ per operation}
$$

**Space.** Total capacity across shards:

$$
S = O(\text{capacity})
$$

## Variants & follow-ups

- **LRU Cache** ([18.1](lru-cache.md)) — the single-threaded base design this page shards.
- **ConcurrentHashMap-based LRU** — the lock-free alternative: per-bucket concurrency via `ConcurrentHashMap`'s internal locking.
- **Interview follow-up:** "Why does the shard count need to be a power of two?" The routing uses a mask (`hash & (shardCount - 1)`) — valid only when `shardCount - 1` is all-ones below the top bit, i.e., when `shardCount` is a power of two. A modulo would work for any count but costs a division; the mask is the constant-time form.
