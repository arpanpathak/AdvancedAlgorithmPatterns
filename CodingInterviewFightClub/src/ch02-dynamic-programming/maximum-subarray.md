# 2.18 Maximum Subarray (Kadane's Algorithm)

> **Source:** [`src/main/kotlin/array/dp/KadensAlgorithm.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/KadensAlgorithm.kt) (+ `array/dp/MaximumSumSubArray.kt`)
> **Pattern:** running best-ending-here · **Core page**

## The Problem

Given `nums` (may be negative), find the **maximum sum** of any contiguous subarray.

- Constraints: $1 \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  nums = [-2,1,-3,4,-1,2,1,-5,4]   -> Output: 6   (the subarray [4,-1,2,1])
Input:  nums = [1]                       -> Output: 1
```

## Intuition — at each index, the best subarray *ending here* is either "this element alone" or "this element + the best ending before"

The recurrence that makes Kadane famous:

$$
\text{bestEnding}[i] = \max(nums[i],\; \text{bestEnding}[i-1] + nums[i])
$$

**Why `max(nums[i], ...)`?** If the running sum went *negative* (or any sum below the bare element), restarting at `nums[i]` beats extending — negative prefixes are always discarded. This is the "carry or restart" decision, and the repo's `dp[0]`/`dp[1]` two-slot array is just `bestEnding` and the global `best`:

```
dp[0] = max(nums[i], dp[0] + nums[i])   # best subarray ENDING at i
dp[1] = max(dp[1], dp[0])               # best subarray ANYWHERE up to i
```

**Why is this DP (not greedy)?** It has the optimal-substructure flavor: the global answer is the max over all `bestEnding[i]` — the [2.1](longest-common-substring.md) "local definition, global max" idiom in its purest 1-D form. The O(1) space is the [2.17](house-robber.md) rolling-variable compression.

**All-negative arrays** are handled by the `max(nums[i], ...)` restart: the answer is the *least negative* element — the algorithm never returns 0 for `[-5,-2]` (it returns -2).

## Approach 1 — All subarrays (O(n^2))

Double loop over every window: correct, quadratic — the baseline Kadane kills.

## Approach 2 — Kadane's one pass (the repo's version, optimal)

```kotlin
class KadensAlgorithm {
    /**
     * @param nums input array (may be negative)
     * @return     max sum of a contiguous subarray
     */
    fun maxSubArray(nums: IntArray): Int {
        if (nums.isEmpty()) return 0

        // dp[0]: max sum ending at the current index
        // dp[1]: overall max sum found so far
        val dp = IntArray(2)
        dp[0] = nums[0]
        dp[1] = nums[0]

        for (i in 1 until nums.size) {
            dp[0] = maxOf(nums[i], dp[0] + nums[i])   // carry or restart
            dp[1] = maxOf(dp[1], dp[0])               // update the global best
        }
        return dp[1]
    }
}
```

```java
public class MaximumSubarray {
    /**
     * @param nums input array (may be negative)
     * @return     max sum of a contiguous subarray
     */
    public int maxSubArray(int[] nums) {
        int bestEnding = nums[0], best = nums[0];

        for (int i = 1; i < nums.length; i++) {
            bestEnding = Math.max(nums[i], bestEnding + nums[i]);   // carry or restart
            best = Math.max(best, bestEnding);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaximumSubarray {
public:
    /**
     * @param nums input array (may be negative)
     * @return     max sum of a contiguous subarray
     */
    int maxSubArray(std::vector<int>& nums) {
        int bestEnding = nums[0], best = nums[0];

        for (int i = 1; i < (int)nums.size(); i++) {
            bestEnding = std::max(nums[i], bestEnding + nums[i]);   // carry or restart
            best = std::max(best, bestEnding);
        }
        return best;
    }
};
```

```python
def max_sub_array(nums: list[int]) -> int:
    """
    @param nums: input array (may be negative)
    @return:     max sum of a contiguous subarray
    """
    best_ending = best = nums[0]

    for num in nums[1:]:
        best_ending = max(num, best_ending + num)   # carry or restart
        best = max(best, best_ending)
    return best
```

```rust
impl Solution {
    /// @param nums input array (may be negative)
    /// @return     max sum of a contiguous subarray
    pub fn max_sub_array(nums: Vec<i32>) -> i32 {
        let mut best_ending = nums[0];
        let mut best = nums[0];

        for &num in &nums[1..] {
            best_ending = num.max(best_ending + num);   // carry or restart
            best = best.max(best_ending);
        }
        best
    }
}
```

## Reading the code — what's actually happening

```kotlin
val dp = IntArray(2)
dp[0] = nums[0]
dp[1] = nums[0]
for (i in 1 until nums.size) {
    dp[0] = maxOf(nums[i], dp[0] + nums[i])   // carry or restart
    dp[1] = maxOf(dp[1], dp[0])               // update the global best
}
return dp[1]
```

The two-slot array holds two very different things, and keeping them straight is the whole trick:

- **`dp[0]` = best subarray sum *ending exactly at the current index*.** This is the "carry or restart" decision: either extend the previous best-ending subarray (`dp[0] + nums[i]`) or abandon it and start fresh at `nums[i]` alone. The `max` picks whichever is larger. Why is restart ever right? If the carried sum is negative, adding it to `nums[i]` only drags the total down — a negative prefix can never help a later subarray, so it's discarded. (That's also why all-negative arrays work: every step restarts, and the answer is the least-negative element, never 0.)
- **`dp[1]` = best subarray sum *anywhere up to the current index*.** This is the global champion: the max of every `dp[0]` seen so far. It only ever increases — it's a running maximum over the local answers.
- **Why one pass is enough:** any maximum subarray must *end* somewhere; its value was, at that moment, a `dp[0]` candidate. So scanning all endings and keeping the max over them captures every possible subarray — no window enumeration needed. That's the optimal-substructure property: the global answer is the max of the local definitions.

