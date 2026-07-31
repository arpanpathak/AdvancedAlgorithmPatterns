# 1.7 Find Peak Element (Safe Boundaries)

> **Source:** [`src/main/kotlin/binarysearch/FindPeakElementBetterSolution.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/FindPeakElementBetterSolution.kt)
> **Pattern:** monotone slope descent, boundary-safe · **Variant page**

## The Problem

Identical to [1.6](find-peak-element.md) — return the index of **any** peak — but the implementation here makes no assumption about reading `nums[mid + 1]` at the boundary: it checks *both* neighbors with explicit bounds guards, and early-returns the moment it *finds* a peak (no need to converge the window).

## Intuition

The two styles differ in *when* they certify a peak:

- **1.6-style (window convergence):** never early-returns; it relies on the invariant "the window provably contains a peak" and reads only `nums[mid]` vs `nums[mid+1]`. Requires trusting the $-\infty$ boundary convention.
- **This page (explicit check):** at each `mid`, read both neighbors (with `Int.MIN_VALUE` at the edges) and check the *definition* of a peak directly: `nums[mid] > left && nums[mid] > right`. If yes — return. If not, use the slope to decide which half still provably contains a peak.

The explicit version is more robust to rephrasings of the problem (e.g. "the array might be monotone; boundaries are the answer") and it self-documents the peak definition. The cost is reading two neighbors instead of one — still $O(1)$ per step, so complexity is unchanged.

## Approach — boundary-safe slope descent

```kotlin
/**
 * @param nums the input array (boundaries treated as -infinity)
 * @return     the index of any peak element, or null if nums is empty
 */
fun findPeakElement(nums: IntArray): Int? {
    if (nums.isEmpty()) return null

    var (left, right) = 0 to nums.size - 1

    while (left < right) {
        val mid = left + (right - left) / 2

        // Read both neighbors with explicit sentinel values at the edges.
        val leftNeighbor  = if (mid > 0) nums[mid - 1] else Int.MIN_VALUE
        val rightNeighbor = if (mid < nums.size - 1) nums[mid + 1] else Int.MIN_VALUE

        when {
            // Direct hit: strictly greater than both neighbors.
            nums[mid] > leftNeighbor && nums[mid] > rightNeighbor -> return mid
            // Rising to the right -> a peak exists strictly to the right.
            nums[mid] < rightNeighbor -> left = mid + 1
            // Falling to the left -> a peak exists at or to the left.
            else -> right = mid
        }
    }
    return left
}
```

```java
public class FindPeakElementSafe {
    /**
     * @param nums the input array (boundaries treated as -infinity)
     * @return     the index of any peak element, or -1 if nums is empty
     */
    public int findPeakElement(int[] nums) {
        if (nums.length == 0) return -1;
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            int leftNeighbor  = mid > 0 ? nums[mid - 1] : Integer.MIN_VALUE;
            int rightNeighbor = mid < nums.length - 1 ? nums[mid + 1] : Integer.MIN_VALUE;
            if (nums[mid] > leftNeighbor && nums[mid] > rightNeighbor) return mid;
            if (nums[mid] < rightNeighbor) left = mid + 1;
            else right = mid;
        }
        return left;
    }
}
```

```cpp
#include <vector>
#include <climits>

class FindPeakElementSafe {
public:
    /**
     * @param nums the input array (boundaries treated as -infinity)
     * @return     the index of any peak element, or -1 if nums is empty
     */
    int findPeakElement(const std::vector<int>& nums) {
        if (nums.empty()) return -1;
        int left = 0, right = (int)nums.size() - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            int leftNeighbor  = mid > 0 ? nums[mid - 1] : INT_MIN;
            int rightNeighbor = mid < (int)nums.size() - 1 ? nums[mid + 1] : INT_MIN;
            if (nums[mid] > leftNeighbor && nums[mid] > rightNeighbor) return mid;
            if (nums[mid] < rightNeighbor) left = mid + 1;
            else right = mid;
        }
        return left;
    }
};
```

```python
def find_peak_element(nums: list[int]) -> int:
    """
    @param nums: the input array (boundaries treated as -infinity)
    @return:     the index of any peak element, or -1 if nums is empty
    """
    if not nums:
        return -1
    left, right = 0, len(nums) - 1
    while left < right:
        mid = left + (right - left) // 2
        left_neighbor  = nums[mid - 1] if mid > 0 else float("-inf")
        right_neighbor = nums[mid + 1] if mid < len(nums) - 1 else float("-inf")
        if nums[mid] > left_neighbor and nums[mid] > right_neighbor:
            return mid                     # direct hit
        if nums[mid] < right_neighbor:
            left = mid + 1                 # rising -> peak to the right
        else:
            right = mid                    # falling -> peak at or left
    return left
```

```rust
impl Solution {
    /// @param nums the input array (boundaries treated as -infinity)
    /// @return     the index of any peak element, or -1 if nums is empty
    pub fn find_peak_element(nums: Vec<i32>) -> i32 {
        if nums.is_empty() {
            return -1;
        }
        let (mut left, mut right) = (0usize, nums.len() - 1);
        while left < right {
            let mid = left + (right - left) / 2;
            let left_neighbor  = if mid > 0 { nums[mid - 1] } else { i32::MIN };
            let right_neighbor = if mid + 1 < nums.len() { nums[mid + 1] } else { i32::MIN };
            if nums[mid] > left_neighbor && nums[mid] > right_neighbor {
                return mid as i32;
            }
            if nums[mid] < right_neighbor {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        left as i32
    }
}
```

## Dry run

**Input:** `nums = [1, 2, 1, 3, 5, 6, 4]`

```
left=0  right=6  mid=3  nums[3]=3, L=1, R=5
  3 > 1 && 3 > 5? NO  |  3 < 5 (rising) -> left=4
left=4  right=6  mid=5  nums[5]=6, L=5, R=4
  6 > 5 && 6 > 4? YES -> return 5 ✓
```

**Input:** `nums = [1, 2, 3]` (monotone rising — peak at the end)

```
left=0  right=2  mid=1  nums[1]=2, L=1, R=3
  2 > 1 && 2 > 3? NO  |  2 < 3 -> left=2
left=2  right=2  -> return 2 ✓   (the loop never probes the boundary read; it converges)
```

**Input:** `nums = [3, 2, 1]` (monotone falling — peak at the start)

```
left=0  right=2  mid=1  nums[1]=2, L=3, R=1
  2 > 3 && 2 > 1? NO  |  2 < 1? NO -> right=1
left=0  right=1  mid=0  nums[0]=3, L=-inf, R=2
  3 > -inf && 3 > 2? YES -> return 0 ✓   (the left sentinel kicks in exactly here)
```

The last trace is the reason this variant exists: the *sentinel* makes the boundary element a valid, provable peak without needing the abstract "$-\infty$" convention from the problem statement.

## Complexity

**Time.** $O(\log n)$ — at most two array reads per halving step.

**Space.** $O(1)$.

## Variants & follow-ups

- **[1.6](find-peak-element.md)** — the minimal one-neighbor version; fewer reads, more reliance on the boundary convention.
- **[1.20](valley-element.md)** — the same boundary-safe style, mirrored for valleys (uses `Int.MAX_VALUE` sentinels).
- **Interview follow-up:** "Return *all* peaks." Binary search doesn't help; you need an $O(n)$ scan (every element can be a peak, e.g. a sawtooth). Knowing when the trick *doesn't* apply is part of the answer.
