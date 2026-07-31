# 10.13 Unique Number Of Occurrences

> **Source:** [`src/main/kotlin/array/hashtable/UniqueNumberOfOccurences.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/UniqueNumberOfOccurences.kt)
> **Pattern:** frequency map + set-size check · **Core page**

## The Problem

Return `true` if the **number of occurrences** of each value in `arr` is itself unique (no two values appear the same number of times).

- Constraints: $1 \le n \le 1000$; values fit in `Int`.

## Examples

```
Input:  arr = [1,2,2,1,1,3]   -> Output: true   (counts 3, 2, 1 — all distinct)
Input:  arr = [1,2]           -> Output: false  (both appear once)
```

## Intuition — count everything, then ask "are the counts distinct?"

Two steps, each one idiom:

1. **Count** — `map[num] = map.getOrPut(num) { 0 } + 1` (the counting one-liner from [10.12](rank-transform-of-an-array.md));
2. **Distinctness** — `map.values.toSet().size == map.size`: if any two counts collide, the set shrinks below the map's size.

**Why is the set-size comparison the whole test?** A `Set` contains each element once. `values` has one entry per *distinct value*; `toSet()` dedupes the *counts*. If all counts are unique, both sizes are equal; any repeat count shrinks the set. No loop, no second map.

## Approach 1 — Frequency map + set (the repo's version, optimal)

```kotlin
class UniqueNumberOfOccurences {
    /**
     * @param arr input array
     * @return    true iff every frequency occurs exactly once
     */
    fun uniqueOccurrences(arr: IntArray): Boolean {
        val map = mutableMapOf<Int, Int>()

        for (num in arr) {
            map[num] = map.getOrPut(num) { 1 } + 1     // count with a one-liner
        }

        return map.values.toSet().size == map.size     // all counts distinct?
    }
}
```

```java
import java.util.*;

public class UniqueNumberOfOccurrences {
    /**
     * @param arr input array
     * @return    true iff every frequency occurs exactly once
     */
    public boolean uniqueOccurrences(int[] arr) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int num : arr) count.merge(num, 1, Integer::sum);

        return count.values().stream().distinct().count() == count.size();
    }
}
```

```cpp
#include <unordered_map>
#include <unordered_set>
#include <vector>

class UniqueNumberOfOccurrences {
public:
    /**
     * @param arr input array
     * @return    true iff every frequency occurs exactly once
     */
    bool uniqueOccurrences(std::vector<int>& arr) {
        std::unordered_map<int, int> count;
        for (int num : arr) count[num]++;

        std::unordered_set<int> freq;
        for (auto& [_, c] : count) freq.insert(c);
        return freq.size() == count.size();
    }
};
```

```python
def unique_occurrences(arr: list[int]) -> bool:
    """
    @param arr: input array
    @return:    true iff every frequency occurs exactly once
    """
    from collections import Counter
    counts = Counter(arr)
    return len(set(counts.values())) == len(counts)
```

```rust
use std::collections::{HashMap, HashSet};

impl Solution {
    /// @param arr input array
    /// @return    true iff every frequency occurs exactly once
    pub fn unique_occurrences(arr: Vec<i32>) -> bool {
        let mut count: HashMap<i32, i32> = HashMap::new();
        for num in arr { *count.entry(num).or_insert(0) += 1; }

        let freq: HashSet<i32> = count.values().copied().collect();
        freq.len() == count.len()
    }
}
```

## Dry run

**Input:** `arr = [1,2,2,1,1,3]`.

```
counts: 1 -> 3, 2 -> 2, 3 -> 1.   (the getOrPut one-liner builds this)
values = [3,2,1].  toSet() = {3,2,1} (size 3).  map size = 3.

3 == 3 -> true ✓

Input: arr = [1,2]: counts 1 -> 1, 2 -> 1.  values = [1,1].  toSet() = {1} (size 1).
1 != 2 -> false ✓
```

The set-size comparison is the whole logic: in the true case every count is a different number, so dedupe changes nothing; in the false case the repeated count collapses the set. No loops beyond the initial counting pass.

## Complexity

**Time.** One counting pass:

$$
T(n) = O(n)
$$

**Space.** The count map + set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Rank Transform Of An Array** ([10.12](rank-transform-of-an-array.md)) — the same count-map family; rank sharing instead of count distinctness.
- **Degree Of An Array** (`array/hashtable/DegreeOfAnArray.kt`) — the frequency map with first/last positions attached.
- **Interview follow-up:** "Why compare set-size to map-size instead of a second loop?" `values.toSet().size == map.size` IS the distinctness check — a set's cardinality equals its source size exactly when no elements repeat. It's the declarative form of "no two counts are equal."
