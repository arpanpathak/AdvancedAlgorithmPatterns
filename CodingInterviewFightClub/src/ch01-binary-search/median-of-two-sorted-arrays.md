# 1.12 Median Of Two Sorted Arrays

> **Source:** [`src/main/kotlin/binarysearch/MedianOfTwoSortedARrays.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/MedianOfTwoSortedARrays.kt)
> **Pattern:** partition-based search · **The boss fight of this chapter**

## The Problem

Given two **sorted** arrays `nums1` (size $m$) and `nums2` (size $n$), return the **median** of the two sorted arrays as a `Double`, in $O(\log \min(m, n))$ time.

- Constraints: $0 \le m, n \le 10^3$, $m + n \ge 1$.

## Examples

```
Input:  nums1 = [1, 3], nums2 = [2]
Output: 2.0
Explanation: merged = [1, 2, 3]; median = 2.

Input:  nums1 = [1, 2], nums2 = [3, 4]
Output: 2.5
Explanation: merged = [1, 2, 3, 4]; median = (2 + 3) / 2 = 2.5.

Input:  nums1 = [], nums2 = [1]
Output: 1.0
```

## Intuition — binary search on a *partition*, not on an element

The median splits the merged array into a left half and a right half of **equal size** (or off-by-one when odd). Key move: we don't merge — we decide **where the split falls inside each array**.

Let `partitionX` = number of elements taken from `nums1` into the left half, and `partitionY` = number taken from `nums2`. For the halves to be balanced we need:

$$
\text{partitionX} + \text{partitionY} = \frac{m + n + 1}{2} \quad \text{(integer division)}
$$

The left half is then `max(leftX, leftY)` where `leftX = nums1[partitionX-1]` etc., and the right half is `min(rightX, rightY)`. The arrangement is a **valid split** exactly when:

$$
\text{leftX} \le \text{rightY} \quad \text{and} \quad \text{leftY} \le \text{rightX}
$$

i.e. everything on the left is ≤ everything on the right. If `leftX > rightY`, we took **too many** elements from `nums1` (its left side pokes past `nums2`'s right side) → move `partitionX` left. If `leftY > rightX`, take **more** from `nums1` → move `partitionX` right. Either way the fix is a *halving* step on `partitionX` — **binary search on the partition position**.

We binary search over `partitionX ∈ [0, m]` (searching the *smaller* array — that's where the $\log \min(m,n)$ comes from). At the valid split, the median is:

$$
\text{median} =
\begin{cases}
\max(\text{leftX}, \text{leftY}), & m + n \text{ odd} \\[2mm]
\dfrac{\max(\text{leftX},\text{leftY}) + \min(\text{rightX},\text{rightY})}{2}, & m + n \text{ even}
\end{cases}
$$

Boundary cells are handled with sentinels: `-∞` for a nonexistent left element, `+∞` for a nonexistent right element. This is why the code uses `getOrNull(...) ?: Int.MIN_VALUE/MAX_VALUE` — the sentinel makes the comparisons work at the edges without special-casing.

**Why search the smaller array?** `partitionY = (m+n+1)/2 - partitionX` must stay in `[0, n]`. If we binary-searched the larger array, `partitionY` could fall out of range. By always searching the smaller one, the constraint is automatically satisfiable. The swap at the top (`if nums1.size > nums2.size → recurse with swapped args`) enforces this.

## Approach 1 — Merge and pick

Merge both arrays into one sorted list ($O(m + n)$), then read the middle element(s). Correct, and what most people write in an interview first. Then the interviewer says "make it $O(\log(m+n))$" — and this page is the answer.

## Approach 2 — Partition binary search (optimal)

```kotlin
/**
 * @param nums1 the first sorted array (may be empty)
 * @param nums2 the second sorted array (may be empty)
 * @return      the median of the combined sorted sequence as a Double
 */
fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
    // Always binary search the SMALLER array: keeps partitionY in bounds.
    if (nums1.size > nums2.size) {
        return findMedianSortedArrays(nums2, nums1)
    }

    val m = nums1.size
    val n = nums2.size
    var start = 0
    var end = m

    while (start <= end) {
        val partitionX = (start + end) / 2
        val partitionY = (m + n + 1) / 2 - partitionX   // balance the halves

        // Sentinels: nonexistent left cells are -inf, right cells are +inf.
        val leftX  = nums1.getOrNull(partitionX - 1) ?: Int.MIN_VALUE
        val rightX = nums1.getOrNull(partitionX) ?: Int.MAX_VALUE
        val leftY  = nums2.getOrNull(partitionY - 1) ?: Int.MIN_VALUE
        val rightY = nums2.getOrNull(partitionY) ?: Int.MAX_VALUE

        when {
            // Valid split: everything left <= everything right.
            leftX <= rightY && leftY <= rightX -> {
                return if ((m + n) % 2 == 0) {
                    (maxOf(leftX, leftY) + minOf(rightX, rightY)) / 2.0
                } else {
                    maxOf(leftX, leftY).toDouble()
                }
            }
            // Too many elements taken from nums1: push partitionX left.
            leftX > rightY -> end = partitionX - 1
            // Too few from nums1: push partitionX right.
            else           -> start = partitionX + 1
        }
    }
    return -1.0 // Unreachable for valid inputs
}
```

```java
public class MedianOfTwoSortedArrays {
    /**
     * @param nums1 the first sorted array (may be empty)
     * @param nums2 the second sorted array (may be empty)
     * @return      the median of the combined sorted sequence
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) return findMedianSortedArrays(nums2, nums1);
        int m = nums1.length, n = nums2.length;
        int start = 0, end = m;

        while (start <= end) {
            int partitionX = (start + end) / 2;
            int partitionY = (m + n + 1) / 2 - partitionX;

            int leftX  = partitionX - 1 >= 0 ? nums1[partitionX - 1] : Integer.MIN_VALUE;
            int rightX = partitionX < m ? nums1[partitionX] : Integer.MAX_VALUE;
            int leftY  = partitionY - 1 >= 0 ? nums2[partitionY - 1] : Integer.MIN_VALUE;
            int rightY = partitionY < n ? nums2[partitionY] : Integer.MAX_VALUE;

            if (leftX <= rightY && leftY <= rightX) {
                if ((m + n) % 2 == 0) {
                    return (Math.max(leftX, leftY) + Math.min(rightX, rightY)) / 2.0;
                }
                return Math.max(leftX, leftY);
            }
            if (leftX > rightY) end = partitionX - 1;
            else start = partitionX + 1;
        }
        return -1.0;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <climits>

class MedianOfTwoSortedArrays {
public:
    /**
     * @param nums1 the first sorted array (may be empty)
     * @param nums2 the second sorted array (may be empty)
     * @return      the median of the combined sorted sequence
     */
    double findMedianSortedArrays(std::vector<int> nums1, std::vector<int> nums2) {
        if (nums1.size() > nums2.size()) return findMedianSortedArrays(nums2, nums1);
        int m = (int)nums1.size(), n = (int)nums2.size();
        int start = 0, end = m;

        while (start <= end) {
            int partitionX = (start + end) / 2;
            int partitionY = (m + n + 1) / 2 - partitionX;

            int leftX  = partitionX - 1 >= 0 ? nums1[partitionX - 1] : INT_MIN;
            int rightX = partitionX < m ? nums1[partitionX] : INT_MAX;
            int leftY  = partitionY - 1 >= 0 ? nums2[partitionY - 1] : INT_MIN;
            int rightY = partitionY < n ? nums2[partitionY] : INT_MAX;

            if (leftX <= rightY && leftY <= rightX) {
                if ((m + n) % 2 == 0) {
                    return (std::max(leftX, leftY) + std::min(rightX, rightY)) / 2.0;
                }
                return std::max(leftX, leftY);
            }
            if (leftX > rightY) end = partitionX - 1;
            else start = partitionX + 1;
        }
        return -1.0;
    }
};
```

```python
def find_median_sorted_arrays(nums1: list[int], nums2: list[int]) -> float:
    """
    @param nums1: the first sorted array (may be empty)
    @param nums2: the second sorted array (may be empty)
    @return:      the median of the combined sorted sequence
    """
    if len(nums1) > len(nums2):
        return find_median_sorted_arrays(nums2, nums1)   # search the smaller array

    m, n = len(nums1), len(nums2)
    start, end = 0, m
    import sys
    NEG, POS = -sys.maxsize - 1, sys.maxsize

    while start <= end:
        partition_x = (start + end) // 2
        partition_y = (m + n + 1) // 2 - partition_x

        left_x  = nums1[partition_x - 1] if partition_x - 1 >= 0 else NEG
        right_x = nums1[partition_x] if partition_x < m else POS
        left_y  = nums2[partition_y - 1] if partition_y - 1 >= 0 else NEG
        right_y = nums2[partition_y] if partition_y < n else POS

        if left_x <= right_y and left_y <= right_x:
            if (m + n) % 2 == 0:
                return (max(left_x, left_y) + min(right_x, right_y)) / 2.0
            return float(max(left_x, left_y))
        if left_x > right_y:
            end = partition_x - 1
        else:
            start = partition_x + 1
    return -1.0
