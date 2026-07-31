# 10.6 Design HashMap

> **Source:** [`src/main/kotlin/hashtable/DesignHashMap.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/DesignHashMap.kt)
> **Pattern:** open addressing · **Core page**

## The Problem

Design a hash map with `put(key, value)`, `get(key)`, and `remove(key)`, mapping `int` keys to `int` values. Keys are in `[0, 10^6]`; up to $10^4$ calls. All operations should average $O(1)$.

- Constraints: keys are **non-negative** and at most $10^6$.

## Examples

```
MyHashMap map = new MyHashMap();
map.put(1, 1);     map.put(2, 2);
map.get(1);   -> 1
map.get(3);   -> -1   (not present)
map.put(2, 1);       (update)
map.get(2);   -> 1
map.remove(2);
map.get(2);   -> -1
```

## Intuition — a hash map is an array plus a *decision about collisions*

The simplest possible hash table: an **array** where a key's position is `key % capacity` — the *hash function*. Lookup is then "check that one slot" — $O(1)$ — provided nothing else lives there.

The entire design question is **what happens when two different keys hash to the same slot** (a collision). The repo's choice — given the constraint that keys are non-negative and ≤ $10^6$ — is the extreme case: an array of size $10^6 + 1$ indexed *directly* by the key. There are **no collisions possible** because the hash is the identity. That's not a cop-out; it's the textbook open-addressing endpoint where "load factor 0" is achieved by sizing the table to the full key universe.

**The general design (what the interview actually wants):**

1. **Hash function** — `key % capacity` for ints; pick a prime capacity to spread keys evenly.
2. **Collision handling — open addressing**: if slot `key % capacity` is taken, probe forward (`+1`, or `+1², +2²...` for quadratic) to the next empty slot. `get`/`remove` walk the same probe sequence until they find the key, an empty slot (key absent), or the table end.
3. **The `remove` subtlety**: you can't just clear a slot — a probe sequence that passed through it would then terminate early and miss keys stored *after* it. The standard fix: mark removed slots with a tombstone (a special "deleted" value) that `get` skips but `put` reuses. (The repo sidesteps this entirely via the collision-free array.)
4. **Load factor / resize** — when slots fill, probing degrades; double the table and rehash everything to restore amortized $O(1)$.

**Why a "Pair + null-check" at each slot?** When the array is keyed by `key % capacity` (not the raw key), the slot alone doesn't identify the key — you must store the key alongside the value and verify `map[slot].first == key` before trusting the value. The repo's `get` checks exactly this; forgetting it is the classic bug (two keys sharing a slot would return each other's values).

## Approach 1 — Direct-indexed array (the repo's version, collision-free for this key range)

```kotlin
class MyHashMap() {
    private val map = Array<Pair<Int, Int>?>(1000000) { null }   // slot per possible key

    /**
     * @param key   non-negative key
     * @param value value to store
     */
    fun put(key: Int, value: Int) {
        val index = key % map.size          // identity hash: key <= 10^6 < 10^6+1... (see note)
        map[index] = Pair(key, value)
    }

    /**
     * @param key non-negative key
     * @return    stored value, or -1 if absent
     */
    fun get(key: Int): Int {
        val index = key % map.size

        return if (map[index] != null && map[index]?.first == key) {   // verify the key, not just the slot
            map[index]?.second ?: -1
        } else {
            -1
        }
    }

    /**
     * @param key non-negative key
     */
    fun remove(key: Int) {
        val index = key % map.size
        if (map[index]?.first == key) {
            map[index] = null
        }
    }
}
```

> **Repo note — the honest sizing story:** the repo allocates `Array(1000000)`, which covers keys `0..999999` directly; with the constraint `key <= 10^6`, one extra slot (`10^6 + 1`) makes the hash the pure identity. The `key % map.size` keeps the modulo for safety. This is the "the constraint IS the design" trick — but the *interview* version below implements a general hash table with real collision handling.

## Approach 2 — Chaining with linked buckets (the general interview answer)

```java
import java.util.*;

class MyHashMapGeneral {
    private static final int SIZE = 10007;              // prime: spreads keys evenly
    private List<int[]>[] buckets = new List[SIZE];

    private int hash(int key) { return key % SIZE; }

    /** @param key non-negative key */
    public void put(int key, int value) {
        int h = hash(key);
        if (buckets[h] == null) buckets[h] = new ArrayList<>();
        for (int[] pair : buckets[h]) {
            if (pair[0] == key) { pair[1] = value; return; }   // update existing
        }
        buckets[h].add(new int[]{key, value});                  // append new
    }

    /** @param key non-negative key */
    public int get(int key) {
        int h = hash(key);
        if (buckets[h] == null) return -1;
        for (int[] pair : buckets[h]) {
            if (pair[0] == key) return pair[1];
        }
        return -1;
    }

    /** @param key non-negative key */
    public void remove(int key) {
        int h = hash(key);
        if (buckets[h] == null) return;
        buckets[h].removeIf(pair -> pair[0] == key);
    }
}
```

```cpp
#include <list>
#include <vector>

class MyHashMap {
    static constexpr int SIZE = 10007;                  // prime: spreads keys evenly
    std::vector<std::list<std::pair<int, int>>> buckets{SIZE};

    int hash(int key) { return key % SIZE; }

public:
    /** @param key non-negative key */
    void put(int key, int value) {
        int h = hash(key);
        for (auto& [k, v] : buckets[h]) {
            if (k == key) { v = value; return; }        // update existing
        }
        buckets[h].push_back({key, value});             // append new
    }

    /** @param key non-negative key */
    int get(int key) {
        int h = hash(key);
        for (auto& [k, v] : buckets[h]) {
            if (k == key) return v;
        }
        return -1;
    }

    /** @param key non-negative key */
    void remove(int key) {
        int h = hash(key);
        buckets[h].remove_if([&](const auto& p) { return p.first == key; });
    }
};
```

```python
class MyHashMap:
    """@param key: non-negative key"""

    def __init__(self):
        self.size = 10007                       # prime: spreads keys evenly
        self.buckets = [[] for _ in range(self.size)]   # chaining

    def _hash(self, key: int) -> int:
        return key % self.size

    def put(self, key: int, value: int) -> None:
        bucket = self.buckets[self._hash(key)]
        for i, (k, _) in enumerate(bucket):
            if k == key:
                bucket[i] = (key, value)        # update existing
                return
        bucket.append((key, value))             # append new

    def get(self, key: int) -> int:
        for k, v in self.buckets[self._hash(key)]:
            if k == key:
                return v
        return -1

    def remove(self, key: int) -> None:
        bucket = self.buckets[self._hash(key)]
        for i, (k, _) in enumerate(bucket):
            if k == key:
                bucket.pop(i)
                return
```

```rust
struct MyHashMap {
    buckets: Vec<Vec<(i32, i32)>>,        // chaining
}

impl MyHashMap {
    fn new() -> Self {
        MyHashMap { buckets: vec![Vec::new(); 10007] }   // prime size
    }

    fn hash(&self, key: i32) -> usize { key as usize % self.buckets.len() }

    /// @param key non-negative key
    fn put(&mut self, key: i32, value: i32) {
        let h = self.hash(key);
        for pair in &mut self.buckets[h] {
            if pair.0 == key { pair.1 = value; return; }   // update existing
        }
        self.buckets[h].push((key, value));                // append new
    }

    /// @param key non-negative key
    fn get(&self, key: i32) -> i32 {
        for &(k, v) in &self.buckets[self.hash(key)] {
            if k == key { return v; }
        }
        -1
    }

    /// @param key non-negative key
    fn remove(&mut self, key: i32) {
        let h = self.hash(key);
        self.buckets[h].retain(|&(k, _)| k != key);
    }
}
```

## Dry run

**Input (chaining version):** `put(1,1)`, `put(2,2)`, `get(1)`, `put(2,1)`, `get(2)`, `remove(2)`, `get(2)`.

```
hash(1) = 1 % 10007 = 1;  hash(2) = 2
put(1,1): bucket[1] empty -> append (1,1).        buckets[1]=[(1,1)]
put(2,2): bucket[2] empty -> append (2,2).        buckets[2]=[(2,2)]
get(1):  scan bucket[1]: (1,1) matches -> 1 ✓
put(2,1): bucket[2] has (2,2) -> update to (2,1). buckets[2]=[(2,1)]
get(2):  -> 1 ✓
remove(2): bucket[2] -> remove (2,1).             buckets[2]=[]
get(2):  bucket[2] empty -> -1 ✓
```

The chaining behavior under collisions: keys `10007` and `0` both hash to bucket 0 — the bucket's list holds both, and `get` scans the (short) list comparing keys. The key-comparison step is what distinguishes a bucket hit from a collision: without `pair.first == key`, `get(10007)` would return `0`'s value.

## Complexity

**Time.** $O(1)$ average — the bucket lists stay short because the prime size spreads keys:

$$
T_{\text{avg}} = O(1), \qquad T_{\text{worst}} = O(n) \text{ (all keys collide)}
$$

**Space.** The table plus stored pairs:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Design HashSet** — the same structure with only keys; `removeIf`-style cleanup gets simpler.
- **LRU Cache / LFU Cache** (`src/main/kotlin/cache/`) — hash map + linked list / frequency buckets: the map is the O(1) lookup half, and the *second* structure (list / heap) provides the eviction order the map can't. The "map + structure" capstone.
- **Maximum Frequency Stack** (`src/main/kotlin/hashtable/MaximumFrequencyStack.kt`) — a map of stacks; the "design" muscle applied to a stack with extra semantics.
- **Interview follow-up:** "Open addressing vs chaining?" Chaining (linked buckets) handles any load factor gracefully and avoids the tombstone complexity; open addressing is cache-friendlier but needs load-factor management and tombstones for `remove`. The repo's direct-indexed array is the degenerate case where neither matters because the key range is fully allocated. Saying all three tiers shows command of the design space.
