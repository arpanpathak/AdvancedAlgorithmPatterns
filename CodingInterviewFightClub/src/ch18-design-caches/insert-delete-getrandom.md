# 18.7 Insert Delete GetRandom O(1)

> **Source:** [`src/main/kotlin/probability/InsertDeleteGetRandom.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/probability/InsertDeleteGetRandom.kt)
> **Pattern:** map + list swap-remove · **Core page**

## The Problem

Design a data structure supporting `insert(val)` (false if present), `remove(val)` (false if absent), and `getRandom()` — a uniformly random existing value — all in **O(1)**.

- Constraints: up to $2 \times 10^5$ operations; values are unique.

## Examples

```
RandomizedSet rs = new RandomizedSet();
rs.insert(1); rs.insert(2); rs.getRandom() -> 1 or 2 (uniform); rs.remove(1); rs.getRandom() -> 2
```

## Intuition — a map alone can't be random; a list alone can't delete in O(1); *together* they can

The two requirements pull in opposite directions:

- `getRandom()` in O(1) needs an **array** (random index);
- `remove(val)` in O(1) needs a **hash map** (locate the value).

The composition: keep the elements in a **list** (the array for random access) and a **map `value -> index`** (for O(1) location). The join is **swap-remove**: to delete `val`, swap it with the *last* element, then pop the back:

```
remove(val):
    index = map[val]                 # O(1) locate
    last = list.removeLast()
    if index < list.size:            # val wasn't already last
        list[index] = last           # the last element moves into the hole
        map[last] = index            # ...and its index follows
    map.remove(val)                  # drop val's entry
```

**Why swap-remove and not list-remove?** Removing from the middle of a list shifts everything right of it — O(n). Swapping with the last element turns the delete into a constant-time pop, and the map update is a single overwrite. The invariant "list is exactly the map's keys, compactly" is preserved by the swap.

**Why is `getRandom` uniform?** The list is dense (no holes — swap-remove guarantees it), so a random index picks each value with equal probability. A sparse list (with tombstones) would bias the sampling.

**Why does `insert` reject duplicates?** The map's presence check (`map[value]` exists) is the O(1) membership test; the list must not hold duplicates or the index map breaks.

## Approach 1 — Set + list without swap (O(n) remove)

A `HashSet` plus a list that does `list.remove(value)`: correct, but shifts O(n) per delete.

## Approach 2 — Map + swap-remove (the repo's version, optimal)

```kotlin
class InsertDeleteGetRandom {
    private val elements = mutableListOf<Int>()
    private val elementIndices = mutableMapOf<Int, Int>()

    /**
     * @param value candidate
     * @return      true if newly inserted, false if already present
     */
    fun insert(value: Int): Boolean {
        elementIndices[value]?.let { return false }          // already present
        elementIndices[value] = elements.size.also { elements.add(value) }
        return true
    }

    /**
     * @param value candidate
     * @return      true if removed, false if absent
     */
    fun remove(value: Int): Boolean {
        val index = elementIndices[value] ?: return false    // absent
        val lastElement = elements.removeLast()

        if (index < elements.size) {                         // value wasn't the last element
            elements[index] = lastElement                    // last element fills the hole
            elementIndices[lastElement] = index              // ...and its index follows
        }
        elementIndices.remove(value)
        return true
    }

    /** @return a uniformly random existing value */
    fun getRandom(): Int = elements.random()
}
```

```java
import java.util.*;

public class RandomizedSet {
    private final List<Integer> list = new ArrayList<>();
    private final Map<Integer, Integer> index = new HashMap<>();
    private final Random random = new Random();

    /**
     * @param val candidate
     * @return    true if newly inserted, false if already present
     */
    public boolean insert(int val) {
        if (index.containsKey(val)) return false;            // already present
        index.put(val, list.size());
        list.add(val);
        return true;
    }

    /**
     * @param val candidate
     * @return    true if removed, false if absent
     */
    public boolean remove(int val) {
        Integer pos = index.get(val);
        if (pos == null) return false;                       // absent

        int last = list.get(list.size() - 1);
        list.set(pos, last);                                 // last element fills the hole
        index.put(last, pos);                                // ...and its index follows
        list.remove(list.size() - 1);                        // pop the back
        index.remove(val);
        return true;
    }

    /** @return a uniformly random existing value */
    public int getRandom() {
        return list.get(random.nextInt(list.size()));
    }
}
```

```cpp
#include <cstdlib>
#include <unordered_map>
#include <vector>

class RandomizedSet {
    std::vector<int> list;
    std::unordered_map<int, int> index;

public:
    /**
     * @param val candidate
     * @return    true if newly inserted, false if already present
     */
    bool insert(int val) {
        if (index.count(val)) return false;                  // already present
        index[val] = list.size();
        list.push_back(val);
        return true;
    }

    /**
     * @param val candidate
     * @return    true if removed, false if absent
     */
    bool remove(int val) {
        if (!index.count(val)) return false;                 // absent

        int pos = index[val];
        int last = list.back();
        list[pos] = last;                                    // last element fills the hole
        index[last] = pos;                                   // ...and its index follows
        list.pop_back();                                     // pop the back
        index.erase(val);
        return true;
    }

    /** @return a uniformly random existing value */
    int getRandom() {
        return list[rand() % list.size()];
    }
};
```

```python
import random

class RandomizedSet:
    def __init__(self):
        self.list = []
        self.index = {}

    def insert(self, val: int) -> bool:
        """@return: true if newly inserted, false if already present"""
        if val in self.index:
            return False                     # already present
        self.index[val] = len(self.list)
        self.list.append(val)
        return True

    def remove(self, val: int) -> bool:
        """@return: true if removed, false if absent"""
        if val not in self.index:
            return False                     # absent
        pos = self.index[val]
        last = self.list[-1]
        self.list[pos] = last                # last element fills the hole
        self.index[last] = pos               # ...and its index follows
        self.list.pop()                      # pop the back
        del self.index[val]
        return True

    def get_random(self) -> int:
        """@return: a uniformly random existing value"""
        return random.choice(self.list)
```

```rust
use rand::Rng;
use std::collections::HashMap;

struct RandomizedSet {
    list: Vec<i32>,
    index: HashMap<i32, usize>,
}

impl RandomizedSet {
    fn new() -> Self { RandomizedSet { list: Vec::new(), index: HashMap::new() } }

    /// @return true if newly inserted, false if already present
    fn insert(&mut self, val: i32) -> bool {
        if self.index.contains_key(&val) { return false; }     // already present
        self.index.insert(val, self.list.len());
        self.list.push(val);
        true
    }

    /// @return true if removed, false if absent
    fn remove(&mut self, val: i32) -> bool {
        let Some(pos) = self.index.remove(&val) else { return false; };  // absent
        let last = *self.list.last().unwrap();
        self.list[pos] = last;                // last element fills the hole
        self.index.insert(last, pos);         // ...and its index follows
        self.list.pop();                      // pop the back
        true
    }

    /// @return a uniformly random existing value
    fn get_random(&self) -> i32 {
        let i = rand::thread_rng().gen_range(0..self.list.len());
        self.list[i]
    }
}
```

## Dry run

**Input:** the example sequence.

```
insert(1): index={1:0}, list=[1].
insert(2): index={1:0, 2:1}, list=[1,2].
getRandom(): random index in {0,1} -> 1 or 2, uniform.  (say 1)
remove(1): pos=0.  last=2.  list[0]=2 -> list=[2,2].  index[2]=0 -> index={2:0}.
           list.pop() -> list=[2].  index.remove(1).  -> true ✓
getRandom(): -> 2 (only element).
remove(1): 1 not in index -> false ✓
```

The swap-remove's critical case is `remove(1)` with `[1,2]`: the last element `2` moves into position 0 — overwriting the doomed value — and its map entry follows. The list stays dense (`[2]`, not `[_, 2]`), which is what keeps `getRandom` uniform and `insert`'s `list.size` index correct.

## Complexity

**Time.** All operations are O(1) (hash + array ops):

$$
T(n) = O(1) \text{ per operation}
$$

**Space.** The list + map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Insert Delete GetRandom With Duplicates** — the map becomes value → set of indices; the swap-remove extends with one more bookkeeping layer.
- **RandomizedCollection / Blacklist variants** — the same dense-list + map design with extra rules.
- **LRU Cache** ([18.1](lru-cache.md)) — the sibling "map + ordered structure" design; this page's list plays the role of LRU's linked list.
- **Interview follow-up:** "Why must the list stay dense?" `getRandom` samples a random *index* — uniform only if every index holds exactly one live value. Swap-remove guarantees density by always replacing a hole with the last element; any tombstone scheme would skew the sampling and is the classic wrong answer to name.
