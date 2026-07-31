# 10.16 Design HashMap

> **Source:** [`src/main/kotlin/hashtable/DesignHashMap.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/DesignHashMap.kt)
> **Pattern:** open addressing with probing · **Core page**

## The Problem

Implement `put`, `get`, `remove` with no built-in hash map.

- Constraints: ≤ 10⁴ calls; keys in `[0, 10⁶]`.

## Examples

```
["MyHashMap","put","put","get","get","put","get","remove","get"]
[[],[1,1],[2,2],[1],[3],[2,1],[2],[2],[2]]
-> [null,null,null,1,-1,null,null,null,-1]
```

## Intuition — an array of slots, index by key, probe on collision

The simplest valid design: a fixed array; `index = key % size`; on collision, **probe forward** for an empty slot (open addressing). Store `(key, value)` pairs so `get`/`remove` can verify identity:

```kotlin
class MyHashMap() {
    private val map = Array<Pair<Int, Int>?>(1000000) { null }

    fun put(key: Int, value: Int) {
        val index = key % map.size
        map[index] = Pair(key, value)
    }

    fun get(key: Int): Int {
        val index = key % map.size
        return if (map[index] != null && map[index]?.first == key) {
            map[index]?.second ?: -1
        } else -1
    }
}
```

**Why the `Pair` and identity check?** `key % size` collides different keys — the slot must store *which* key it holds, and `get` must verify `first == key` before trusting the value. Without the key, a collision returns the wrong value.

**Why open addressing and not chaining?** At ≤ 10⁴ calls with a 10⁶-sized array, collisions are rare and probing finds slots fast — the minimal honest design. The [10.0](pattern-primer.md) chaining variant (buckets of linked lists) is the production answer; this file's huge-array approach is the "keys are bounded" shortcut.

## Approach 1 — Bucket chaining (production style)

`Array<MutableList<Pair<Int, Int>>>` with hash + modulo: the interview-standard robust design.

## Approach 2 — Open addressing (the repo's version)

```kotlin
class MyHashMap() {
    private val map = Array<Pair<Int, Int>?>(1000000) { null }

    /**
     * @param key   hash key
     * @param value value to store
     */
    fun put(key: Int, value: Int) {
        val index = key % map.size
        map[index] = Pair(key, value)
    }

    /**
     * @param key hash key
     * @return    stored value or -1
     */
    fun get(key: Int): Int {
        val index = key % map.size

        return if (map[index] != null && map[index]?.first == key) {
            map[index]?.second ?: -1
        } else {
            -1
        }
    }

    /**
     * @param key hash key to remove
     */
    fun remove(key: Int) {
        val index = key % map.size
        if (map[index]?.first == key) {
            map[index] = null
        }
    }
}
```

```java
public class MyHashMap {
    private final int[] keys;
    private final int[] values;

    public MyHashMap() {
        keys = new int[1_000_001];          // key bounds
        values = new int[1_000_001];
    }

    /**
     * @param key   hash key
     * @param value value to store
     */
    public void put(int key, int value) {
        keys[key] = 1;                      // mark present
        values[key] = value;
    }

    /**
     * @param key hash key
     * @return    stored value or -1
     */
    public int get(int key) {
        return keys[key] == 1 ? values[key] : -1;
    }

    /**
     * @param key hash key to remove
     */
    public void remove(int key) {
        keys[key] = 0;
    }
}
```

```cpp
#include <vector>

class MyHashMap {
    std::vector<int> keys;
    std::vector<int> values;

public:
    MyHashMap() : keys(1000001, 0), values(1000001, 0) {}

    /**
     * @param key   hash key
     * @param value value to store
     */
    void put(int key, int value) {
        keys[key] = 1;                      // mark present
        values[key] = value;
    }

    /**
     * @param key hash key
     * @return    stored value or -1
     */
    int get(int key) {
        return keys[key] ? values[key] : -1;
    }

    /**
     * @param key hash key to remove
     */
    void remove(int key) {
        keys[key] = 0;
    }
};
```

```python
class MyHashMap:
    """open addressing over a fixed array"""

    def __init__(self):
        self.table = [None] * 1000001       # key bounds

    def put(self, key: int, value: int) -> None:
        self.table[key] = value             # direct index

    def get(self, key: int) -> int:
        v = self.table[key]
        return v if v is not None else -1

    def remove(self, key: int) -> None:
        self.table[key] = None
```

```rust
struct MyHashMap {
    table: Vec<i32>,
}

impl MyHashMap {
    fn new() -> Self { Self { table: vec![-1; 1_000_001] } }

    /// @param key   hash key
    /// @param value value to store
    fn put(&mut self, key: i32, value: i32) {
        self.table[key as usize] = value;
    }

    /// @param key hash key
    /// @return    stored value or -1
    fn get(&self, key: i32) -> i32 {
        self.table[key as usize]
    }

    /// @param key hash key to remove
    fn remove(&mut self, key: i32) {
        self.table[key as usize] = -1;
    }
}
```

## Dry run

**Input:** `put(1,1); put(2,2); get(1); get(3); put(2,1); get(2); remove(2); get(2)`.

```
put(1,1): table[1 % size] = (1,1).
put(2,2): table[2] = (2,2).
get(1):   table[1].first == 1 ✓ -> 1 ✓
get(3):   table[3] null -> -1 ✓
put(2,1): table[2] = (2,1).
get(2):   table[2].first == 2 -> 1 ✓
remove(2): table[2].first == 2 -> table[2] = null.
get(2):   null -> -1 ✓
```

The identity check is the whole correctness story: `get(2)` after `put(2,1)` must return 1 — the `first == key` guard ensures the slot's key matches before the value is trusted. Without the key stored, a collided slot would answer with the wrong key's value. The `% size` indexing plus the Pair makes collisions detectable; the Java/C++ `keys` sentinel array is the same idea with parallel arrays.

## Complexity

**Time.** O(1) per op (no probing in practice at this scale):

$$
T = O(1)
$$

**Space.** The fixed table:

$$
S = O(1\,000\,000) = O(1)
$$

## Variants & follow-ups

- **Insert Delete GetRandom** ([18.8](../ch18-design-caches/insert-delete-getrandom.md)) — the map + array design with O(1) random.
- **Design A Stack With Increment Operations** ([18.7](../ch18-design-caches/design-a-stack-with-increment-operations.md)) — the design-family sibling.
- **Interview follow-up:** "Why open addressing here and chaining in production?" Keys are bounded to 10⁶ — a direct-index array is O(1) with zero collision handling. Production maps need arbitrary keys, so hashing + chaining (or probing with load-factor resizing) replaces the direct index. The design choice follows the key universe: bounded → direct, unbounded → hash.
