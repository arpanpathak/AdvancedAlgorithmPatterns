# 1.5 Find Minimum In Rotated Sorted Array

> **Source:** [`src/main/kotlin/binarysearch/FindMinimumInRotatedSortedArray.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/FindMinimumInRotatedSortedArray.kt)
> **Pattern:** rotated-array pivot · **Core page**

## The Problem

A sorted array was rotated at some unknown pivot (e.g. `[0,1,2,4,5,6,7]` became `[4,5,6,7,0,1,2]`). All elements are **distinct**. Find the **minimum** element in $O(\log n)$.

- Constraints: $1 \le n \le 5000$, all values distinct.

## Examples

```
Input:  nums = [3, 4, 5, 1, 2]
Output: 1

Input:  nums = [4, 5, 6, 7, 0, 1, 2]
Output: 0

Input:  nums = [11, 13, 15, 17]
Output: 11        (rotation by 0 — array is still sorted)

Input:  nums = [2, 1]
Output: 1
```

## Intuition — the "two sorted runs" picture

A rotated sorted array is *two sorted runs glued together*:

```
[4, 5, 6, 7, | 0, 1, 2]
 \__run 1__/  \_run 2_/
               ^
            minimum = start of run 2
```

Everything in run 1 is **larger than everything** in run 2 (because the array was sorted before rotation). Now watch what happens at `nums[mid]`:

- If `nums[mid] > nums[right]`, then `mid` sits in run 1, and the minimum is strictly to its **right** → `left = mid + 1`.
- Otherwise (`nums[mid] < nums[right]`, the only other case since all values are distinct), `mid` sits in run 2, and the minimum is at `mid` or to its **left** → `right = mid`.

This is **Template A from [1.0](pattern-primer.md)** wearing a rotated costume: the predicate is "$nums[mid]$ is in run 2", which is monotone (run 1 first, then run 2 — never interleaved). We're finding the *first* element of run 2.

The edge `nums[mid] == nums[right]` cannot happen here (distinct values), which is exactly why the duplicate-tolerant variant ([1.16](search-in-rotated-sorted-array-ii.md)) needs extra work.

## Approach 1 — Linear scan

Scan for the first element smaller than its predecessor. $O(n)$ time. Works, but the problem's $O(\log n)$ requirement (and the sorted structure) demands the binary version.

## Approach 2 — Binary search on the pivot (optimal)

```kotlin
/**
 * @param nums the rotated sorted array (distinct values)
 * @return     the minimum element
 */
fun findMin(nums: IntArray): Int {
    var (left, right) = 0 to nums.size - 1

    while (left < right) {
        val mid = left + (right - left) / 2
        when {
            // mid is in the "big" left run -> the minimum is strictly to the right
            nums[mid] > nums[right] -> left = mid + 1
            // mid is in the "small" right run -> minimum is at or left of mid
            else                    -> right = mid
        }
    }
    return nums[left]
}
```

```java
public class FindMinimumInRotatedSortedArray {
    /**
     * @param nums the rotated sorted array (distinct values)
     * @return     the minimum element
     */
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;          // mid is in the big left run
            } else {
                right = mid;             // mid is in the small right run
            }
        }
        return nums[left];
    }
}
```

```cpp
#include <vector>

class FindMinimumInRotatedSortedArray {
public:
    /**
     * @param nums the rotated sorted array (distinct values)
     * @return     the minimum element
     */
    int findMin(const std::vector<int>& nums) {
        int left = 0, right = (int)nums.size() - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return nums[left];
    }
};
```

```python
def find_min(nums: list[int]) -> int:
    """
    @param nums: the rotated sorted array (distinct values)
    @return:     the minimum element
    """
    left, right = 0, len(nums) - 1
    while left < right:
        mid = left + (right - left) // 2
        if nums[mid] > nums[right]:
            left = mid + 1          # mid is in the big left run
        else:
            right = mid             # mid is in the small right run
    return nums[left]
```

```rust
impl Solution {
    /// @param nums the rotated sorted array (distinct values)
    /// @return     the minimum element
    pub fn find_min(nums: Vec<i32>) -> i32 {
        let (mut left, mut right) = (0usize, nums.len() - 1);
        while left < right {
            let mid = left + (right - left) / 2;
            if nums[mid] > nums[right] {
                left = mid + 1;          // mid is in the big left run
            } else {
                right = mid;             // mid is in the small right run
            }
        }
        nums[left]
    }
}
```

## Dry run

**Input:** `nums = [3, 4, 5, 1, 2]`

```
left=0  right=4  mid=2  nums[2]=5  nums[4]=2  5 > 2 -> left=3   (5 is in the big run; min is right of it)
left=3  right=4  mid=3  nums[3]=1  nums[4]=2  1 > 2? NO -> right=3
left=3  right=3  -> return nums[3] = 1 ✓
```

**Input:** `nums = [11, 13, 15, 17]` (no rotation)

```
left=0  right=3  mid=1  nums[1]=13  nums[3]=17  13 > 17? NO -> right=1
left=0  right=1  mid=0  nums[0]=11  nums[1]=13  11 > 13? NO -> right=0
return nums[0] = 11 ✓   (the "rotation by zero" case lands on the first element)
```

**Input:** `nums = [2, 1]` (rotation of a 2-element array)

```
left=0  right=1  mid=0  nums[0]=2  nums[1]=1  2 > 1 -> left=1
left=1  right=1  -> return nums[1] = 1 ✓
```

Why the `>` comparison against `nums[right]` (not `nums[left]`)? Compare with the "find the rotation point" version that tests `nums[mid] > nums[first]`. The `right`-anchored version is safe even when the array is *not rotated at all*: in a fully sorted array `nums[mid] > nums[right]` is always false, so `right` collapses leftward onto index 0 — the minimum. The `first`-anchored version would instead collapse toward the rotation point, which is index 0 too, but it needs an extra "did we rotate?" check. Anchoring on `right` is the cleaner invariant.

## Complexity

**Time.** Each iteration halves the window:

$$
T(n) = O(\log n)
$$

**Space.** $O(1)$ auxiliary.

## Variants & follow-ups

- **Search In Rotated Sorted Array** ([1.17](search-in-rotated-sorted-array.md)) — same two-run structure, but you must also decide *which run the target is in*.
- **Search In Rotated Sorted Array II** ([1.16](search-in-rotated-sorted-array-ii.md)) — duplicates break the `>`/`<` trichotomy; the fix is a dedup step that can degrade the worst case to $O(n)$.
- **Interview follow-up:** "Find the rotation index (not the value)." The same loop returns `left`, and the rotation index is `left` (0 if not rotated).
- **Interview follow-up:** "What changes with duplicates?" The `==` case (e.g. `[2,2,2,0,2]`) makes it impossible to know which run `mid` is in — see 1.16 for the standard handling.
