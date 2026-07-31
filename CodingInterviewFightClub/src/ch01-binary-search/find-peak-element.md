# 1.6 Find Peak Element

> **Source:** [`src/main/kotlin/binarysearch/FindPeakElement.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/FindPeakElement.kt)
> **Pattern:** monotone slope descent · **Core page**

## The Problem

A **peak** in an array is an element that is strictly greater than its neighbors. An array may have multiple peaks; return the index of **any** peak. You may assume `nums[-1] = nums[n] = -∞` (the boundaries count as "not greater than anything").

- Constraints: $1 \le n \le 10^4$, values distinct-ish (the algorithm works with duplicates too).

## Examples

```
Input:  nums = [1, 2, 3, 1]
Output: 2                  (nums[2] = 3 > 2 and 3 > 1)

Input:  nums = [1, 2, 1, 3, 5, 6, 4]
Output: 5                  (nums[5] = 6 is a peak; index 1 is also a peak — either is accepted)

Input:  nums = [1, 2, 3]   (monotone increasing)
Output: 2                  (3 > 2 and nums[3] = -∞)

Input:  nums = [3, 2, 1]   (monotone decreasing)
Output: 0                  (3 > -∞ and 3 > 2)
```

## Intuition — "climb the mountain"

The naive approach scans for any element greater than both neighbors: $O(n)$. But there's a *directional* structure: at any position `mid`, compare `nums[mid]` with `nums[mid+1]`:

- **`nums[mid] < nums[mid+1]` — the array is rising at `mid`.** Somewhere to the *right* there must be a peak. Why? The sequence starting at `mid+1` either keeps rising forever — in which case the last element is a peak (its right neighbor is $-\infty$) — or it eventually falls, and the first fall *is* a peak. Either way: **a peak exists strictly to the right**.
- **`nums[mid] > nums[mid+1]` — the array is falling at `mid`.** Symmetrically, a peak exists at `mid` or to its *left* (walk left and the sequence either keeps falling — first element is a peak since its left neighbor is $-\infty$ — or turns up, and the turn is a peak).

So the comparison `nums[mid] vs nums[mid+1]` tells us which half **provably contains a peak**. That's a monotone-ish predicate (once the array starts descending, it's "peaky on the left") — Template A again, but with a *geometric* argument instead of a value ordering. This is sometimes called **"binary search on the slope."**

The boundary assumption $nums[-1] = nums[n] = -\infty$ is what makes the "must exist" arguments airtight. Without it, a monotone array would have no peak.

## Approach 1 — Linear scan

Check every element against its two neighbors. $O(n)$. The classic $O(\log n)$ requirement is the whole point.

## Approach 2 — Slope-descent binary search (optimal)

```kotlin
/**
 * @param nums the input array (boundaries count as -infinity)
 * @return     the index of any peak element
 */
fun findPeakElement(nums: IntArray): Int {
    var left = 0
    var right = nums.lastIndex

    while (left < right) {
        val mid = left + (right - left) / 2
        if (nums[mid] > nums[mid + 1]) {
            right = mid        // falling here -> peak at or left of mid
        } else {
            left = mid + 1     // rising here -> peak strictly right of mid
        }
    }
    return left
}
```

```java
public class FindPeakElement {
    /**
     * @param nums the input array (boundaries count as -infinity)
     * @return     the index of any peak element
     */
    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid + 1]) {
                right = mid;      // falling here -> peak at or left of mid
            } else {
                left = mid + 1;   // rising here -> peak strictly right of mid
            }
        }
        return left;
    }
}
```

```cpp
#include <vector>

class FindPeakElement {
public:
    /**
     * @param nums the input array (boundaries count as -infinity)
     * @return     the index of any peak element
     */
    int findPeakElement(const std::vector<int>& nums) {
        int left = 0, right = (int)nums.size() - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid + 1]) right = mid;
            else left = mid + 1;
        }
        return left;
    }
};
```

```python
def find_peak_element(nums: list[int]) -> int:
    """
    @param nums: the input array (boundaries count as -infinity)
    @return:     the index of any peak element
    """
    left, right = 0, len(nums) - 1
    while left < right:
        mid = left + (right - left) // 2
        if nums[mid] > nums[mid + 1]:
            right = mid          # falling here -> peak at or left of mid
        else:
            left = mid + 1       # rising here -> peak strictly right of mid
    return left
