# 18.1 LRU Cache

> **Source:** [`src/main/kotlin/cache/LRUCache.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/cache/LRUCache.kt)
> **Pattern:** LinkedHashMap, access-order · **Core page**

## The Problem

Design a cache with `get(key)` and `put(key, value)` — both **O(1)** — evicting the **least recently used** key when the capacity is exceeded.

- Constraints: capacity up to $3 \times 10^3$; $3 \times 10^5$ operations.

## Examples

```
LRUCache(2);  put(1,1); put(2,2); get(1) -> 1;  put(3,3) evicts 2;  get(2) -> -1
```

## Intuition — a hash map for O(1) lookup, a *recency order* for eviction

The contract "get/put in O(1)" forces a hash map. The contract "evict least-recently-used" forces an *order* over the entries. The `LinkedHashMap` provides both: it's a hash map whose entries are also a doubly-linked list — and with **access-order** enabled (`LinkedHashMap(accessOrder = true)` in Java), every `get` re-links the entry to the *tail* (most recent). The head is always the LRU entry.

The repo's version manages the order manually:

- `get(key)`: if present, **re-insert it** (remove + put) — which moves it to the map's tail;
- `put(key, value)`: if present, remove it first (so the re-insert lands at the tail); if at capacity, remove `cache.keys.first()` — the oldest — then insert.

**Why does "move-to-end on access" implement LRU?** "Least recently used" = the entry whose access was longest ago. Every access (get or put) refreshes recency by moving the entry to the most-recent end; the least-recent end holds the entry that hasn't been touched the longest. Evicting that end is exactly the policy.

**The hand-rolled version** (hash map key → list node + doubly linked list) is the same structure without the library: the map gives O(1) node access, the list gives O(1) move-to-end/evict-head. The repo's `LRUCacheLinkedList.kt` is that version — the LinkedHashMap page is its compact form.

## Approach 1 — HashMap + timestamps (O(log n) or worse)

Scan for the min timestamp on eviction: correct but breaks the O(1) contract.

## Approach 2 — LinkedHashMap in access order (the repo's version, optimal)

```kotlin
class LRUCache(private val capacity: Int) {

    private val cache: LinkedHashMap<Int, Int> = LinkedHashMap()

    /**
     * @param key lookup key
     * @return    value, or -1 if absent (and marks the key recently used)
     */
    fun get(key: Int): Int {
        val data = cache[key] ?: -1

        if (data != -1)
            put(key, data)              // re-insert: moves the entry to the tail (most recent)
        return data
    }

    /**
     * @param key   key to store
     * @param value value to store (also marks the key recently used)
     */
    fun put(key: Int, value: Int) {
        if (cache.containsKey(key)) {
            cache.remove(key)           // drop the old entry so the re-insert lands at the tail
        } else if (cache.size == this.capacity) {
            // Eviction policy: remove the least recently used element (the head)
            cache.remove(cache.keys.first())
        }
        cache[key] = value              // insert at the tail
    }
}
```

```java
import java.util.*;

public class LRUCache {
    private final LinkedHashMap<Integer, Integer> cache;

    /** @param capacity max entries before eviction */
    public LRUCache(int capacity) {
        // accessOrder = true: every get moves the entry to the tail (most recent)
        cache = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    /**
     * @param key lookup key
     * @return    value, or -1 if absent (and marks the key recently used)
     */
    public int get(int key) {
        return cache.getOrDefault(key, -1);   // accessOrder=true refreshes recency for us
    }

    /**
     * @param key   key to store
     * @param value value to store (also marks the key recently used)
     */
    public void put(int key, int value) {
        cache.put(key, value);                // put on an existing key also refreshes recency
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return cache.size() > capacity;       // auto-evict the LRU entry on overflow
    }
}
```

```cpp
#include <list>
#include <unordered_map>

class LRUCache {
    std::list<std::pair<int, int>> order;                  // recency order (head = LRU)
    std::unordered_map<int, std::list<std::pair<int, int>>::iterator> map;
    int cap;

    void touch(std::unordered_map<int, std::list<std::pair<int, int>>::iterator>::iterator it) {
        order.splice(order.end(), order, it->second);      // move to the tail (most recent)
    }

public:
    /** @param capacity max entries before eviction */
    LRUCache(int capacity) : cap(capacity) {}

    /**
     * @param key lookup key
     * @return    value, or -1 if absent (and marks the key recently used)
     */
    int get(int key) {
        auto it = map.find(key);
        if (it == map.end()) return -1;
        touch(it);                                          // refresh recency
        return it->second->second;
    }

    /**
     * @param key   key to store
     * @param value value to store (also marks the key recently used)
     */
    void put(int key, int value) {
        auto it = map.find(key);
        if (it != map.end()) {                              // update: refresh recency
            it->second->second = value;
            touch(it);
            return;
        }
        if (map.size() == cap) {                            // evict the LRU (head)
            map.erase(order.front().first);
            order.pop_front();
        }
        order.emplace_back(key, value);
        map[key] = std::prev(order.end());
    }
};
```

```python
from collections import OrderedDict

class LRUCache:
    """@param capacity: max entries before eviction"""

    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = OrderedDict()          # insertion order = recency (tail = most recent)

    def get(self, key: int) -> int:
        """@return: value, or -1 if absent (and marks the key recently used)"""
        if key not in self.cache:
            return -1
        self.cache.move_to_end(key)         # refresh recency
        return self.cache[key]

    def put(self, key: int, value: int) -> None:
        """@param key: key to store  @param value: value to store"""
        if key in self.cache:
            self.cache.move_to_end(key)     # refresh recency on update
        self.cache[key] = value
        if len(self.cache) > self.capacity:
            self.cache.popitem(last=False)  # evict the LRU (the head)
```

```rust
use std::collections::HashMap;

struct LRUCache {
    cap: usize,
    map: HashMap<i32, (i32, u64)>,   // key -> (value, recency stamp)
    clock: u64,                      // monotonic recency counter
}

impl LRUCache {
    /// @param capacity max entries before eviction
    fn new(capacity: i32) -> Self {
        LRUCache { cap: capacity as usize, map: HashMap::new(), clock: 0 }
    }

    /// @param key lookup key
    /// @return    value, or -1 if absent (and marks the key recently used)
    fn get(&mut self, key: i32) -> i32 {
        match self.map.get_mut(&key) {
            Some((v, stamp)) => { *stamp = self.clock; self.clock += 1; *v }
            None => -1,
        }
    }

    /// @param key   key to store
    /// @param value value to store (also marks the key recently used)
    fn put(&mut self, key: i32, value: i32) {
        if let Some((v, stamp)) = self.map.get_mut(&key) {
            *v = value;
            *stamp = self.clock;
            self.clock += 1;
            return;
        }
        if self.map.len() == self.cap {            // evict the least recently used
            let lru = self.map.iter()
                .min_by_key(|(_, &(_, s))| s)
                .map(|(&k, _)| k)
                .unwrap();
            self.map.remove(&lru);
        }
        self.map.insert(key, (value, self.clock));
        self.clock += 1;
    }
}
```

## Dry run

**Input:** `LRUCache(2)`.

```
put(1,1): cache = {1:1}.                        size 1
put(2,2): cache = {1:1, 2:2}.                   size 2
get(1):   re-insert 1 -> order {2:2, 1:1}.  return 1
put(3,3): at capacity -> evict cache.keys.first() = 2.  cache = {1:1, 3:3}
get(2):   -1 ✓   (2 was evicted as the least recently used)
get(3):   re-insert -> order {1:1, 3:3}.  return 3
```

The eviction at `put(3,3)` is the policy in action: `1` was accessed most recently (get(1)), `2` hasn't been touched since its insert — so `2` is the LRU and dies. Access-order re-insertion is the only mechanism; everything else follows from it.

## Complexity

**Time.** All ops are hash-map + list operations:

$$
T(n) = O(1) \text{ per operation}
$$

**Space.** The cache entries:

$$
S = O(\text{capacity})
$$

## Variants & follow-ups

- **LFU Cache** ([18.2](lfu-cache.md)) — the frequency dimension; eviction by count, then recency.
- **LRUCacheLinkedList** (`src/main/kotlin/cache/LRUCacheLinkedList.kt`) — the hand-rolled map + doubly-linked list, for interviews where LinkedHashMap isn't available.
- **Thread-safe LRU** ([18.3](thread-safe-lru-cache.md)) — the sharded + locked version for concurrent access.
- **Interview follow-up:** "Why does re-inserting implement recency?" The map's iteration order is insertion order; removing and re-inserting a key drops it from its old position and appends it — so the tail is always "most recently touched" and the head is "least recently touched". The LRU eviction is literally `first key`, O(1).
