# 1.16 Search In Rotated Sorted Array II

> **Source:** [`src/main/kotlin/binarysearch/SearchInRotatedArray_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/SearchInRotatedArray_II.kt)
> **Pattern:** rotated-array search + duplicate dedup · **Variant page**

## The Problem

Same as [1.17](search-in-rotated-sorted-array.md) — search a rotated sorted array for a `target` — but now the array **may contain duplicates**. Return `true`/`false`.

- Constraints: $1 \le n \le 5000$.

## Examples

```
Input:  nums = [2, 5, 6, 0, 0, 1, 2], target = 0
Output: true

Input:  nums = [2, 5, 6, 0, 0, 1, 2], target = 3
Output: false

The killer case for the naive approach:
Input:  nums = [1, 0, 1, 1, 1], target = 0     (a "rotated" array where start == mid == end)
Output: true
```

## Intuition — what breaks, and the one-line fix

In [1.17](search-in-rotated-sorted-array.md) the sorted-half test was `nums[start] <= nums[mid]`. With duplicates, the equality case `nums[start] == nums[mid] == nums[end]` can occur even when the range *isn't* sorted:

```
nums = [1, 0, 1, 1, 1], start=0, end=4, mid=2
nums[0] == nums[2] == nums[4] == 1
```

The left half `[1,0,1]` is **not** sorted, yet `nums[start] <= nums[mid]` is true — the test lies. We cannot decide which half is sorted, so we cannot discard anything confidently.

**The fix:** when `nums[left] == nums[mid] == nums[right]`, shrink the window from *both* ends — `left++`, `right--` — discarding two elements we *know* are equal to the target-agnostic value. This restores the sorted-half test. The cost: in the worst case (e.g. all elements equal) the loop degenerates to a scan, so worst-case time becomes $O(n)$ — that's provably unavoidable (any algorithm must distinguish `[1,1,...,1]` from `[1,1,...,0,...,1]`, which forces examining the array).

## Approach 1 — The duplicate-tolerant binary search (optimal)

```kotlin
/**
 * @param nums   the rotated sorted array (duplicates allowed)
 * @param target the value to find
 * @return       true iff target occurs in nums
 */
fun search(nums: IntArray, target: Int): Boolean {
    var left = 0
    var right = nums.lastIndex

    while (left <= right) {
        val mid = left + (right - left) / 2

        when {
            nums[mid] == target -> return true

            // All three equal: cannot decide which half is sorted. Shrink both ends.
            nums[left] == nums[mid] && nums[mid] == nums[right] -> {
                left++
                right--
            }

            // Left portion is sorted (the honest test now)
            nums[left] <= nums[mid] -> {
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1    // target in the sorted left portion
                } else {
                    left = mid + 1     // target in the right portion
                }
            }

            // Right portion is sorted
            else -> {
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1     // target in the sorted right portion
                } else {
                    right = mid - 1    // target in the left portion
                }
            }
        }
    }
    return false
}
```

```java
public class SearchInRotatedSortedArrayII {
    /**
     * @param nums   the rotated sorted array (duplicates allowed)
     * @param target the value to find
     * @return       true iff target occurs in nums
     */
    public boolean search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return true;

