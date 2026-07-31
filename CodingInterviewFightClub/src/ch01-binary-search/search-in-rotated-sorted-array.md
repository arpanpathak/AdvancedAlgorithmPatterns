# 1.17 Search In Rotated Sorted Array

> **Source:** [`src/main/kotlin/binarysearch/SearchInRotatedSortedArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/SearchInRotatedSortedArray.kt)
> **Pattern:** rotated-array search · **Core page**

## The Problem

A sorted array of **distinct** integers was rotated at an unknown pivot. Given the rotated array and a `target`, return the **index** of `target`, or `-1` if absent. Must run in $O(\log n)$.

- Constraints: $1 \le n \le 5000$.

## Examples

```
Input:  nums = [4, 5, 6, 7, 0, 1, 2], target = 0
Output: 4

Input:  nums = [4, 5, 6, 7, 0, 1, 2], target = 3
Output: -1

Input:  nums = [1], target = 0
Output: -1
```

## Intuition — one binary search, not two

The naive plan is "find the pivot, then binary search the right run." That works ($O(\log n)$), but there's a cleaner formulation that does **one** binary search. The trick: at every step, **at least one half is fully sorted**, and we can always tell which one.

Compare `nums[mid]` with `nums[start]`:

- **`nums[start] <= nums[mid]` → the left half is sorted.** Then `target` is in the left half *iff* `nums[start] <= target < nums[mid]`. If yes, go left; if no, the target (if it exists) must be in the right half.
- **Otherwise → the right half is sorted** (the pivot lies inside the left half). Then `target` is in the right half *iff* `nums[mid] < target <= nums[end]`. If yes, go right; else go left.

Why is this correct? The two-run picture from [1.5](find-minimum-in-rotated-sorted-array.md): a rotated array is run 1 (big values) followed by run 2 (small values). If `nums[start] <= nums[mid]`, then start and mid are in the *same run* (you can't cross the pivot without values dropping), so `[start, mid]` is fully sorted and the standard "is target inside this sorted range?" test applies. Same argument mirrored for the right half.

This is still **Template B** (exact match) from [1.0](pattern-primer.md), with the sorted-range test replacing the plain comparison.

## Approach 1 — Find pivot, then binary search

1. Binary search for the rotation index (as in [1.5](find-minimum-in-rotated-sorted-array.md)).
2. Decide which run the target belongs to (compare with `nums[0]`).
3. Run a plain binary search on that run.

Correct, $O(\log n)$, but two passes and more edge cases to narrate. The one-pass version below is what interviewers want to hear.

## Approach 2 — Single-pass rotated binary search (optimal)

```kotlin
/**
 * @param nums   the rotated sorted array (distinct values)
 * @param target the value to find
 * @return       the index of target, or -1 if it is absent
 */
fun search(nums: IntArray, target: Int): Int {
    var (start, end) = 0 to nums.lastIndex

    while (start <= end) {
        val mid = start + (end - start) / 2

        if (nums[mid] == target) return mid

        // Left half [start..mid] is fully sorted.
        if (nums[start] <= nums[mid]) {
            if (nums[start] <= target && target < nums[mid]) {
                end = mid - 1          // target inside the sorted left half
            } else {
                start = mid + 1        // target must be in the right half
            }
        } else {
            // Right half [mid..end] is fully sorted.
            if (nums[mid] < target && target <= nums[end]) {
                start = mid + 1        // target inside the sorted right half
            } else {
                end = mid - 1          // target must be in the left half
            }
        }
    }
    return -1
}
```

```java
public class SearchInRotatedSortedArray {
    /**
     * @param nums   the rotated sorted array (distinct values)
     * @param target the value to find
     * @return       the index of target, or -1 if it is absent
     */
    public int search(int[] nums, int target) {
        int start = 0, end = nums.length - 1;
        while (start <= end) {
            int mid = start + (end - start) / 2;
            if (nums[mid] == target) return mid;

            if (nums[start] <= nums[mid]) {                 // left half sorted
                if (nums[start] <= target && target < nums[mid]) {
                    end = mid - 1;
                } else {
                    start = mid + 1;
                }
            } else {                                        // right half sorted
                if (nums[mid] < target && target <= nums[end]) {
                    start = mid + 1;
                } else {
                    end = mid - 1;
                }
            }
        }
        return -1;
    }
}
```

```cpp
#include <vector>

class SearchInRotatedSortedArray {
public:
    /**
     * @param nums   the rotated sorted array (distinct values)
     * @param target the value to find
     * @return       the index of target, or -1 if it is absent
     */
    int search(const std::vector<int>& nums, int target) {
        int start = 0, end = (int)nums.size() - 1;
        while (start <= end) {
            int mid = start + (end - start) / 2;
            if (nums[mid] == target) return mid;

            if (nums[start] <= nums[mid]) {                 // left half sorted
                if (nums[start] <= target && target < nums[mid]) {
                    end = mid - 1;
                } else {
                    start = mid + 1;
                }
            } else {                                        // right half sorted
                if (nums[mid] < target && target <= nums[end]) {
                    start = mid + 1;
                } else {
                    end = mid - 1;
                }
            }
        }
        return -1;
    }
};
```

```python
def search(nums: list[int], target: int) -> int:
    """
    @param nums:   the rotated sorted array (distinct values)
    @param target: the value to find
    @return:       the index of target, or -1 if it is absent
    """
    start, end = 0, len(nums) - 1
    while start <= end:
        mid = start + (end - start) // 2
        if nums[mid] == target:
            return mid

        if nums[start] <= nums[mid]:            # left half sorted
            if nums[start] <= target < nums[mid]:
                end = mid - 1
            else:
                start = mid + 1
        else:                                   # right half sorted
            if nums[mid] < target <= nums[end]:
                start = mid + 1
            else:
                end = mid - 1
    return -1
```

```rust
impl Solution {
    /// @param nums   the rotated sorted array (distinct values)
    /// @param target the value to find
    /// @return       the index of target, or -1 if it is absent
    pub fn search(nums: Vec<i32>, target: i32) -> i32 {
        let (mut start, mut end) = (0usize, nums.len() - 1);
        while start <= end {
            let mid = start + (end - start) / 2;
            if nums[mid] == target {
                return mid as i32;
            }

            if nums[start] <= nums[mid] {               // left half sorted
                if nums[start] <= target && target < nums[mid] {
                    if mid == 0 { break; }
                    end = mid - 1;
                } else {
                    start = mid + 1;
                }
            } else {                                    // right half sorted
                if nums[mid] < target && target <= nums[end] {
                    start = mid + 1;
                } else {
                    if mid == 0 { break; }
                    end = mid - 1;
                }
            }
        }
        -1
    }
}
```

> **Rust note:** `usize` cannot go below zero, so the `mid == 0` guards replace the implicit `-1` underflow of the other languages; breaking out of the loop is equivalent to "window exhausted, not found".

## Dry run

**Input:** `nums = [4, 5, 6, 7, 0, 1, 2]`, `target = 0`

```
start=0  end=6  mid=3  nums[3]=7 != 0
  nums[0]=4 <= 7  -> left half [4,5,6,7] is sorted
  is 4 <= 0 < 7? NO -> target must be right -> start=4
start=4  end=6  mid=5  nums[5]=1 != 0
  nums[4]=0 <= 1  -> left half [0,1] is sorted
  is 0 <= 0 < 1? YES -> end=4
start=4  end=4  mid=4  nums[4]=0 == 0 -> return 4 ✓
```

**Input:** `nums = [4, 5, 6, 7, 0, 1, 2]`, `target = 3`

```
start=0  end=6  mid=3  nums[3]=7 != 3
  left half sorted; is 4 <= 3 < 7? NO -> start=4
start=4  end=6  mid=5  nums[5]=1 != 3
  nums[4]=0 <= 1 -> left half [0,1] sorted; is 0 <= 3 < 1? NO -> start=6
start=6  end=6  mid=6  nums[6]=2 != 3
  nums[6]=2 <= nums[6]=2 -> left half [2] sorted; is 2 <= 3 < 2? NO -> start=7
start=7  end=6  -> loop ends, return -1 ✓
```

The key property to verify by hand: at every step, the branch we take is *forced* — the sorted-half test is guaranteed to be correct for one of the two halves, so the window always shrinks and never discards the target's location.

## Complexity

**Time.** The window halves every iteration:

$$
T(n) = O(\log n)
$$

**Space.** $O(1)$ auxiliary.

## Variants & follow-ups

- **Search In Rotated Sorted Array II** ([1.16](search-in-rotated-sorted-array-ii.md)) — duplicates make `nums[start] == nums[mid]` possible, which breaks the "which half is sorted" test; the fix costs $O(n)$ in the worst case.
- **Find Minimum In Rotated Sorted Array** ([1.5](find-minimum-in-rotated-sorted-array.md)) — the pivot-finding half of this problem, standalone.
- **Interview follow-up:** "Can we find the pivot with this same loop?" Yes — remove the equality return and the sorted-range test, keep the `nums[mid] > nums[end]` comparison, and the loop converges to the pivot. That's 1.5 verbatim.
- **Interview follow-up:** "Why `nums[start] <= nums[mid]` with `<=`?" With distinct values it never matters, but the `<=` makes the code identical to the duplicate-tolerant version's sorted-half test — one less thing to rewrite if the interviewer adds duplicates.