```

```rust
impl Solution {
    /// @param nums1 the first sorted array (may be empty)
    /// @param nums2 the second sorted array (may be empty)
    /// @return      the median of the combined sorted sequence
    pub fn find_median_sorted_arrays(nums1: Vec<i32>, nums2: Vec<i32>) -> f64 {
        if nums1.len() > nums2.len() {
            return Self::find_median_sorted_arrays(nums2, nums1);
        }
        let (m, n) = (nums1.len(), nums2.len());
        let (mut start, mut end) = (0usize, m);

        while start <= end {
            let partition_x = start + (end - start) / 2;
            let partition_y = (m + n + 1) / 2 - partition_x;

            let left_x  = if partition_x > 0 { nums1[partition_x - 1] } else { i32::MIN };
            let right_x = if partition_x < m { nums1[partition_x] } else { i32::MAX };
            let left_y  = if partition_y > 0 { nums2[partition_y - 1] } else { i32::MIN };
            let right_y = if partition_y < n { nums2[partition_y] } else { i32::MAX };

            if left_x <= right_y && left_y <= right_x {
                if (m + n) % 2 == 0 {
                    return (left_x.max(left_y) as f64 + right_x.min(right_y) as f64) / 2.0;
                }
                return left_x.max(left_y) as f64;
            }
            if left_x > right_y {
                if partition_x == 0 { break; }
                end = partition_x - 1;
            } else {
                start = partition_x + 1;
            }
        }
        -1.0
    }
}
```

## Dry run

**Input:** `nums1 = [1, 3]`, `nums2 = [2]`. $m = 2, n = 1, m + n = 3$ (odd → median is the max of the left halves).

```
start=0  end=2  partitionX=1  partitionY=(3+1)/2 - 1 = 1
  leftX = nums1[0] = 1        rightX = nums1[1] = 3
  leftY = nums2[0] = 2        rightY = +inf (partitionY = 1 == n)
  leftX(1) <= rightY(inf) ✓   leftY(2) <= rightX(3) ✓  -> VALID
  odd -> return max(1, 2) = 2.0 ✓
