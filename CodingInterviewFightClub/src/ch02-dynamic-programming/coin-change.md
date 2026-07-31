# 2.16 Coin Change

> **Source:** [`src/main/kotlin/array/dp/CoinChange.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/CoinChange.kt) (+ `CoinChangeBottomUp.kt`, `CoinChange_II.kt`, `CoinChangeBFS.kt` — four repo implementations)
> **Pattern:** unbounded-knapsack minimization · **Core page**

## The Problem

Given `coins` (denominations, unlimited supply) and an `amount`, return the **fewest coins** that make that amount, or `-1`.

- Constraints: $1 \le$ coins ≤ 12; amount ≤ 10⁴; coin values ≤ 2³¹.

## Examples

```
Input:  coins = [1,2,5], amount = 11   -> Output: 3   (5 + 5 + 1)
Input:  coins = [2], amount = 3        -> Output: -1  (impossible)
```

## Intuition — `dp[a]` = fewest coins for amount a; every coin is one more step

The classic unbounded-knapsack minimization. Define `dp[a]` = minimum coins to make exactly `a`. The last coin chosen is some `c ≤ a`, so:

$$
dp[a] = 1 + \min_{c \in coins,\; c \le a} dp[a - c]
$$

**Top-down** (the repo's `CoinChange.kt`): `coinChange(amount)` recurses on `amount - coin`; the `dp` array is the memo. **Bottom-up** (`CoinChangeBottomUp.kt` / `coinChangeCleanAf`): fill `dp[1..amount]` in order — every subproblem's answer is already computed because `a - c < a`. Both are the [2.4](zero-one-knapsack.md) unbounded shape; the difference is max-value → min-count.

**The `amount + 1` sentinel** — `dp` initialized to `amount + 1` (an impossible count) makes "can't reach" self-evident: if `dp[amount]` is still the sentinel, return `-1`. No separate visited bookkeeping.

**The BFS variant** (`CoinChangeBFS.kt`) — each "amount" is a node, each coin an edge `a → a - c`; the first time `0` is reached gives the fewest coins. Same complexity, a different mental model.

## Approach 1 — Greedy (fails!)

Take the largest coin first: `[1,3,4], amount = 6` → greedy picks 4+1+1 (3 coins); optimal is 3+3 (2). Coin systems aren't canonical — this is the [11.0](../ch11-greedy/pattern-primer.md) greedy-fails red flag.

## Approach 2 — Bottom-up DP (the repo's versions, optimal)

```kotlin
class CoinChange {
    /**
     * @param coins  denominations (unlimited supply)
     * @param amount target amount
     * @return       fewest coins, or -1 if impossible
     */
    fun coinChange(coins: IntArray, amount: Int): Int {
        val maxVal = amount + 1                          // sentinel: impossible
        val dp = IntArray(amount + 1) { maxVal }
        dp[0] = 0

        for (i in 1..amount) {
            for (coin in coins) {
                if (coin <= i) {
                    dp[i] = minOf(dp[i], dp[i - coin] + 1)
                }
            }
        }
        return dp[amount].takeIf { it <= amount } ?: -1
    }
}
```

```java
public class CoinChange {
    /**
     * @param coins  denominations (unlimited supply)
     * @param amount target amount
     * @return       fewest coins, or -1 if impossible
     */
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);                     // sentinel: impossible
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int c : coins) {
                if (c <= i) dp[i] = Math.min(dp[i], dp[i - c] + 1);
            }
        }
        return dp[amount] <= amount ? dp[amount] : -1;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class CoinChange {
public:
    /**
     * @param coins  denominations (unlimited supply)
     * @param amount target amount
     * @return       fewest coins, or -1 if impossible
     */
    int coinChange(std::vector<int>& coins, int amount) {
        std::vector<int> dp(amount + 1, amount + 1);     // sentinel: impossible
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int c : coins) {
                if (c <= i) dp[i] = std::min(dp[i], dp[i - c] + 1);
            }
        }
        return dp[amount] <= amount ? dp[amount] : -1;
    }
};
```

```python
def coin_change(coins: list[int], amount: int) -> int:
    """
    @param coins:  denominations (unlimited supply)
    @param amount: target amount
    @return:       fewest coins, or -1 if impossible
    """
    dp = [amount + 1] * (amount + 1)     # sentinel: impossible
    dp[0] = 0

    for i in range(1, amount + 1):
        for c in coins:
            if c <= i:
                dp[i] = min(dp[i], dp[i - c] + 1)

    return dp[amount] if dp[amount] <= amount else -1
```

```rust
impl Solution {
    /// @param coins  denominations (unlimited supply)
    /// @param amount target amount
    /// @return       fewest coins, or -1 if impossible
    pub fn coin_change(coins: Vec<i32>, amount: i32) -> i32 {
        let amount = amount as usize;
        let mut dp = vec![amount + 1; amount + 1];   // sentinel: impossible
        dp[0] = 0;

        for i in 1..=amount {
            for &c in &coins {
                if (c as usize) <= i {
                    dp[i] = dp[i].min(dp[i - c as usize] + 1);
                }
            }
        }
        if dp[amount] <= amount { dp[amount] as i32 } else { -1 }
    }
}
```

## Dry run

**Input:** `coins = [1,2,5]`, `amount = 11`.

```
dp[0]=0
dp[1] = 1 + dp[0] = 1.   dp[2] = min(1+dp[1], 1+dp[0]) = min(2,1) = 1.
dp[3] = min(1+dp[2], 1+dp[1]) = 2.   dp[4] = min(1+dp[3], 1+dp[2]) = 2.
dp[5] = min(1+dp[4], 1+dp[3], 1+dp[0]) = min(3,3,1) = 1.   (one 5-cent coin!)
dp[6] = min(1+dp[5], 1+dp[4], 1+dp[1]) = 2.
dp[7] = 2.  dp[8] = min(1+dp[7],1+dp[6],1+dp[3]) = 3.
dp[9] = 3.  dp[10] = min(1+dp[9],1+dp[8],1+dp[5]) = 2.
dp[11] = min(1+dp[10],1+dp[9],1+dp[6]) = min(3,4,3) = 3.

Output: 3 ✓   (5 + 5 + 1)
```

The `min` over coins is the whole algorithm: each `dp[i]` re-uses the best answer for `i - c`, and the sentinel `12` never propagates into reachable cells. `coins = [2], amount = 3` → `dp[3]` stays the sentinel → `-1` ✓.

## Complexity

**Time.** Amount × coins:

$$
T(A, C) = O(A \cdot C)
$$

**Space.** The dp array:

$$
S(A) = O(A)
$$

## Variants & follow-ups

- **Coin Change II** (`array/dp/CoinChange_II.kt`, also `CoinChange_II_BottomUp.kt`) — count the *ways* instead of the minimum: `dp[c] += dp[c - coin]` with the coin loop outside (order matters — see the [2.6](partition-equal-subset-sum.md) ascending-vs-descending discussion).
- **Minimum Number Of Refueling Stops** ([11.7](../ch11-greedy/minimum-number-of-refueling-stops.md)) — the greedy twin: reachability with a max-heap instead of a table.
- **Interview follow-up:** "Why does the coin loop need the `coin <= i` guard?" `dp[i - coin]` would index below 0 for a coin larger than the current amount — the guard is the bounds check that keeps the recurrence valid. (And why greedy fails: `[1,3,4], 6` → 4+1+1 vs 3+3, because non-canonical systems break the "largest-first" assumption.)
