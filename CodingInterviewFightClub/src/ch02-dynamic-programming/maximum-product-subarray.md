# 2.7 Maximum Product Subarray

> **Source:** [`src/main/kotlin/dynamic_programming/MaximumProductSubarray.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/dynamic_programming/MaximumProductSubarray.kt)
> **Pattern:** two-track DP (max AND min) · **Core page — the sign-flip lesson**

## The Problem

Given an integer array `nums`, find the **contiguous subarray** (non-empty) with the largest **product**, and return that product. Values may be negative or zero.

- Constraints: $1 \le n \le 2 \times 10^4$, $-10 \le nums[i] \le 10$.

## Examples

```
Input:  nums = [2, 3, -2, 4]    -> 6   (the subarray [2, 3])
Input:  nums = [-2, 0, -1]      -> 0   (either 0, or a negative — 0 wins; subarray [0])
Input:  nums = [-2, 3, -4]      -> 24  (-2 × 3 × -4 — two negatives flip positive)
Input:  nums = [-1, -2, -3]     -> 6   ([-2, -3] or [-1,-2,-3] = 6)
```

## Intuition — why tracking only the max fails

The obvious DP "max product ending at i = max(nums[i], maxPrev × nums[i])" is **wrong**, and the counterexample is `[-2, 3, -4]`:

- At index 1, max ending here = `max(3, -2×3) = 3`.
- At index 2, max ending here = `max(-4, 3×-4) = -4`. Answer would be 3 — but the true answer is **24** = `(-2 × 3 × -4)`.

The missing information: the *minimum* product ending at `i-1`. Because **a negative × negative is positive**, the smallest value can become the largest after multiplying by a negative. So the DP must track *both extremes*:

$$
\begin{aligned}
\maxEnd_i &= \max\big(nums[i],\; \maxEnd_{i-1} \cdot nums[i],\; \minEnd_{i-1} \cdot nums[i]\big) \\
\minEnd_i &= \min\big(nums[i],\; \maxEnd_{i-1} \cdot nums[i],\; \minEnd_{i-1} \cdot nums[i]\big)
\end{aligned}
$$

Three candidates each, and the three cases have names:

1. **Start fresh** — `nums[i]` alone (e.g. after a zero resets the streak, or when the previous products are worse).
2. **Extend the max streak** — `maxEnd × nums[i]` (positive × positive, or negative × negative when the sign works out).
3. **Flip the min streak** — `minEnd × nums[i]` (negative × negative = positive — *this* is the case the naive DP forgets).

The answer is the running max of `maxEnd` over all positions (the best subarray can end anywhere).

## Approach 1 — Brute force

Try every subarray: $O(n^2)$ pairs, each product $O(1)$ incremental → $O(n^2)$ time. $n = 2 \times 10^4$ → $4 \times 10^8$ — too slow.

## Approach 2 — Two-track DP (optimal)

```kotlin
/**
 * @param nums the integer array (may contain negatives and zeros)
 * @return     the maximum product of any contiguous non-empty subarray
 */
fun maxProduct(nums: IntArray): Int {
    if (nums.isEmpty()) return 0

    var maxEnd = nums[0]   // max product of a subarray ENDING at the current position
    var minEnd = nums[0]   // min product of a subarray ENDING at the current position
    var result = maxEnd    // global best so far

    for (i in 1 until nums.size) {
        val x = nums[i]

        // Case 1: start fresh with x. Case 2: extend the previous max. Case 3: flip the previous min.
        val newMax = maxOf(x, maxEnd * x, minEnd * x)
        val newMin = minOf(x, maxEnd * x, minEnd * x)

        maxEnd = newMax
        minEnd = newMin
        result = maxOf(result, maxEnd)
    }
    return result
}
```

```java
public class MaximumProductSubarray {
    /**
     * @param nums the integer array (may contain negatives and zeros)
     * @return     the maximum product of any contiguous non-empty subarray
     */
    public int maxProduct(int[] nums) {
        int maxEnd = nums[0], minEnd = nums[0], result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            int newMax = Math.max(x, Math.max(maxEnd * x, minEnd * x));
            int newMin = Math.min(x, Math.min(maxEnd * x, minEnd * x));
            maxEnd = newMax;
            minEnd = newMin;
            result = Math.max(result, maxEnd);
        }
        return result;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaximumProductSubarray {
public:
    /**
     * @param nums the integer array (may contain negatives and zeros)
     * @return     the maximum product of any contiguous non-empty subarray
     */
    int maxProduct(const std::vector<int>& nums) {
        int maxEnd = nums[0], minEnd = nums[0], result = nums[0];
        for (int i = 1; i < (int)nums.size(); i++) {
            int x = nums[i];
            int newMax = std::max({x, maxEnd * x, minEnd * x});
            int newMin = std::min({x, maxEnd * x, minEnd * x});
            maxEnd = newMax;
            minEnd = newMin;
            result = std::max(result, maxEnd);
        }
        return result;
    }
};
```

```python
def max_product(nums: list[int]) -> int:
    """
    @param nums: the integer array (may contain negatives and zeros)
    @return:     the maximum product of any contiguous non-empty subarray
    """
    max_end = min_end = result = nums[0]
    for x in nums[1:]:
        new_max = max(x, max_end * x, min_end * x)   # start fresh / extend max / flip min
        new_min = min(x, max_end * x, min_end * x)
        max_end, min_end = new_max, new_min
        result = max(result, max_end)
    return result
```

```rust
impl Solution {
    /// @param nums the integer array (may contain negatives and zeros)
    /// @return     the maximum product of any contiguous non-empty subarray
    pub fn max_product(nums: Vec<i32>) -> i32 {
        let (mut max_end, mut min_end, mut result) = (nums[0], nums[0], nums[0]);
        for &x in &nums[1..] {
            let new_max = x.max(max_end * x).max(min_end * x);
            let new_min = x.min(max_end * x).min(min_end * x);
            max_end = new_max;
            min_end = new_min;
            result = result.max(max_end);
        }
        result
    }
}
```

> **Note on the repository file:** the repo's `MaximumProductSubarray.kt` contains a stray `val m = mapOf(1 to 2, 2 to 4, 5 to 6)` scratch line — leftover experimentation, harmless but irrelevant. The book listing above is the same algorithm, clean. (Removing scratch is a "clean up your own mess" courtesy; the logic is untouched.)

## Dry run

**Input:** `nums = [-2, 3, -4]`

```
i=0: maxEnd = -2, minEnd = -2, result = -2
i=1 (x=3):
  newMax = max(3, -2*3 = -6, -2*3 = -6) = 3
  newMin = min(3, -6, -6) = -6
  maxEnd = 3, minEnd = -6, result = max(-2, 3) = 3
i=2 (x=-4):
  newMax = max(-4, 3*-4 = -12, -6*-4 = 24) = 24      <- the FLIP: min * negative = 24
  newMin = min(-4, -12, 24) = -12
  maxEnd = 24, minEnd = -12, result = max(3, 24) = 24
Answer: 24 ✓
```

The critical line is `i=2`: only `minEnd = -6` (the *worst* product ending at index 1) could become 24 when multiplied by -4. The naive max-only DP would have answered 3 — wrong by 8×.

**Input with a zero:** `nums = [-2, 0, -1]`

```
i=0: maxEnd = minEnd = result = -2
i=1 (x=0):
  newMax = max(0, -2*0, -2*0) = 0      <- zero resets both streaks
  newMin = min(0, 0, 0) = 0
  maxEnd = 0, minEnd = 0, result = 0
i=2 (x=-1):
  newMax = max(-1, 0*-1, 0*-1) = 0     <- the subarray [0] beats [-1] and [0,-1]
  result stays 0
Answer: 0 ✓
```

Note the zero handling is *free*: `max(x, ...)` with `x = 0` naturally starts a fresh streak, which is why "subarray containing a zero" cases never need special-casing.

## Complexity

**Time.** One pass, $O(1)$ per element:

$$
T(n) = O(n)
$$

**Space.** $O(1)$ — three scalars.

## Variants & follow-ups

- **Maximum Sum Subarray / Kadane's algorithm** (`src/main/kotlin/array/dp/KadensAlgorithm.kt`, `MaximumSumSubArray.kt`) — the sum version needs only ONE tracker, because addition is sign-agnostic ("the max prefix sum can only help"). Saying *why* sum needs one tracker but product needs two is the interview payoff.
- **Interview follow-up:** "What if all values are negative?" The `max(x, ...)` term handles it: the best product is either a *pair* of negatives or a *triple* that ends negative-or-positive — e.g. `[-1, -2, -3]`: at i=1 the best is `[-1, -2] = 2`; at i=2, `max(-3, 2·-3, 6·-3) = -3`, so the running max stays 2... but wait — `[-2, -3] = 6` isn't captured by "ending at i" trackers at i=1! Let's trace honestly: i=1 trackers only see subarrays ending at index 1: `[-2]` and `[-1,-2]` → maxEnd=2, minEnd=-2. At i=2 (x=-3): candidates are `[-3]`, `2·-3=-6` (extend [-1,-2]), `-2·-3=6` (extend [-2]) → newMax=6. So result = max(2, 6) = 6 ✓. The two-track DP *does* capture `[-2,-3]` because `minEnd=-2` at i=1 *is* the subarray `[-2]`. So: all-negative arrays are handled with zero special-casing — the min track is what makes it work.
- **Interview follow-up:** "Prove tracking min is sufficient." Any product ending at `i` is `nums[i]` times a product ending at `i-1` (or fresh). Products ending at `i-1` range between minEnd and maxEnd (they're all products of a contiguous suffix); multiplying by a *fixed sign* x moves the extremes of that range to `minEnd·x` and `maxEnd·x`. So the two extremes capture the entire range — nothing else is needed. That's the formal argument.
