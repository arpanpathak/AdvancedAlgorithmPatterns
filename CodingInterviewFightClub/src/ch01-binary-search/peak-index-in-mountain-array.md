# 1.13 Peak Index In A Mountain Array

> **Source:** [`src/main/kotlin/binarysearch/PeakIndexInMountainArray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/PeakIndexInMountainArray.kt)
> **Pattern:** monotone slope descent (unimodal) · **Variant page**

## The Problem

A **mountain array** is an array that strictly increases up to a peak index `i`, then strictly decreases:

$$
arr[0] < arr[1] < \cdots < arr[i] > arr[i+1] > \cdots > arr[n-1]
$$

`arr` is guaranteed to be a mountain. Return the **peak index** `i`.

- Constraints: $3 \le n \le 10^4$, `arr` is a mountain by construction.

## Examples

```
Input:  arr = [0, 1, 0]          -> Output: 1
Input:  arr = [0, 2, 1, 0]       -> Output: 1
Input:  arr = [0, 1, 2, 3, 6, 5, 4, 3]  -> Output: 4
```

## Intuition

This is [1.6](find-peak-element.md) with a guarantee: the array is **unimodal**, so there is *exactly one* peak, and the slope comparison is *globally* consistent (rises everywhere left of the peak, falls everywhere right of it). That makes the predicate **truly monotone**, not just "a peak exists on this side":

```
slope:  /  /  /  /  |  \  \  \
                    ^
                peak = the turning point
```

So the standard slope-descent binary search is not just correct — it's *sharp*: it finds the unique turning point in $O(\log n)$. (With the unimodal guarantee, even the variant that compares `nums[mid]` with *both* neighbors and early-returns on a hit works.)

## Approach 1 — Linear scan

Scan until `arr[i] > arr[i+1]`; that `i` is the peak. $O(n)$. The $O(\log n)$ requirement (and $n$ up to $10^4$+) makes binary search the expected answer.

## Approach 2 — Slope-descent binary search (optimal)

```kotlin
/**
 * @param arr a mountain array (strictly increases then strictly decreases)
 * @return    the index of the single peak
 */
fun peakIndexInMountainArray(arr: IntArray): Int {
    var (left, right) = 0 to arr.lastIndex

    while (left <= right) {
        val mid = left + (right - left) / 2
        when {
            arr[mid + 1] > arr[mid] -> left = mid + 1   // still rising -> peak is to the right
            else                    -> right = mid - 1  // falling -> peak is at or left of mid
        }
    }
    return left
}
```

```java
public class PeakIndexInMountainArray {
    /**
     * @param arr a mountain array (strictly increases then strictly decreases)
     * @return    the index of the single peak
     */
    public int peakIndexInMountainArray(int[] arr) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid + 1] > arr[mid]) {
                left = mid + 1;       // still rising -> peak is to the right
            } else {
                right = mid - 1;      // falling -> peak is at or left of mid
            }
        }
        return left;
    }
}
```

```cpp
#include <vector>

class PeakIndexInMountainArray {
public:
    /**
     * @param arr a mountain array (strictly increases then strictly decreases)
     * @return    the index of the single peak
     */
    int peakIndexInMountainArray(const std::vector<int>& arr) {
        int left = 0, right = (int)arr.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid + 1] > arr[mid]) left = mid + 1;
            else right = mid - 1;
        }
        return left;
    }
};
```

```python
def peak_index_in_mountain_array(arr: list[int]) -> int:
    """
    @param arr: a mountain array (strictly increases then strictly decreases)
    @return:    the index of the single peak
    """
    left, right = 0, len(arr) - 1
    while left <= right:
        mid = left + (right - left) // 2
        if arr[mid + 1] > arr[mid]:
            left = mid + 1        # still rising -> peak is to the right
        else:
            right = mid - 1       # falling -> peak is at or left of mid
    return left
```

```rust
impl Solution {
    /// @param arr a mountain array (strictly increases then strictly decreases)
    /// @return    the index of the single peak
    pub fn peak_index_in_mountain_array(arr: Vec<i32>) -> i32 {
        let (mut left, mut right) = (0usize, arr.len() - 1);
        while left <= right {
            let mid = left + (right - left) / 2;
            if arr[mid + 1] > arr[mid] {
                left = mid + 1;
            } else if mid == 0 {
                break;
            } else {
                right = mid - 1;
            }
        }
        left as i32
    }
}
```

> **Note on `mid + 1`:** the code reads `arr[mid + 1]`. Because `arr` is a mountain with $n \ge 3$, the peak is never at index 0, so a valid `mid` never equals `arr.lastIndex` while `left <= right` still holds — the access is safe. If you dislike relying on that, use the two-neighbor form from [1.7](find-peak-element-safe.md).

## Dry run

**Input:** `arr = [0, 1, 2, 3, 6, 5, 4, 3]`

```
left=0  right=7  mid=3  arr[4]=6 > arr[3]=3  -> left=4   (rising; peak right)
left=4  right=7  mid=5  arr[6]=4 > arr[5]=5? NO -> right=4  (falling; peak at/left)
left=4  right=4  mid=4  arr[5]=5 > arr[4]=6? NO -> right=3
left=4  right=3  -> loop ends, return 4 ✓
```

**Input:** `arr = [0, 2, 1, 0]`

```
left=0  right=3  mid=1  arr[2]=1 > arr[1]=2? NO -> right=0
left=0  right=0  mid=0  arr[1]=2 > arr[0]=0  YES -> left=1
left=1  right=0  -> return 1 ✓
```

## Complexity

**Time.** Each iteration halves the window; the `mid + 1` reads keep it at a constant number of array accesses per step:

$$
T(n) = O(\log n)
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **[1.6](find-peak-element.md)** — the general (multi-peak) version, where "any peak" is acceptable.
- **Find in Mountain Array** (LeetCode 1095) — combine *this* search (to find the peak) with two standard binary searches (one on the rising half, one on the falling half).
- **Interview follow-up:** "What if the array could be flat (plateaus)?" The mountain guarantee breaks; the `arr[mid+1] > arr[mid]` test becomes ambiguous on a flat run, and you'd fall back to the boundary-safe neighbor comparison of 1.7.
