# 1.20 Valley Element

> **Source:** [`src/main/kotlin/binarysearch/ValleyElement.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/ValleyElement.kt)
> **Pattern:** monotone slope descent (mirror) · **Variant page**

## The Problem

A **valley** (local minimum) in an array is an element strictly smaller than both its neighbors, with the boundaries treated as $+\infty$ (`Int.MAX_VALUE`). The repository ships **two** variants:

- `findValleyElementBinary` — returns the *value* of any valley,
- `findValley` — returns the *value* of any valley with a slightly different branch ordering.

Return the value of **any** valley, or `null`/`-1` for an empty array.

## Examples

```
Input:  nums = [5, 3, 1, 2, 4]      -> Output: 1   (the minimum, and a valley)
Input:  nums = [5, 4, 3, 2, 1]      -> Output: 1   (monotone decreasing; last element is a valley: right neighbor = +∞)
Input:  nums = [1, 2, 3, 4, 5]      -> Output: 1   (monotone increasing; first element is a valley: left neighbor = +∞)
```

## Intuition — mirror of the peak

[1.6](find-peak-element.md) climbs toward a peak; this problem descends toward a valley. The slope test is flipped:

- `nums[mid] > nums[mid + 1]` — the array is *falling* at `mid` → a valley exists strictly to the **right** → `left = mid + 1`.
- otherwise (`nums[mid] < nums[mid + 1]`) — rising at `mid` → a valley exists at `mid` or to its **left** → `right = mid`.

The correctness argument is the mirror of the peak argument: a falling prefix either reaches the last element (a valley because its right neighbor is $+\infty$) or turns upward (the turn is a valley). One of those must happen, so the right half provably contains a valley.

The boundary-safe version reads both neighbors with `Int.MAX_VALUE` sentinels and can early-return on a direct hit — exactly the pattern of [1.7](find-peak-element-safe.md).

## Approach — valley-descent binary search

```kotlin
/**
 * @param nums the input array (boundaries treated as +infinity)
 * @return     the value of any valley element, or null if nums is empty
 */
fun findValleyElementBinary(nums: IntArray): Int? {
    if (nums.isEmpty()) return null

    var (left, right) = 0 to nums.size

    while (left < right) {
        val mid = left + (right - left) / 2

        // Sentinel reads: outside the array counts as +infinity.
        val leftNeighbor  = if (mid > 0) nums[mid - 1] else Int.MAX_VALUE
        val rightNeighbor = if (mid < nums.size - 1) nums[mid + 1] else Int.MAX_VALUE

        when {
            // Direct hit: strictly smaller than both neighbors.
            nums[mid] < leftNeighbor && nums[mid] < rightNeighbor -> return nums[mid]
            // Still falling to the right -> valley is strictly to the right.
            nums[mid] > rightNeighbor -> left = mid + 1
            // Rising to the left -> valley is at or to the left.
            else -> right = mid
        }
    }
    return null // Unreachable for a valid non-empty array
}

/**
 * @param arr the input array (boundaries treated as +infinity)
 * @return    the value of any valley element, or null if arr is empty
 */
fun findValley(arr: IntArray): Int? {
    if (arr.isEmpty()) return null
    if (arr.size == 1) return arr[0]

    var low = 0
    var high = arr.size - 1
    while (low <= high) {
        val mid = low + (high - low) / 2
        val leftVal  = if (mid > 0) arr[mid - 1] else Int.MAX_VALUE
        val rightVal = if (mid < arr.size - 1) arr[mid + 1] else Int.MAX_VALUE

        when {
            arr[mid] <= leftVal && arr[mid] <= rightVal -> return arr[mid]
            leftVal < arr[mid] -> high = mid - 1   // moving toward the descending slope
            else               -> low = mid + 1
        }
    }
    return null
}
```

```java
public class ValleyElement {
    /**
     * @param nums the input array (boundaries treated as +infinity)
     * @return     the value of any valley element, or -1 if nums is empty
     */
    public int findValleyElementBinary(int[] nums) {
        if (nums.length == 0) return -1;
        int left = 0, right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            int leftNeighbor  = mid > 0 ? nums[mid - 1] : Integer.MAX_VALUE;
            int rightNeighbor = mid < nums.length - 1 ? nums[mid + 1] : Integer.MAX_VALUE;
            if (nums[mid] < leftNeighbor && nums[mid] < rightNeighbor) return nums[mid];
            if (nums[mid] > rightNeighbor) left = mid + 1;
            else right = mid;
        }
        return -1;
    }
}
```

```cpp
#include <vector>
#include <climits>

