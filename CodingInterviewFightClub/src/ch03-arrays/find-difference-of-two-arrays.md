# 3.44 Find Difference Of Two Arrays

> **Source**: [`src/main/kotlin/array/hashtable/FindDifferenceOfTwoArrays.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/hashtable/FindDifferenceOfTwoArrays.kt)
> **Pattern**: set subtraction · **Core page**

## The Problem

`[values only in nums1, values only in nums2]`.

- Constraints: n, m ≤ 1000.

## Examples

```
Input:  nums1 = [1,2,3], nums2 = [2,4,6]   -> Output: [[1,3],[4,6]]
```

## Intuition — set difference both ways

```kotlin
val set1 = nums1.toSet()
val set2 = nums2.toSet()

return listOf(
    set1.subtract(set2).toList(),
    set2.subtract(set1).toList()
)
```

## Approach 1 — Set subtraction (the repo's version, optimal)

```kotlin
class FindDifferenceOfTwoArrays {
    /**
     * @param nums1 first array
     * @param nums2 second array
     * @return      [only in 1, only in 2]
     */
    fun findDifference(nums1: IntArray, nums2: IntArray): List<List<Int>> {
        val set1 = nums1.toSet()
        val set2 = nums2.toSet()

        return listOf(set1.subtract(set2).toList(), set2.subtract(set1).toList())
    }
}
```

```java
import java.util.*;

public class FindDifferenceOfTwoArrays {
    /**
     * @param nums1 first array
     * @param nums2 second array
     * @return      [only in 1, only in 2]
     */
    public List<List<Integer>> findDifference(int[] nums1, int[] nums2) {
        Set<Integer> s1 = new HashSet<>();
        Set<Integer> s2 = new HashSet<>();
        for (int n : nums1) s1.add(n);
        for (int n : nums2) s2.add(n);

        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>(s1));
        result.add(new ArrayList<>(s2));
        result.get(0).removeAll(s2);
        result.get(1).removeAll(s1);
        return result;
    }
}
```

```cpp
#include <vector>
#include <unordered_set>

class FindDifferenceOfTwoArrays {
public:
    /**
     * @param nums1 first array
     * @param nums2 second array
     * @return      [only in 1, only in 2]
     */
    std::vector<std::vector<int>> findDifference(std::vector<int>& nums1, std::vector<int>& nums2) {
        std::unordered_set<int> s1(nums1.begin(), nums1.end());
        std::unordered_set<int> s2(nums2.begin(), nums2.end());

        std::vector<std::vector<int>> result(2);
        for (int n : s1) if (!s2.count(n)) result[0].push_back(n);
        for (int n : s2) if (!s1.count(n)) result[1].push_back(n);
        return result;
    }
};
```

```python
def find_difference(nums1: list[int], nums2: list[int]) -> list[list[int]]:
    """
    @param nums1: first array
    @param nums2: second array
    @return:      [only in 1, only in 2]
    """
    s1, s2 = set(nums1), set(nums2)
    return [list(s1 - s2), list(s2 - s1)]
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param nums1 first array
    /// @param nums2 second array
    /// @return      [only in 1, only in 2]
    pub fn find_difference(nums1: Vec<i32>, nums2: Vec<i32>) -> Vec<Vec<i32>> {
        let s1: HashSet<i32> = nums1.into_iter().collect();
        let s2: HashSet<i32> = nums2.into_iter().collect();

        vec![
            s1.difference(&s2).copied().collect(),
            s2.difference(&s1).copied().collect(),
        ]
    }
}
```

## Reading the code — what's actually happening

```kotlin
val set1 = nums1.toSet()
val set2 = nums2.toSet()
return listOf(set1.subtract(set2).toList(), set2.subtract(set1).toList())
```

The problem asks for two lists: values **only** in `nums1`, and values **only** in `nums2`. That's literally set difference, in both directions — and the `Set` data structure was built for exactly this.

- **`nums1.toSet()` and `nums2.toSet()` dedupe first.** The problem wants *distinct* values (e.g. `[1,1,2]` should contribute `1` once, not twice). Converting to sets collapses duplicates before any comparison happens — that's why we can't just scan the raw arrays.
- **`set1.subtract(set2)` keeps what's in `set1` but not in `set2`** — the "only in nums1" answer. Set subtraction is O(1) per element, so the whole operation is linear in the set sizes.
- **`set2.subtract(set1)` is the mirror image** — "only in nums2". Notice the asymmetry of the problem (there's no requirement that the answers be disjoint from *each other* beyond the obvious), and the two subtractions are independent.
- **Why is the set approach better than scanning?** A naive double loop would be O(n·m) and would need extra bookkeeping for duplicates. Sets turn both concerns into O(1) lookups and automatic dedup — O(n + m) total.

Trace `nums1 = [1,2,3], nums2 = [2,4,6]`: `set1 − set2 = {1,3}`, `set2 − set1 = {4,6}` → `[[1,3],[4,6]]` ✓.

## Dry run

**Input:** `nums1 = [1,2,3], nums2 = [2,4,6]`.

```
s1 = {1,2,3}, s2 = {2,4,6}.
only1 = {1,3}, only2 = {4,6}.
Output: [[1,3],[4,6]] ✓
```

## Complexity

**Time.** O(n + m):

$$
T(n, m) = O(n + m)
$$

**Space.** Two sets:

$$
S(n, m) = O(n + m)
$$

## Variants & follow-ups

- **Intersection Of Two Arrays** ([10.29](../ch10-hash-tables/intersection-of-two-arrays.md)) — the common-values twin.
- **Interview follow-up:** "Why sets first?" Duplicates would pollute the difference — sets dedupe before the subtraction, matching the problem's "distinct" requirement.
