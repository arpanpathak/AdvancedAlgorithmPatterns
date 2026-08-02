# 10.29 Intersection Of Two Arrays

> **Source**: [`src/main/kotlin/hashtable/IntersectionOfTwoArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/hashtable/IntersectionOfTwoArray.kt)
> **Pattern**: set intersection · **Core page**

## The Problem

The **distinct** values in both arrays.

- Constraints: n, m ≤ 1000.

## Examples

```
Input:  nums1 = [1,2,2,1], nums2 = [2,2]   -> Output: [2]
Input:  nums1 = [4,9,5], nums2 = [9,4,9,8,4] -> Output: [9,4]
```

## Intuition — set the first, filter the second, dedupe

```kotlin
val first = nums1.toMutableSet()
val intersectionSet = mutableSetOf<Int>()

for (num in nums2) {
    if (first.contains(num)) {
        intersectionSet.add(num)
    }
}
return intersectionSet.toIntArray()
```

The output set dedupes repeated hits (e.g. 2 appearing twice in nums2).

## Approach 1 — Set intersection (the repo's version, optimal)

## Approach 2 — Sort + two pointers (O(n log n), O(1) space)

Sort both; walk with the merge two-pointer, adding equal pairs.

```kotlin
class IntersectionOfTwoArray {
    /**
     * @param nums1 first array
     * @param nums2 second array
     * @return      distinct common values
     */
    fun intersection(nums1: IntArray, nums2: IntArray): IntArray {
        val first = nums1.toMutableSet()
        val intersectionSet = mutableSetOf<Int>()

        for (num in nums2) {
            if (first.contains(num)) {
                intersectionSet.add(num)
            }
        }
        return intersectionSet.toIntArray()
    }
}
```

```java
import java.util.*;

public class IntersectionOfTwoArrays {
    /**
     * @param nums1 first array
     * @param nums2 second array
     * @return      distinct common values
     */
    public int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> first = new HashSet<>();
        for (int num : nums1) first.add(num);

        Set<Integer> result = new HashSet<>();
        for (int num : nums2) {
            if (first.contains(num)) result.add(num);
        }

        int[] out = new int[result.size()];
        int i = 0;
        for (int num : result) out[i++] = num;
        return out;
    }
}
```

```cpp
#include <vector>
#include <unordered_set>

class IntersectionOfTwoArrays {
public:
    /**
     * @param nums1 first array
     * @param nums2 second array
     * @return      distinct common values
     */
    std::vector<int> intersection(std::vector<int>& nums1, std::vector<int>& nums2) {
        std::unordered_set<int> first(nums1.begin(), nums1.end());
        std::unordered_set<int> result;

        for (int num : nums2) {
            if (first.count(num)) result.insert(num);
        }
        return std::vector<int>(result.begin(), result.end());
    }
};
```

```python
def intersection(nums1: list[int], nums2: list[int]) -> list[int]:
    """
    @param nums1: first array
    @param nums2: second array
    @return:      distinct common values
    """
    return list(set(nums1) & set(nums2))
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param nums1 first array
    /// @param nums2 second array
    /// @return      distinct common values
    pub fn intersection(nums1: Vec<i32>, nums2: Vec<i32>) -> Vec<i32> {
        let first: HashSet<i32> = nums1.into_iter().collect();
        nums2.into_iter().collect::<HashSet<_>>()
            .into_iter().filter(|n| first.contains(n)).collect()
    }
}
```

## Reading the code — what's actually happening

```kotlin
val first = nums1.toMutableSet()
val intersectionSet = mutableSetOf<Int>()
for (num in nums2) {
    if (first.contains(num)) {
        intersectionSet.add(num)
    }
}
return intersectionSet.toIntArray()
```

The strategy has three deliberate moves — **set the first, filter the second, dedupe the hits**:

- **`nums1.toMutableSet()` builds the lookup table.** We want to answer "is this value in `nums1`?" for every element of `nums2` — sets give O(1) membership. Converting `nums1` also kills its duplicates (`[1,2,2,1]` → `{1,2}`), so we never double-count a value that appears twice in the first array.
- **The loop filters `nums2` through that table.** For each element, `first.contains(num)` is the yes/no test. We don't need to touch `nums1` again — every answer is a lookup.
- **`intersectionSet.add(num)` is the dedup.** `nums2` itself may repeat values (like `[2,2]`), and the problem wants *distinct* common values. A set is idempotent — adding `2` twice stores it once. Using a set here instead of a list is what guarantees the distinct output for free.
- **The two-set shape is O(n + m)** — build once, then one linear scan with O(1) checks. The sort-based alternative (Approach 2) is O(n log n + m log m) but uses no extra space; the set version trades memory for speed, which is the right call for interview-scale inputs.

Trace `nums1 = [1,2,2,1], nums2 = [2,2]`: `first = {1,2}`; loop: `2` in first → add; second `2` → already in the result set → skipped. Output `[2]` ✓.

## Dry run

**Input:** `nums1 = [1,2,2,1], nums2 = [2,2]`.

```
first = {1,2}.  nums2: 2 in first -> add.  2 again -> already there.
Output: [2] ✓
```

## Complexity

**Time.** One pass:

$$
T(n, m) = O(n + m)
$$

**Space.** Two sets:

$$
S(n, m) = O(n)
$$

## Variants & follow-ups

- **Intersection Of Two Arrays II** — the counting version (multiset, frequencies).
- **Interview follow-up:** "Why the result set?" nums2 can contain duplicates — the output must be distinct, so hits are collected into a set before conversion.