```

```rust
impl Solution {
    /// @param nums the input array (boundaries count as -infinity)
    /// @return     the index of any peak element
    pub fn find_peak_element(nums: Vec<i32>) -> i32 {
        let (mut left, mut right) = (0usize, nums.len() - 1);
        while left < right {
            let mid = left + (right - left) / 2;
            if nums[mid] > nums[mid + 1] {
                right = mid;          // falling here -> peak at or left of mid
            } else {
                left = mid + 1;       // rising here -> peak strictly right of mid
            }
        }
        left as i32
    }
}
```

### 4. `FindPeakElementBetterSolution.kt` — boundary-safe peak

[1.6](../ch01-binary-search/find-peak-element.md) documents the standard binary-search peak; this file's "better" claim is **explicit boundary handling** — neighbors default to `Int.MIN_VALUE` at the edges:

```kotlin
class FindPeakElementBetterSolution {
    fun findPeakElement(nums: IntArray): Int? {
        if (nums.isEmpty()) return null

        var (left, right) = 0 to nums.size - 1

        while (left < right) {
            val mid = left + (right - left) / 2

            // Safely handle boundaries
            val leftNeighbor = if (mid > 0) nums[mid - 1] else Int.MIN_VALUE
            val rightNeighbor = if (mid < nums.size - 1) nums[mid + 1] else Int.MIN_VALUE

            when {
                nums[mid] > leftNeighbor && nums[mid] > rightNeighbor -> return mid   // peak
                nums[mid] < rightNeighbor -> left = mid + 1                            // go right
                else -> right = mid                                                    // go left
            }
        }
        return left
    }
}
```

**What's cool:** the `Int.MIN_VALUE` neighbors make the boundary cells valid peaks (a single-element array's only element is a peak); the `when` reads as the three-way decision; and `Int?` return explicitly signals "empty input". The three-branch structure also avoids [1.7](../ch01-binary-search/find-peak-element-safe.md)'s separate "safe boundaries" page — this file *is* that page's idea in one method.


## Dry run

**Input:** `nums = [1, 2, 3, 1]`

```
left=0  right=3  mid=1  nums[1]=2  nums[2]=3  2 > 3? NO -> left=2   (rising; peak to the right)
left=2  right=3  mid=2  nums[2]=3  nums[3]=1  3 > 1? YES -> right=2
left=2  right=2  -> return 2 ✓   (nums[2] = 3 is the peak)
```

**Input:** `nums = [1, 2, 3]` (monotone rising — peak is the last element)

```
left=0  right=2  mid=1  nums[1]=2  nums[2]=3  2 > 3? NO -> left=2
left=2  right=2  -> return 2 ✓   (last element; its right neighbor is -infinity)
```

**Input:** `nums = [3, 2, 1]` (monotone falling — peak is the first element)

```
left=0  right=2  mid=1  nums[1]=2  nums[2]=1  2 > 1? YES -> right=1
left=0  right=1  mid=0  nums[0]=3  nums[1]=2  3 > 2? YES -> right=0
left=0  right=0  -> return 0 ✓   (first element; its left neighbor is -infinity)
```

The two monotone cases are worth tracing twice — they're the *proof* that a peak always exists on the chosen side, and they're exactly what an interviewer will probe.

## Complexity

**Time.**

$$
T(n) = O(\log n)
$$

**Space.** $O(1)$.

## Variants & follow-ups

- **[1.13](peak-index-in-mountain-array.md)** — the same slope descent, but the array is *unimodal* (strictly rises then strictly falls) so there's exactly one peak.
- **[1.20](valley-element.md)** — mirror image: follow the *descending* direction to find a local minimum.
- **[1.7](find-peak-element-safe.md)** — a boundary-safe variant that reads both neighbors (works when you can't rely on the $±\infty$ convention).
- **Find Peak Element II (2D)** — the same "climb the slope" idea generalized to a matrix: find the max of the middle column, then recurse on the half that slopes up. $O(n \log m)$ instead of $O(nm)$.
- **Interview follow-up:** "Prove a peak exists in a non-empty array." The element with the maximum value is always a peak (it's ≥ both neighbors; with strict inequalities and distinct values it's strictly greater). Existence is free — the search just needs to *find* one without scanning.
