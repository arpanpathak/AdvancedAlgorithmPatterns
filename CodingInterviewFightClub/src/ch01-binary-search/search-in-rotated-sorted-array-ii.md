# 1.16 Search In Rotated Sorted Array II

> **Source**: [`src/main/kotlin/binarysearch/SearchInRotatedArray_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/SearchInRotatedArray_II.kt)
> **Pattern**: rotated binary search with duplicates · **Core page**

## The Problem

Search in a **rotated** array that may contain **duplicates** (return existence).

- Constraints: n ≤ 5000.

## Examples

```
Input:  nums = [2,5,6,0,0,1,2], target = 0   -> Output: true
Input:  nums = [2,5,6,0,0,1,2], target = 3   -> Output: false
```

## Intuition — the rotated search; when `nums[mid] == nums[left]`, shrink

Duplicates break the rotation detection: `nums[left] == nums[mid]` can't tell which side is sorted. The fix: **narrow the range** (`left++`) and retry:

```kotlin
while (left <= right) {
    val mid = left + (right - left) / 2

    when {
        nums[mid] == target -> return true
        nums[left] == nums[mid] -> left++      // ambiguous: shrink
        nums[left] < nums[mid] -> {            // left half sorted
            if (target in nums[left]..nums[mid]) right = mid - 1
            else left = mid + 1
        }
        else -> {                              // right half sorted
            if (target in nums[mid]..nums[right]) left = mid + 1
            else right = mid - 1
        }
    }
}
return false
```

**Why `left++` on the tie?** Equal endpoints make both halves "look sorted" ambiguously — advancing one step removes a duplicate and re-tests. Worst case degrades to O(n) (all duplicates), but typical stays O(log n).

## Approach 1 — Binary search with ambiguity shrink (the repo's version, optimal)

```kotlin
class SearchInRotatedArray_II {
    /**
     * @param nums   rotated sorted array with duplicates
     * @param target search value
     * @return       true iff found
     */
    fun search(nums: IntArray, target: Int): Boolean {
        var left = 0
        var right = nums.lastIndex

        while (left <= right) {
            val mid = left + (right - left) / 2

            when {
                nums[mid] == target -> return true
                nums[left] == nums[mid] -> left++
                nums[left] < nums[mid] -> {
                    if (target in nums[left]..nums[mid]) right = mid - 1
                    else left = mid + 1
                }
                else -> {
                    if (target in nums[mid]..nums[right]) left = mid + 1
                    else right = mid - 1
                }
            }
        }
        return false
    }
}
```

```java
public class SearchInRotatedSortedArrayII {
    /**
     * @param nums   rotated sorted array with duplicates
     * @param target search value
     * @return       true iff found
     */
    public boolean search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return true;

            if (nums[left] == nums[mid]) left++;
            else if (nums[left] < nums[mid]) {
                if (target >= nums[left] && target <= nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (target >= nums[mid] && target <= nums[right]) left = mid + 1;
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
     * @param nums   rotated sorted array with duplicates
     * @param target search value
     * @return       true iff found
     */
    bool search(std::vector<int>& nums, int target) {
        int left = 0, right = nums.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return true;

            if (nums[left] == nums[mid]) left++;
            else if (nums[left] < nums[mid]) {
                if (target >= nums[left] && target <= nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (target >= nums[mid] && target <= nums[right]) left = mid + 1;
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
    @param nums:   rotated sorted array with duplicates
    @param target: search value
    @return:       true iff found
    """
    left, right = 0, len(nums) - 1

    while left <= right:
        mid = (left + right) // 2

        if nums[mid] == target:
            return True

        if nums[left] == nums[mid]:
            left += 1
        elif nums[left] < nums[mid]:
            if nums[left] <= target <= nums[mid]:
                right = mid - 1
            else:
                left = mid + 1
        else:
            if nums[mid] <= target <= nums[right]:
                left = mid + 1
            else:
                right = mid - 1

    return False
```

```rust
impl Solution {
    /// @param nums   rotated sorted array with duplicates
    /// @param target search value
    /// @return       true iff found
    pub fn search(nums: Vec<i32>, target: i32) -> bool {
        let (mut left, mut right) = (0, nums.len() - 1);

        while left <= right {
            let mid = left + (right - left) / 2;

            if nums[mid] == target { return true; }

            if nums[left] == nums[mid] { left += 1; }
            else if nums[left] < nums[mid] {
                if target >= nums[left] && target <= nums[mid] { right = mid - 1; }
                else { left = mid + 1; }
            } else {
                if target >= nums[mid] && target <= nums[right] { left = mid + 1; }
                else { right = mid - 1; }
            }
        }
        false
    }
}
```

## Dry run

**Input:** `nums = [2,5,6,0,0,1,2], target = 0`.

```
left=0, right=6.  mid=3 (0).  found -> true ✓

Input: [1,0,1,1,1], target = 0: mid=2 (1).  nums[0]==1 == mid -> left=1.
  mid=(1+4)/2=2 (1): nums[1]=0 != 1.  nums[1] < nums[2]: right-half check: 0 in [1..1]? no
  -> right=1.  mid=1 (0): found ✓
```

## Complexity

**Time.** O(log n) typical, O(n) worst (all duplicates):

$$
T(n) = O(\log n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Search In Rotated Sorted Array** ([1.17](search-in-rotated-sorted-array.md)) — the no-duplicates sibling.
- **Interview follow-up:** "Why can't duplicates keep it O(log n)?" An adversarial all-equal array forces the `left++` shrink to walk linearly — no comparison can distinguish. The problem accepts the O(n) worst case (existence only).
