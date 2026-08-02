# 2.29 Min Cost Climbing Stairs

> **Source**: [`src/main/kotlin/array/dp/MinCostClimbingStaris.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/MinCostClimbingStaris.kt)
> **Pattern**: two-step DP · **Core page**

## The Problem

Climb to the top paying `cost[i]` per step; take 1 or 2 steps. Min cost.

- Constraints: n ≥ 2.

## Examples

```
Input:  cost = [10,15,20]      -> Output: 15   (start at 1, pay 15, jump past top)
Input:  cost = [1,100,1,1,1,100,1,1,100,1]  -> Output: 6
```

## Intuition — the cheapest way to stand on step i

`dp[i]` = min cost to reach step i = `cost[i] + min(dp[i-1], dp[i-2])` — the [2.4](house-robber.md) neighbor-choice DP:

```kotlin
val dp = IntArray(cost.size)
dp[0] = cost[0]
dp[1] = cost[1]

for (i in 2 until cost.size) {
    dp[i] = cost[i] + minOf(dp[i - 1], dp[i - 2])
}
return minOf(dp[cost.size - 1], dp[cost.size - 2])   // finish from either top step
```

**Why return the min of the last two?** The top is *past* the last step — you can finish from step n-1 or n-2 (one 2-step jump). The answer is the cheaper landing.

**Why the [2.4](house-robber.md) shape?** Each step's best depends only on the previous two — a linear DP with constant lookback.

## Approach 1 — Full DP array (the repo's version)

## Approach 2 — Two rolling variables (O(1) space)

`prev2, prev1` updated per step — same recurrence, no array.

```kotlin
class MinCostClimbingStaris {
    /**
     * @param cost step costs
     * @return     min cost to reach the top
     */
    fun minCostClimbingStairs(cost: IntArray): Int {
        if (cost.size <= 2) return cost.min()

        val dp = IntArray(cost.size)
        dp[0] = cost[0]
        dp[1] = cost[1]

        for (i in 2 until cost.size) {
            dp[i] = cost[i] + minOf(dp[i - 1], dp[i - 2])
        }
        return minOf(dp[cost.size - 1], dp[cost.size - 2])
    }
}
```

```java
public class MinCostClimbingStairs {
    /**
     * @param cost step costs
     * @return     min cost to reach the top
     */
    public int minCostClimbingStairs(int[] cost) {
        int prev2 = cost[0], prev1 = cost[1];

        for (int i = 2; i < cost.length; i++) {
            int cur = cost[i] + Math.min(prev1, prev2);
            prev2 = prev1;
            prev1 = cur;
        }
        return Math.min(prev1, prev2);
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MinCostClimbingStairs {
public:
    /**
     * @param cost step costs
     * @return     min cost to reach the top
     */
    int minCostClimbingStairs(std::vector<int>& cost) {
        int prev2 = cost[0], prev1 = cost[1];

        for (int i = 2; i < (int)cost.size(); i++) {
            int cur = cost[i] + std::min(prev1, prev2);
            prev2 = prev1;
            prev1 = cur;
        }
        return std::min(prev1, prev2);
    }
};
```

```python
def min_cost_climbing_stairs(cost: list[int]) -> int:
    """
    @param cost: step costs
    @return:     min cost to reach the top
    """
    prev2, prev1 = cost[0], cost[1]

    for c in cost[2:]:
        prev2, prev1 = prev1, c + min(prev1, prev2)

    return min(prev1, prev2)
```

```rust
impl Solution {
    /// @param cost step costs
    /// @return     min cost to reach the top
    pub fn min_cost_climbing_stairs(cost: Vec<i32>) -> i32 {
        let mut prev2 = cost[0];
        let mut prev1 = cost[1];

        for &c in cost.iter().skip(2) {
            let cur = c + prev1.min(prev2);
            prev2 = prev1;
            prev1 = cur;
        }
        prev1.min(prev2)
    }
}
```

## Reading the code — what's actually happening

```kotlin
val dp = IntArray(cost.size)
dp[0] = cost[0]
dp[1] = cost[1]
for (i in 2 until cost.size) {
    dp[i] = cost[i] + minOf(dp[i - 1], dp[i - 2])
}
return minOf(dp[cost.size - 1], dp[cost.size - 2])
```

Stand on step `i` and ask: "what's the cheapest way I could have gotten here?" You arrived either from step `i-1` (one step) or step `i-2` (two steps) — so the answer is this step's cost **plus the cheaper of those two arrival costs**.

- **`dp[0] = cost[0]` and `dp[1] = cost[1]` are the hand-placed bases.** The problem lets you *start* on step 0 or step 1 for free (no cost to begin), so the cheapest way to "be on" step 0 is just paying `cost[0]`, and likewise step 1. Steps 0 and 1 can't be reached by stepping onto them, so they can't use the recurrence.
- **`dp[i] = cost[i] + min(dp[i-1], dp[i-2])` is the two-step lookback.** Each step looks only two steps behind — a *linear* recurrence with constant history, which is why this whole problem needs O(1) memory (the rolling `prev2/prev1` variant) even though the code above uses a full array.
- **`return min(dp[n-1], dp[n-2])` is the "past the top" finish.** The top of the stairs is *beyond* the last step. From step `n-1` you can finish with a 1-step; from step `n-2` with a 2-step. Whichever arrival is cheaper is the answer — you never pay for a step past the end.

Trace `[10,15,20]`: `dp[0]=10, dp[1]=15`; `dp[2] = 20 + min(15,10) = 30`; answer `min(dp[2], dp[1]) = min(30,15) = 15` ✓ — start on step 1 (pay 15) and take the 2-step over the top.

## Dry run

**Input:** `cost = [10,15,20]`.

```
dp[0]=10, dp[1]=15.
i=2: dp[2] = 20 + min(15, 10) = 30.
return min(30, 15) = 15 ✓  (start at 1, pay 15, 2-step to the top)
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** O(n) or O(1):

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Climbing Stairs** — the no-cost counting twin.
- **House Robber** ([2.4](house-robber.md)) — the same two-step lookback DP.
- **Interview follow-up:** "Why the min of the last two dp entries?" The top isn't a paid step — it's reached from either of the last two. Ending the recurrence one step early and taking the cheaper landing IS the finish rule.
