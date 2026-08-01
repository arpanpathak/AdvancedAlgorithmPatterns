# 3.46 Unique Number Of Occurrences

> **Source**: [`src/main/kotlin/array/hashtable/UniqueNumberOfOccurences.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/UniqueNumberOfOccurences.kt)
> **Pattern**: frequency-set distinctness · **Core page**

## The Problem

Do all values appear a **unique** number of times?

- Constraints: n ≤ 1000.

## Examples

```
Input:  arr = [1,2,2,1,1,3]   -> Output: true  (3, 2, 1 all distinct)
Input:  arr = [1,2]           -> Output: false (both once)
```

## Intuition — the frequency multiset must have no duplicates

```kotlin
val map = mutableMapOf<Int, Int>()
for (num in arr) {
    map[num] = map.getOrPut(num) { 1 } + 1
}
return map.values.toSet().size == map.size
```

## Approach 1 — Frequency-set check (the repo's version, optimal)

```kotlin
class UniqueNumberOfOccurences {
    /**
     * @param arr input array
     * @return    true iff all frequencies are distinct
     */
    fun uniqueOccurrences(arr: IntArray): Boolean {
        val map = mutableMapOf<Int, Int>()

        for (num in arr) {
            map[num] = map.getOrPut(num) { 1 } + 1
        }
        return map.values.toSet().size == map.size
    }
}
```

```java
import java.util.*;

public class UniqueNumberOfOccurrences {
    /**
     * @param arr input array
     * @return    true iff all frequencies are distinct
     */
    public boolean uniqueOccurrences(int[] arr) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int num : arr) count.put(num, count.getOrDefault(num, 0) + 1);

        return new HashSet<>(count.values()).size() == count.size();
    }
}
```

```cpp
#include <vector>
#include <unordered_map>
#include <unordered_set>

class UniqueNumberOfOccurrences {
public:
    /**
     * @param arr input array
     * @return    true iff all frequencies are distinct
     */
    bool uniqueOccurrences(std::vector<int>& arr) {
        std::unordered_map<int, int> count;
        for (int num : arr) count[num]++;

        std::unordered_set<int> freqs;
        for (auto& [_, f] : count) freqs.insert(f);
        return freqs.size() == count.size();
    }
};
```

```python
def unique_occurrences(arr: list[int]) -> bool:
    """
    @param arr: input array
    @return:    true iff all frequencies are distinct
    """
    from collections import Counter
    freq = Counter(arr)
    return len(set(freq.values())) == len(freq)
```

```rust
use std::collections::{HashMap, HashSet};

impl Solution {
    /// @param arr input array
    /// @return    true iff all frequencies are distinct
    pub fn unique_occurrences(arr: Vec<i32>) -> bool {
        let mut count: HashMap<i32, i32> = HashMap::new();
        for num in arr { *count.entry(num).or_insert(0) += 1; }

        let freqs: HashSet<i32> = count.values().copied().collect();
        freqs.len() == count.len()
    }
}
```

## Dry run

**Input:** `arr = [1,2,2,1,1,3]`.

```
counts: 1→3, 2→2, 3→1.  values {3,2,1} size 3 == 3 -> true ✓
Input: [1,2]: counts {1,1}.  set size 1 != 2 -> false ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Maps:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does set-size == map-size decide it?" The set dedupes frequencies — if any frequency repeats, the set shrinks below the value count.
