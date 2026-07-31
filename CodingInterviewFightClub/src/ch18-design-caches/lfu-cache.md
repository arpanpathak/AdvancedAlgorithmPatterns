# 18.2 LFU Cache

> **Source:** [`src/main/kotlin/cache/LFUCache.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/cache/LFUCache.kt)
> **Pattern:** 3 maps + min-frequency · **Core page**

## The Problem

Design a cache with `get(key)` and `put(key, value)` — both **O(1)** — evicting the **least frequently used** key when at capacity (ties broken by *least recently used* within the frequency).

- Constraints: capacity up to $10^4$; $10^5$ operations.

## Examples

```
LFUCache(2);  put(1,1); put(2,2); get(1) -> 1 (1 now freq 2);  put(3,3) evicts 2 (freq 1); get(2) -> -1
```

## Intuition — three maps make each operation a single-step bucket move

LRU needed two structures; LFU adds the *frequency* dimension. The repo's composition:

1. `vals: key → value` — O(1) lookup;
2. `freq: key → count` — the access count;
3. `lists: count → LinkedHashSet<key>` — for each frequency, the keys at that frequency, in LRU order (the LinkedHashSet gives insertion order = recency within the bucket).

Plus one stateful counter: `min` — the *current minimum frequency*, which is what eviction pops from.

**The operations:**

- `get(key)`: bump `freq[key]`; remove the key from its old bucket; if that emptied the `min` bucket, `min++`; add the key to the `count+1` bucket.
- `put(key, value)`: if present, update the value and bump (via `get`); if at capacity, **evict `lists[min].first()`** — the LRU entry of the least-frequent bucket; then insert with frequency 1 and set `min = 1`.

**Why is `min` the right eviction target?** The least-frequently-used entry is in the *minimum* frequency bucket; within that bucket, the LinkedHashSet's first element is the least recently used — the tie-break the problem demands. `min` only ever increases on a get (a bucket emptied below) or resets to 1 on insert — so it's maintained in O(1) per operation.

**Why LinkedHashSet for the buckets?** A frequency bucket needs "remove a specific key" (when it's bumped) and "remove the first key" (when evicting) — both O(1) with a hash set that preserves insertion order.

## Approach 1 — LFU via min-heap of (freq, time, key)

A heap keyed by (freq, recency): correct, but get/put need O(log n) heap operations.

## Approach 2 — Three maps + min-counter (the repo's version, optimal)

```kotlin
class LFUCache(capacity: Int) {

    private val vals = mutableMapOf<Int, Int>()                 // key -> value
    private val freq = mutableMapOf<Int, Int>()                 // key -> access count
    private val lists = mutableMapOf<Int, LinkedHashSet<Int>>() // count -> keys (LRU order)
    private val MAX_SIZE = capacity
    private var min = -1

    init {
        lists[1] = LinkedHashSet()
    }

    /**
     * @param key lookup key
     * @return    value, or -1 if absent (and bumps the key's frequency)
     */
    fun get(key: Int): Int {
        if (!vals.containsKey(key)) return -1

        val count = freq[key]!!
        freq[key] = count + 1
        lists[count]?.remove(key)                    // leave the old bucket

        if (count == min && lists[count]?.size == 0) {
            min++                                    // the min bucket emptied: raise it
        }

        if (!lists.containsKey(count + 1)) {
            lists[count + 1] = LinkedHashSet()
        }
        lists[count + 1]?.add(key)                   // join the new bucket (tail = newest)
        return vals[key]!!
    }

    /**
     * @param key   key to store
     * @param value value to store
     */
    fun put(key: Int, value: Int) {
        if (MAX_SIZE <= 0) return
        if (vals.containsKey(key)) {
            vals.put(key, value)
            get(key)                                 // update + bump frequency
            return
        }

        if (vals.size >= MAX_SIZE) {                 // evict: LRU of the min-frequency bucket
            val evict = lists[min]?.first()
            lists[min]?.remove(evict)
            vals.remove(evict)
            freq.remove(evict)
        }

        vals[key] = value
        freq[key] = 1
        min = 1
        lists[1]?.add(key)
    }
}
```

```java
import java.util.*;

public class LFUCache {
    private final Map<Integer, Integer> vals = new HashMap<>();      // key -> value
    private final Map<Integer, Integer> freq = new HashMap<>();      // key -> count
    private final Map<Integer, LinkedHashSet<Integer>> lists = new HashMap<>();  // count -> keys
    private final int capacity;
    private int min = -1;

    /** @param capacity max entries before eviction */
    public LFUCache(int capacity) {
        this.capacity = capacity;
        lists.put(1, new LinkedHashSet<>());
    }

    /**
     * @param key lookup key
     * @return    value, or -1 if absent (and bumps the key's frequency)
     */
    public int get(int key) {
        if (!vals.containsKey(key)) return -1;

        int count = freq.get(key);
        freq.put(key, count + 1);
        lists.get(count).remove(key);                    // leave the old bucket
        if (count == min && lists.get(count).isEmpty()) min++;   // min bucket emptied

        lists.computeIfAbsent(count + 1, k -> new LinkedHashSet<>()).add(key);  // new bucket
        return vals.get(key);
    }

    /**
     * @param key   key to store
     * @param value value to store
     */
    public void put(int key, int value) {
        if (capacity <= 0) return;
        if (vals.containsKey(key)) {
            vals.put(key, value);
            get(key);                                    // update + bump frequency
            return;
        }
        if (vals.size() >= capacity) {                   // evict: LRU of the min bucket
            int evict = lists.get(min).iterator().next();
            lists.get(min).remove(evict);
            vals.remove(evict);
            freq.remove(evict);
        }
        vals.put(key, value);
        freq.put(key, 1);
        min = 1;
        lists.get(1).add(key);
    }
}
```

```cpp
#include <list>
#include <unordered_map>

class LFUCache {
    int cap, minFreq;
    std::unordered_map<int, int> vals;                          // key -> value
    std::unordered_map<int, int> freq;                          // key -> count
    std::unordered_map<int, std::list<int>> buckets;            // count -> keys (LRU order)
    std::unordered_map<int, std::list<int>::iterator> iters;    // key -> its bucket position

    void bump(int key) {
        int f = freq[key]++;
        buckets[f].erase(iters[key]);                           // leave the old bucket
        if (buckets[f].empty() && f == minFreq) minFreq++;      // min bucket emptied
        buckets[f + 1].push_back(key);                          // join the new bucket
        iters[key] = std::prev(buckets[f + 1].end());
    }

public:
    /** @param capacity max entries before eviction */
    LFUCache(int capacity) : cap(capacity), minFreq(0) {}

    /**
     * @param key lookup key
     * @return    value, or -1 if absent (and bumps the key's frequency)
     */
    int get(int key) {
        if (!vals.count(key)) return -1;
        bump(key);
        return vals[key];
    }

    /**
     * @param key   key to store
     * @param value value to store
     */
    void put(int key, int value) {
        if (cap <= 0) return;
        if (vals.count(key)) { vals[key] = value; bump(key); return; }
        if ((int)vals.size() >= cap) {                          // evict: LRU of the min bucket
            int evict = buckets[minFreq].front();
            buckets[minFreq].pop_front();
            vals.erase(evict); freq.erase(evict); iters.erase(evict);
        }
        vals[key] = value; freq[key] = 1; minFreq = 1;
        buckets[1].push_back(key);
        iters[key] = std::prev(buckets[1].end());
    }
};
```

```python
from collections import OrderedDict

class LFUCache:
    """@param capacity: max entries before eviction"""

    def __init__(self, capacity: int):
        self.capacity = capacity
        self.vals = {}                       # key -> value
        self.freq = {}                       # key -> count
        self.lists = {1: OrderedDict()}      # count -> keys (LRU order)
        self.min = 1

    def _bump(self, key: int) -> None:
        count = self.freq[key]
        self.freq[key] = count + 1
        del self.lists[count][key]           # leave the old bucket
        if count == self.min and not self.lists[count]:
            self.min += 1                    # min bucket emptied: raise it
        self.lists.setdefault(count + 1, OrderedDict())[key] = None   # new bucket

    def get(self, key: int) -> int:
        """@return: value, or -1 if absent (and bumps the key's frequency)"""
        if key not in self.vals:
            return -1
        self._bump(key)
        return self.vals[key]

    def put(self, key: int, value: int) -> None:
        """@param key: key to store  @param value: value to store"""
        if self.capacity <= 0:
            return
        if key in self.vals:
            self.vals[key] = value
            self._bump(key)                  # update + bump frequency
            return
        if len(self.vals) >= self.capacity:  # evict: LRU of the min bucket
            evict = next(iter(self.lists[self.min]))
            del self.lists[self.min][evict]
            del self.vals[evict], self.freq[evict]
        self.vals[key] = value
        self.freq[key] = 1
        self.min = 1
        self.lists[1][key] = None
```

```rust
use std::collections::{HashMap, HashSet};
use std::hash::Hash;

impl Solution {
    // Illustrative structure (std lacks LinkedHashSet): freq -> key set + per-key counts.
    // The canonical triple-map design is shown in the Kotlin/Java/C++ blocks.
}
```

## Dry run

**Input:** `LFUCache(2)`.

```
put(1,1): vals={1:1}, freq={1:1}, lists={1:{1}}, min=1.
put(2,2): vals={1:1,2:2}, freq={1:1,2:1}, lists={1:{1,2}}, min=1.
get(1):   freq[1]=2.  lists={1:{2}, 2:{1}}.  return 1.
put(3,3): at capacity -> evict lists[min=1].first() = 2 (freq 1, oldest in bucket).
          vals={1:1,3:3}, freq={1:2,3:1}, lists={1:{3}, 2:{1}}, min=1.
get(2):   -1 ✓   (2 was evicted: lowest frequency, and least recently used within it)
```

The eviction shows the two-tier policy: key `1` has frequency 2, key `2` has frequency 1 — so `2` is evicted even though it was used *more recently* than... hmm, actually `1` was used most recently too. The frequency tier decides first: `1` (freq 2) beats `2` (freq 1) regardless of recency. Only *within* the same frequency does the LinkedHashSet order (recency) matter.

## Complexity

**Time.** All ops are single bucket moves:

$$
T(n) = O(1) \text{ per operation}
$$

**Space.** Three maps:

$$
S = O(\text{capacity})
$$

## Variants & follow-ups

- **LRU Cache** ([18.1](lru-cache.md)) — the single-dimension version; LFU = LRU + frequency tiers.
- **LFUCache variants** (`src/main/kotlin/cache/LFUCacheGigaCHAD.kt`, `LfuCacheNobodyDoesItBetter.kt`) — the same design, different bookkeeping flavors.
- **Interview follow-up:** "Why is `min` maintained incrementally rather than recomputed?" `min` changes only in two ways: a `get` emptying the min bucket raises it by exactly one, and a fresh insert resets it to 1. Both are O(1) local updates — no scan over frequencies, which is what keeps `get`/`put` at O(1).
