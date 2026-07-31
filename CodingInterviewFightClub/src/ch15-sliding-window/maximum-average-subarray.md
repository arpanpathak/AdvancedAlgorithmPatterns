# 15.4 Maximum Average Subarray I

> **Source:** [`src/main/kotlin/sliding_window/MaximumAverageSubarray_I.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/MaximumAverageSubarray_I.kt)
> **Pattern:** fixed-size running sum · **Core page**

## The Problem

Given an array `nums` and a window size `k`, return the maximum **average** of any contiguous subarray of length exactly `k`.

- Constraints: $1 \le k \le n \le 10^5$; values fit in `Double`.

## Examples

```
Input:  nums = [1,12,-5,-6,50,3], k = 4   -> Output: 12.75   (the window [12,-5,-6,50])
Input:  nums = [5], k = 1                  -> Output: 5.0
```

## Intuition — the fixed-size window is arithmetic, not logic

The window is always exactly `k` long, so the left pointer is *derived* (`left = right - k + 1`), never chosen — there's no condition to satisfy, no shrink loop. The only question is updating the **running sum** in $O(1)$: add the entering element; once the window is full (`i >= k-1`), record `sum / k` and remove the leaving element.

**Why a running sum instead of recomputing?** Summing each window from scratch is $O(n \cdot k)$. The running sum does one add and one subtract per step — the entire savings of the sliding-window idea in its purest form. The maximum average is the maximum sum divided by the constant `k` — so tracking the *sum* and dividing at the end (or per window) are equivalent; the repo divides per window.

**The edge case rhythm:** the add happens first; the remove happens *after* recording. For `i < k-1` the window isn't full yet (only adds); for `i >= k-1` it's full — record, then remove `nums[i - k + 1]` to make room for the next add.

**Negative numbers matter:** initializing `maxAverage` to `Double.NEGATIVE_INFINITY` (not 0) is what makes windows with negative sums (like `[-5,-6,...]`) counted correctly. A `0` initializer would wrongly report 0 when every window is negative.

## Approach 1 — Sum every window (O(nk))

For each start, sum k elements: the baseline that the running sum beats.

## Approach 2 — Fixed-size running sum (the repo's version, optimal)

```kotlin
class MaximumAverageSubarray_I {
    /**
     * @param nums input array
     * @param k    window size
     * @return     maximum average of any length-k subarray
     */
    fun findMaxAverage(nums: IntArray, k: Int): Double {
        var windowSum = 0.0
        var maxAverage = Double.NEGATIVE_INFINITY      // not 0: negative windows must count

        for (i in nums.indices) {
            windowSum += nums[i]                       // the entering element

            if (i >= k - 1) {                          // window is full
                maxAverage = maxOf(maxAverage, windowSum / k)
                windowSum -= nums[i - k + 1]           // the leaving element
            }
        }
        return maxAverage
    }
}
```

```java
public class MaximumAverageSubarray {
    /**
     * @param nums input array
     * @param k    window size
     * @return     maximum average of any length-k subarray
     */
    public double findMaxAverage(int[] nums, int k) {
        double windowSum = 0;
        double maxAverage = Double.NEGATIVE_INFINITY;   // not 0: negative windows must count

        for (int i = 0; i < nums.length; i++) {
            windowSum += nums[i];                       // the entering element

            if (i >= k - 1) {                           // window is full
                maxAverage = Math.max(maxAverage, windowSum / k);
                windowSum -= nums[i - k + 1];           // the leaving element
            }
        }
        return maxAverage;
    }
}
```

```cpp
#include <vector>

class MaximumAverageSubarray {
public:
    /**
     * @param nums input array
     * @param k    window size
     * @return     maximum average of any length-k subarray
     */
    double findMaxAverage(std::vector<int>& nums, int k) {
        double windowSum = 0;
        double maxAverage = -1e18;                       // not 0: negative windows must count

        for (int i = 0; i < (int)nums.size(); i++) {
            windowSum += nums[i];                        // the entering element

            if (i >= k - 1) {                            // window is full
                maxAverage = std::max(maxAverage, windowSum / k);
                windowSum -= nums[i - k + 1];            // the leaving element
            }
        }
        return maxAverage;
    }
};
```

```python
def find_max_average(nums: list[int], k: int) -> float:
    """
    @param nums: input array
    @param k:    window size
    @return:     maximum average of any length-k subarray
    """
    window_sum = 0
    max_average = float("-inf")          # not 0: negative windows must count

    for i, num in enumerate(nums):
        window_sum += num                # the entering element

        if i >= k - 1:                   # window is full
            max_average = max(max_average, window_sum / k)
            window_sum -= nums[i - k + 1]   # the leaving element
    return max_average
```

```rust
impl Solution {
    /// @param nums input array
    /// @param k    window size
    /// @return     maximum average of any length-k subarray
    pub fn find_max_average(nums: Vec<i32>, k: i32) -> f64 {
        let k = k as usize;
        let mut window_sum = 0f64;
        let mut max_average = f64::NEG_INFINITY;     // not 0: negative windows must count

        for i in 0..nums.len() {
            window_sum += nums[i] as f64;            // the entering element

            if i >= k - 1 {                          // window is full
                max_average = max_average.max(window_sum / k as f64);
                window_sum -= nums[i - k + 1] as f64;   // the leaving element
            }
        }
        max_average
    }
}
```

## Dry run

**Input:** `nums = [1,12,-5,-6,50,3]`, `k = 4`.

```
windowSum = 0, maxAverage = -inf

i=0 (1):  sum=1.    window not full.
i=1 (12): sum=13.   not full.
i=2 (-5): sum=8.    not full.
i=3 (-6): sum=2.    FULL: avg = 2/4 = 0.5 -> max=0.5.  remove nums[0]=1 -> sum=1.
i=4 (50): sum=51.   FULL: avg = 51/4 = 12.75 -> max=12.75.  remove nums[1]=12 -> sum=39.
i=5 (3):  sum=42.   FULL: avg = 42/4 = 10.5.  max stays 12.75.

Output: 12.75 ✓   (the window [12,-5,-6,50])
```

The add-record-remove rhythm in one view: the sum is always exactly the current k-window's total (after i=3), because every add past the full point is paired with a remove. The `-inf` initializer is what lets a window averaging *negative* (say `[-5,-6,...]`) still be the answer when all windows are negative.

## Complexity

**Time.** One pass, O(1) per step:

$$
T(n) = O(n)
$$

**Space.** Two variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Maximum Sum Of Distinct Subarrays With Length K** (`src/main/kotlin/sliding_window/`) — the same fixed window plus a distinctness condition (a set or map tracks duplicates, and the left pointer must also clear them).
- **Sliding Window Maximum** ([15.6](sliding-window-maximum.md)) — the same fixed window but the *max* (not the sum) per window — needs the monotonic deque.
- **Maximum Erasure Value** (`src/main/kotlin/sliding_window/`) — a variable-length window maximizing a sum with no repeats.
- **Interview follow-up:** "Why divide per window instead of tracking the max sum and dividing once?" Both are correct (k is constant); dividing per window keeps the code directly readable as "average". The only care needed is floating point — `Double` for the sum avoids precision drift on large inputs.
