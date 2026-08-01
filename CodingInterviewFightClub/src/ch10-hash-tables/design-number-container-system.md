# 10.31 Design Number Container System

> **Source**: [`src/main/kotlin/hashtable/DesignANumberContainerSystem.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/DesignANumberContainerSystem.kt)
> **Pattern**: index→number + number→sorted-indices · **Core page**

## The Problem

`change(index, number)` and `find(number)` → the smallest index holding it.

- Constraints: ≤ 2×10⁵ calls.

## Examples

```
["NumberContainers","change","find","change","find"]
[[],[1,10],[10],[1,20],[10]]
-> [null,null,1,null,-1]
```

## Intuition — two maps: index→number, number→sorted set of indices

```kotlin
private val numberToIndices: MutableMap<Int, SortedSet<Int>> = mutableMapOf()
private val indexToNumber: MutableMap<Int, Int> = mutableMapOf()

fun change(index: Int, number: Int) {
    indexToNumber[index]?.let { previousNumber ->
        numberToIndices[previousNumber]?.remove(index)
    }

    indexToNumber[index] = number
    numberToIndices.getOrPut(number) { sortedSetOf() }.add(index)
}

fun find(number: Int): Int = numberToIndices[number]?.firstOrNull() ?: -1
```

## Approach 1 — Dual maps (the repo's version, optimal)

```kotlin
class DesignANumberContainerSystem {
    private val numberToIndices: MutableMap<Int, SortedSet<Int>> = mutableMapOf()
    private val indexToNumber: MutableMap<Int, Int> = mutableMapOf()

    /**
     * @param index  position
     * @param number new value
     */
    fun change(index: Int, number: Int) {
        indexToNumber[index]?.let { previousNumber ->
            numberToIndices[previousNumber]?.remove(index)
        }

        indexToNumber[index] = number
        numberToIndices.getOrPut(number) { sortedSetOf() }.add(index)
    }

    /**
     * @param number search value
     * @return       smallest index holding it or -1
     */
    fun find(number: Int): Int = numberToIndices[number]?.firstOrNull() ?: -1
}
```

```java
import java.util.*;

public class NumberContainers {
    private final Map<Integer, Integer> indexToNumber = new HashMap<>();
    private final Map<Integer, TreeSet<Integer>> numberToIndices = new HashMap<>();

    /**
     * @param index  position
     * @param number new value
     */
    public void change(int index, int number) {
        if (indexToNumber.containsKey(index)) {
            int old = indexToNumber.get(index);
            numberToIndices.get(old).remove(index);
        }

        indexToNumber.put(index, number);
        numberToIndices.computeIfAbsent(number, k -> new TreeSet<>()).add(index);
    }

    /**
     * @param number search value
     * @return       smallest index holding it or -1
     */
    public int find(int number) {
        TreeSet<Integer> set = numberToIndices.get(number);
        return set == null || set.isEmpty() ? -1 : set.first();
    }
}
```

```cpp
#include <unordered_map>
#include <set>

class NumberContainers {
    std::unordered_map<int, int> indexToNumber;
    std::unordered_map<int, std::set<int>> numberToIndices;

public:
    /**
     * @param index  position
     * @param number new value
     */
    void change(int index, int number) {
        if (indexToNumber.count(index)) {
            numberToIndices[indexToNumber[index]].erase(index);
        }

        indexToNumber[index] = number;
        numberToIndices[number].insert(index);
    }

    /**
     * @param number search value
     * @return       smallest index holding it or -1
     */
    int find(int number) {
        auto it = numberToIndices.find(number);
        if (it == numberToIndices.end() || it->second.empty()) return -1;
        return *it->second.begin();
    }
};
```

```python
from sortedcontainers import SortedSet
# or a plain set + heap; the standard solution uses heap + lazy deletion


class NumberContainers:
    def __init__(self):
        self.index_to_number = {}
        self.number_to_indices = {}

    def change(self, index: int, number: int) -> None:
        if index in self.index_to_number:
            old = self.index_to_number[index]
            self.number_to_indices[old].discard(index)

        self.index_to_number[index] = number
        self.number_to_indices.setdefault(number, set()).add(index)

    def find(self, number: int) -> int:
        s = self.number_to_indices.get(number)
        return min(s) if s else -1
```

```rust
use std::collections::{HashMap, BTreeSet};

struct NumberContainers {
    index_to_number: HashMap<i32, i32>,
    number_to_indices: HashMap<i32, BTreeSet<i32>>,
}

impl NumberContainers {
    fn new() -> Self {
        Self { index_to_number: HashMap::new(), number_to_indices: HashMap::new() }
    }

    /// @param index  position
    /// @param number new value
    fn change(&mut self, index: i32, number: i32) {
        if let Some(&old) = self.index_to_number.get(&index) {
            if let Some(set) = self.number_to_indices.get_mut(&old) {
                set.remove(&index);
            }
        }

        self.index_to_number.insert(index, number);
        self.number_to_indices.entry(number).or_default().insert(index);
    }

    /// @param number search value
    /// @return       smallest index holding it or -1
    fn find(&self, number: i32) -> i32 {
        self.number_to_indices.get(&number)
            .and_then(|s| s.first().copied())
            .unwrap_or(-1)
    }
}
```

## Dry run

**Input:** the example.

```
change(1,10): index 1 -> 10.  find(10): {1} -> 1 ✓.
change(1,20): remove 1 from 10's set.  find(10): empty -> -1 ✓.
```

## Complexity

**Time.** O(log n) per op:

$$
T = O(\log n)
$$

**Space.** Two maps:

$$
S = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why the sorted set?" `find` needs the *smallest* index — a sorted structure keeps the min at the front without a scan.