```

Visualize the split:

```
nums1: [1 | 3]        partitionX = 1 (take 1 into the left half)
nums2: [2 | ]         partitionY = 1 (take 2 into the left half)
left half  = {1, 2}   right half = {3}
median = max(1, 2) = 2 ✓
```

**Input:** `nums1 = [1, 2]`, `nums2 = [3, 4]`. $m + n = 4$ (even → average of the two middle values).

```
start=0  end=2  partitionX=1  partitionY=(4+1)/2 - 1 = 1
  leftX=1  rightX=2  leftY=3  rightY=4
  leftX(1) <= rightY(4) ✓   leftY(3) <= rightX(2)? NO  -> leftY > rightX: take MORE from nums1 -> start=2
start=2  end=2  partitionX=2  partitionY=2 - 2 = 0
  leftX = nums1[1] = 2    rightX = +inf
  leftY = -inf            rightY = nums2[0] = 3
  leftX(2) <= rightY(3) ✓  leftY(-inf) <= rightX(inf) ✓ -> VALID
  even -> (max(2, -inf) + min(inf, 3)) / 2 = (2 + 3) / 2 = 2.5 ✓
```

Visualize the second (final) split:

```
nums1: [1, 2 | ]      partitionX = 2 (take both into the left half)
nums2: [   | 3, 4]    partitionY = 0 (take none)
left half  = {1, 2}   right half = {3, 4}
median = (max(2, -inf) + min(inf, 3)) / 2 = 2.5 ✓
```

Notice how the sentinels (`-inf`/`+inf`) make the "empty side" cases flow through the same formula — no special-casing anywhere.

## Complexity

**Time.** The search space is `partitionX ∈ [0, m]` with $m = \min(m, n)$ after the swap; each step is $O(1)$:

$$
T(m, n) = O(\log \min(m, n))
$$

This beats the $O(\log(m+n))$ "binary search the k-th element" alternative and is the best possible for comparison-based approaches.

**Space.** $O(1)$.

## Variants & follow-ups

- **Kth element of two sorted arrays** — the general form; this page's partition idea generalizes by adjusting the "balance" formula to `k`.
- **Median of a data stream** (`src/main/kotlin/heap/`) — the *streaming* version: two heaps (max-heap for the lower half, min-heap for the upper half), $O(\log n)$ per insertion, $O(1)$ median. Static vs streaming is a classic pairing.
- **Interview follow-up:** "Why must `partitionY` stay in `[0, n]`?" If it fell outside, `nums2` would be entirely on one side — an invalid split. The smaller-array swap guarantees `0 ≤ (m+n+1)/2 − x ≤ n` for every `x ∈ [0, m]`; verify with the extremes: at `x = 0`, `partitionY = (m+n+1)/2 ≤ n` since `m ≤ n`; at `x = m`, `partitionY = (n−m+1)/2 ≥ 0`.
- **Interview follow-up:** "Why `(m + n + 1) / 2` and not `(m + n) / 2`?" The `+1` biases the left half to be *at least as large* as the right half, so the odd case (extra element in the left half) is handled by `max(leftX, leftY)` without branching on which array holds the extra element.
