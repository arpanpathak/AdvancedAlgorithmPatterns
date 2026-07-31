# 1.3 Find First And Last Position Of Target

> **Source:** [`src/main/kotlin/binarysearch/FindFirstAndLastPosition.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/FindFirstAndLastPosition.kt)
> **Pattern:** lower bound + upper bound · **Core page**

## The Problem

Given a **sorted** array `nums` (may contain duplicates) and a target value, return the index of the **first** occurrence and the index of the **last** occurrence. If the target does not appear, return `[-1, -1]`.

- Constraints: $0 \le n \le 10^5$, values and target fit in `Int`.

## Examples

```
Input:  nums = [5, 7, 7, 8, 8, 10], target = 8
Output: [3, 4]

Input:  nums = [5, 7, 7, 8, 8, 10], target = 6
Output: [-1, -1]

Input:  nums = [], target = 0
Output: [-1, -1]
```

## Intuition

A naive linear scan finds the first occurrence in $O(n)$ — fine once, but this is the *primitive* out of which heavier machinery is built (count of a value = last − first + 1; range queries in sorted data; binary-search-based data structures). The sorted structure gives us a much sharper tool.

The key insight: **the boundary trick works on both sides of the target.**

- **First occurrence = "first true" for the predicate $P_1(x) = nums[x] \ge target$.** Before the first `8`, all elements are `< 8` (false); from the first `8` onward, all elements are `>= 8` (true). The boundary is exactly the first 8.
- **Last occurrence = "last true" for the predicate $P_2(x) = nums[x] \le target$**, i.e. the first index where the *opposite* predicate $nums[x] > target$ becomes true, minus 1.

Both boundaries are found by the same machinery from [1.0](pattern-primer.md) — Template B with a twist: when we hit `target`, we **don't stop**; we keep shrinking the window toward the side we care about. That's the whole trick:

- to find the *first* occurrence: on a hit, `right = mid - 1` (keep looking left),
- to find the *last* occurrence: on a hit, `left = mid + 1` (keep looking right),
- and we remember the last hit in `result` — when the window empties, `result` holds the boundary.

## Approach 1 — Linear scan

Scan left to right for the first match, then right to left for the last. $O(n)$ time, $O(1)$ space. Correct but ignores the sorted structure entirely; if the interviewer then asks "what if `nums` has a billion entries", you want Approach 2.

## Approach 2 — Two binary searches (optimal)

```kotlin
/**
 * @param nums   the sorted array (may contain duplicates)
 * @param target the value whose range of occurrences to find
 * @return       an IntArray [firstIndex, lastIndex], or [-1, -1] if target is absent
 */
fun searchRange(nums: IntArray, target: Int): IntArray {
    val result = intArrayOf(-1, -1)

    // Find the first occurrence: keep shrinking right on hits.
    result[0] = binarySearch(nums, target, findFirst = true)
    // If the first occurrence is missing, the whole range is missing.
    if (result[0] == -1) return result

    // Find the last occurrence: keep growing left on hits.
    result[1] = binarySearch(nums, target, findFirst = false)
    return result
}

/**
 * @param nums      the sorted array
 * @param target    the value to locate
 * @param findFirst when true, return the leftmost occurrence; when false, the rightmost
 * @return          the requested boundary index, or -1 if target is absent
 */
private fun binarySearch(nums: IntArray, target: Int, findFirst: Boolean): Int {
    var left = 0
    var right = nums.lastIndex
    var result = -1

    while (left <= right) {
        val mid = left + (right - left) / 2
        when {
            nums[mid] == target -> {
                result = mid
                // Narrow the window past the hit, toward the side we want.
                if (findFirst) right = mid - 1  // hunt for an earlier equal element
                else            left = mid + 1  // hunt for a later equal element
            }
            nums[mid] < target -> left = mid + 1
            else                -> right = mid - 1
        }
    }
    return result
}
```

```java
public class FindFirstAndLastPosition {
    /**
     * @param nums   the sorted array (may contain duplicates)
     * @param target the value whose range of occurrences to find
     * @return       int[] {firstIndex, lastIndex}, or {-1, -1} if target is absent
     */
    public int[] searchRange(int[] nums, int target) {
        int[] result = {-1, -1};
        result[0] = binarySearch(nums, target, true);
        if (result[0] == -1) return result;
        result[1] = binarySearch(nums, target, false);
        return result;
    }

    /**
     * @param nums      the sorted array
     * @param target    the value to locate
     * @param findFirst true for leftmost occurrence, false for rightmost
     * @return          the requested boundary index, or -1 if absent
     */
    private int binarySearch(int[] nums, int target, boolean findFirst) {
        int left = 0, right = nums.length - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                result = mid;
                if (findFirst) right = mid - 1;
                else           left = mid + 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }
}
```

```cpp
#include <vector>

class FindFirstAndLastPosition {
public:
    /**
     * @param nums   the sorted array (may contain duplicates)
     * @param target the value whose range of occurrences to find
     * @return       {firstIndex, lastIndex}, or {-1, -1} if target is absent
     */
    std::vector<int> searchRange(const std::vector<int>& nums, int target) {
        std::vector<int> result = {-1, -1};
        result[0] = binarySearch(nums, target, true);
        if (result[0] == -1) return result;
        result[1] = binarySearch(nums, target, false);
        return result;
    }

private:
    /**
     * @param nums      the sorted array
     * @param target    the value to locate
     * @param findFirst true for leftmost occurrence, false for rightmost
     * @return          the requested boundary index, or -1 if absent
     */
    int binarySearch(const std::vector<int>& nums, int target, bool findFirst) {
        int left = 0, right = (int)nums.size() - 1, result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                result = mid;
                if (findFirst) right = mid - 1;
                else           left = mid + 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }
};
```

```python
def search_range(nums: list[int], target: int) -> list[int]:
    """
    @param nums:   the sorted array (may contain duplicates)
    @param target: the value whose range of occurrences to find
    @return:       [firstIndex, lastIndex], or [-1, -1] if target is absent
    """
    first = binary_search(nums, target, find_first=True)
    if first == -1:
        return [-1, -1]
    last = binary_search(nums, target, find_first=False)
    return [first, last]


def binary_search(nums: list[int], target: int, find_first: bool) -> int:
    """
    @param nums:       the sorted array
    @param target:     the value to locate
    @param find_first: True for leftmost occurrence, False for rightmost
    @return:           the requested boundary index, or -1 if absent
    """
    left, right, result = 0, len(nums) - 1, -1
    while left <= right:
        mid = left + (right - left) // 2
        if nums[mid] == target:
            result = mid
            if find_first:
                right = mid - 1
            else:
                left = mid + 1
        elif nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return result
```

```rust
impl Solution {
    /// @param nums   the sorted array (may contain duplicates)
    /// @param target the value whose range of occurrences to find
    /// @return       [firstIndex, lastIndex], or [-1, -1] if target is absent
    pub fn search_range(nums: Vec<i32>, target: i32) -> Vec<i32> {
        let first = Self::binary_search(&nums, target, true);
        if first == -1 {
            return vec![-1, -1];
        }
        let last = Self::binary_search(&nums, target, false);
        vec![first, last]
    }

    /// @param nums       the sorted array
    /// @param target     the value to locate
    /// @param find_first true for leftmost occurrence, false for rightmost
    /// @return           the requested boundary index, or -1 if absent
    fn binary_search(nums: &[i32], target: i32, find_first: bool) -> i32 {
        if nums.is_empty() {
            return -1;
        }
        let (mut left, mut right, mut result) = (0usize, nums.len() - 1, -1i32);
        while left <= right {
            let mid = left + (right - left) / 2;
            if nums[mid] == target {
                result = mid as i32;
                if find_first {
                    if mid == 0 { break; }
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else if nums[mid] < target {
                left = mid + 1;
            } else {
                if mid == 0 { break; }
                right = mid - 1;
            }
        }
        result
    }
}
```

> **Note on the Rust version:** `usize` can't go negative, so when `mid == 0` and we need to move `right` below it, we break out — the search window is exhausted and `result` already holds the best boundary found. This is the one place where Rust's type system forces a slightly different (but equivalent) control flow than the other four languages.

## Dry run

**Input:** `nums = [5, 7, 7, 8, 8, 10]`, `target = 8`. First, the "find first" pass:

```
left=0  right=5  mid=2  nums[2]=7 < 8  -> left=3
left=3  right=5  mid=4  nums[4]=8 == 8 -> result=4, findFirst -> right=3
left=3  right=3  mid=3  nums[3]=8 == 8 -> result=3, findFirst -> right=2
left=3  right=2  -> loop ends. return 3    (first occurrence) ✓
```

Then the "find last" pass:

```
left=0  right=5  mid=2  nums[2]=7 < 8  -> left=3
left=3  right=5  mid=4  nums[4]=8 == 8 -> result=4, findLast -> left=5
left=5  right=5  mid=5  nums[5]=10 > 8 -> right=4
left=5  right=4  -> loop ends. return 4    (last occurrence) ✓
```

Both passes examine $\lceil \log_2 6 \rceil = 3$ elements each — six probes total versus six for a linear scan on this tiny input, and the gap widens exponentially as `n` grows.

**Edge cases:** `nums = []` → `right = -1`, loop never runs, `result = -1` → `[-1, -1]`. Target smaller than everything → both passes return -1. Target larger than everything → same.

## Complexity

**Time.** Each of the two passes halves the window, so each costs $O(\log n)$; total:

$$
T(n) = 2 \cdot \lceil \log_2(n+1) \rceil = O(\log n)
$$

**Space.** A handful of scalars: $O(1)$ auxiliary.

## Variants & follow-ups

- **First Bad Version** ([1.8](first-bad-version.md)) is the same "find the boundary" search with an *oracle* predicate instead of an array.
- **Count of occurrences** of `target` in a sorted array = `last - first + 1` — one extra subtraction on top of this page.
- **Kth Missing Positive Number** ([1.11](kth-missing-positive-number.md)) reuses the "first true" boundary idea on a counting predicate.
- **Interview follow-up:** "How would you find the index where the array stops being ≤ target?" — that's literally the `findLast` pass; the boundary style of thinking transfers to *any* monotone predicate, which is the theme of [1.0](pattern-primer.md).