            if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                left++;
                right--;                          // dedup: can't decide which half is sorted
            } else if (nums[left] <= nums[mid]) { // left half sorted
                if (nums[left] <= target && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {                              // right half sorted
                if (nums[mid] < target && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return false;
    }
}
```

```cpp
#include <vector>

class SearchInRotatedSortedArrayII {
public:
    /**
     * @param nums   the rotated sorted array (duplicates allowed)
     * @param target the value to find
     * @return       true iff target occurs in nums
     */
    bool search(const std::vector<int>& nums, int target) {
        int left = 0, right = (int)nums.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return true;

            if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                left++;
                right--;
            } else if (nums[left] <= nums[mid]) {
                if (nums[left] <= target && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (nums[mid] < target && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return false;
    }
};
```

```python
def search(nums: list[int], target: int) -> bool:
    """
    @param nums:   the rotated sorted array (duplicates allowed)
    @param target: the value to find
    @return:       True iff target occurs in nums
    """
    left, right = 0, len(nums) - 1
    while left <= right:
        mid = left + (right - left) // 2
        if nums[mid] == target:
            return True

        if nums[left] == nums[mid] == nums[right]:
            left += 1
            right -= 1                 # dedup: cannot decide which half is sorted
        elif nums[left] <= nums[mid]:  # left half sorted
            if nums[left] <= target < nums[mid]:
                right = mid - 1
            else:
                left = mid + 1
        else:                          # right half sorted
            if nums[mid] < target <= nums[right]:
                left = mid + 1
            else:
                right = mid - 1
    return False
```

```rust
impl Solution {
    /// @param nums   the rotated sorted array (duplicates allowed)
    /// @param target the value to find
    /// @return       true iff target occurs in nums
    pub fn search(nums: Vec<i32>, target: i32) -> bool {
        let (mut left, mut right) = (0usize, nums.len().wrapping_sub(1));
        while left <= right {
            let mid = left + (right - left) / 2;
            if nums[mid] == target {
                return true;
            }

            if nums[left] == nums[mid] && nums[mid] == nums[right] {
                left += 1;
                if right == 0 { break; }
                right -= 1;                       // dedup: cannot decide which half is sorted
            } else if nums[left] <= nums[mid] {   // left half sorted
                if nums[left] <= target && target < nums[mid] {
                    if mid == 0 { break; }
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {                              // right half sorted
                if nums[mid] < target && target <= nums[right] {
                    left = mid + 1;
                } else {
                    if mid == 0 { break; }
                    right = mid - 1;
                }
            }
        }
        false
    }
}
```

> **Rust note:** the `wrapping_sub` + `break` guards handle the empty/underflow cases idiomatically for `usize`; on an empty input the loop body never executes (left=0, right=usize::MAX, `nums[left]` would panic — the LeetCode harness guarantees non-empty input, but guard with `if nums.is_empty()` when adapting).

## Dry run — the killer case

**Input:** `nums = [1, 0, 1, 1, 1]`, `target = 0`

```
left=0  right=4  mid=2  nums[2]=1 != 0
  nums[0]=1 == nums[2]=1 == nums[4]=1  -> dedup: left=1, right=3
left=1  right=3  mid=2  nums[2]=1 != 0
  nums[1]=0 <= 1  -> left half [0,1] sorted; is 0 <= 0 < 1? YES -> right=1
left=1  right=1  mid=1  nums[1]=0 == 0 -> return true ✓
```

Without the dedup branch, the first iteration would have taken the "left half sorted" branch with `nums[left]=1 <= nums[mid]=1`, tested `1 <= 0 < 1` → false, and *discarded the left half containing the 0*. That is the bug the dedup step exists to prevent.

**Input:** `nums = [1, 1, 1, 1]`, `target = 0` (all equal)

```
Every iteration hits the dedup branch: left++ right--.
left=0 right=3 -> left=1 right=2 -> left=2 right=1 -> loop ends -> false ✓
```

This is the $O(n)$ worst case: the algorithm degenerates to examining the whole array one element at a time. Provably unavoidable.

## Complexity

**Best/average:** $O(\log n)$. **Worst:** $O(n)$ — when the dedup branch fires repeatedly (all-equal or mostly-equal inputs), the window shrinks by 1, not by half. The lower bound argument: distinguishing `[1,1,...,1]` from `[1,1,...,0,1]` requires reading enough elements to find the possible 0, which is $\Omega(n)$ in the worst case. So the code is **optimal for the problem as stated**.

**Space:** $O(1)$.

## Variants & follow-ups

- **[1.17](search-in-rotated-sorted-array.md)** — the no-duplicates version, where the dedup branch can never fire and the guarantee is a clean $O(\log n)$.
- **Find Minimum In Rotated Sorted Array II** — the same dedup trick applied to [1.5](find-minimum-in-rotated-sorted-array.md); worst case also $O(n)$.
- **Interview follow-up:** "Why can't we keep $O(\log n)$?" Because the adversary can make every probe read an element equal to its neighbors, forcing you to look further; no comparison-based algorithm can do better (adversary argument).