class ValleyElement {
public:
    /**
     * @param nums the input array (boundaries treated as +infinity)
     * @return     the value of any valley element, or -1 if nums is empty
     */
    int findValleyElementBinary(const std::vector<int>& nums) {
        if (nums.empty()) return -1;
        int left = 0, right = (int)nums.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            int leftNeighbor  = mid > 0 ? nums[mid - 1] : INT_MAX;
            int rightNeighbor = mid < (int)nums.size() - 1 ? nums[mid + 1] : INT_MAX;
            if (nums[mid] < leftNeighbor && nums[mid] < rightNeighbor) return nums[mid];
            if (nums[mid] > rightNeighbor) left = mid + 1;
            else right = mid;
        }
        return -1;
    }
};
```

```python
def find_valley(nums: list[int]) -> int:
    """
    @param nums: the input array (boundaries treated as +infinity)
    @return:     the value of any valley element, or -1 if nums is empty
    """
    if not nums:
        return -1
    left, right = 0, len(nums)
    while left < right:
        mid = left + (right - left) // 2
        left_neighbor  = nums[mid - 1] if mid > 0 else float("inf")
        right_neighbor = nums[mid + 1] if mid < len(nums) - 1 else float("inf")
        if nums[mid] < left_neighbor and nums[mid] < right_neighbor:
            return nums[mid]
        if nums[mid] > right_neighbor:
            left = mid + 1
        else:
            right = mid
    return -1
```

```rust
impl Solution {
    /// @param nums the input array (boundaries treated as +infinity)
    /// @return     the value of any valley element, or -1 if nums is empty
    pub fn find_valley(nums: Vec<i32>) -> i32 {
        if nums.is_empty() {
            return -1;
        }
        let (mut left, mut right) = (0usize, nums.len());
        while left < right {
            let mid = left + (right - left) / 2;
            let left_neighbor  = if mid > 0 { nums[mid - 1] } else { i32::MAX };
            let right_neighbor = if mid + 1 < nums.len() { nums[mid + 1] } else { i32::MAX };
            if nums[mid] < left_neighbor && nums[mid] < right_neighbor {
                return nums[mid];
            }
            if nums[mid] > right_neighbor {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        -1
    }
}
```

## Dry run

**Input:** `nums = [5, 3, 1, 2, 4]`

```
left=0  right=5  mid=2  nums[2]=1, L=3, R=2
  1 < 3 && 1 < 2? YES -> return 1 ✓
```

**Input:** `nums = [5, 4, 3, 2, 1]` (monotone decreasing — valley at the end)

```
left=0  right=5  mid=2  nums[2]=3, L=4, R=2
  3 < 4 && 3 < 2? NO  |  3 > 2 (falling) -> left=3
left=3  right=5  mid=4  nums[4]=1, L=2, R=+inf
  1 < 2 && 1 < +inf? YES -> return 1 ✓   (the right sentinel certifies the last element)
```

**Input:** `nums = [1, 2, 3, 4, 5]` (monotone increasing — valley at the start)

```
left=0  right=5  mid=2  nums[2]=3, L=2, R=4
  3 < 2 && 3 < 4? NO  |  3 > 4? NO -> right=2
left=0  right=2  mid=1  nums[1]=2, L=1, R=3
  2 < 1? NO | 2 > 3? NO -> right=1
left=0  right=1  mid=0  nums[0]=1, L=+inf, R=2
  1 < +inf && 1 < 2? YES -> return 1 ✓   (the left sentinel certifies the first element)
```

## Complexity

**Time.** $O(\log n)$ — the sentinel reads keep every step at constant work.

**Space.** $O(1)$.

## Variants & follow-ups

- **[1.6](find-peak-element.md) / [1.7](find-peak-element-safe.md)** — the peak twin; flip every comparison and swap `MIN`/`MAX` sentinels to get this page.
- **Find the maximum of a bitonic array** — same structure, "climb the slope" the other way.
- **Interview follow-up:** "What if the array has a plateau of equal values?" The `<`/`>` comparisons break (a flat run is neither rising nor falling). With `<=` and `>=` the algorithm still returns *some* valley of the flattened array, but the guarantee weakens — another reason the boundary-safe style with explicit neighbor checks is preferred when inputs are messy.