Trace `[-2,1,-3,4,-1,2,1,-5,4]`: endings go -2, 1, -2, 4, 3, 5, 6, 1, 5 — the global best climbs 1 → 4 → 5 → 6 and stays 6 through the trailing `-5,4`. Answer 6 ✓ (the subarray `[4,-1,2,1]`).

## Dry run

**Input:** `nums = [-2,1,-3,4,-1,2,1,-5,4]`.

```
bestEnding=best=-2
i=1 (1):  bestEnding = max(1, -2+1) = 1.   best = max(-2, 1) = 1
i=2 (-3): bestEnding = max(-3, 1-3) = -2.  best = 1
i=3 (4):  bestEnding = max(4, -2+4) = 4.   best = 4
i=4 (-1): bestEnding = max(-1, 4-1) = 3.   best = 4
i=5 (2):  bestEnding = max(2, 3+2) = 5.    best = 5
i=6 (1):  bestEnding = max(1, 5+1) = 6.    best = 6
i=7 (-5): bestEnding = max(-5, 6-5) = 1.   best = 6
i=8 (4):  bestEnding = max(4, 1+4) = 5.    best = 6

Output: 6 ✓   (the subarray [4,-1,2,1])
```

The "carry or restart" moments: at i=2 the carry goes negative (`-2`) — the i=3 restart at 4 discards it, exactly as Kadane intends. The global `best` only ever increases; the trailing `-5,4` can't beat 6.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Two variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Maximum Product Subarray** ([2.7](maximum-product-subarray.md)) — the sign-flip cousin: track max *and* min because negatives invert.
- **Split Array Largest Sum** (`array/dp/SplitArrayLargestSum.kt`) — "minimize the largest subarray sum": binary search + greedy feasibility, the [1.2](../ch01-binary-search/capacity-to-ship-packages.md) shape.
- **Interview follow-up:** "Can you return the subarray itself?" Track `start` reset on restart and `bestStart/bestEnd` when `best` updates — the same two-variable DP with two more indices.
