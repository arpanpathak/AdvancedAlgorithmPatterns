# 2.30 Coin Change II

> **Source**: [`src/main/kotlin/array/dp/CoinChange_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/CoinChange_II.kt)
> **Pattern**: unbounded-knapsack combinations · **Core page**

## The Problem

The **number of combinations** making `amount` (unlimited coins).

- Constraints: amount ≤ 5000; coins ≤ 300.

## Examples

```
Input:  amount = 5, coins = [1,2,5]   -> Output: 4   (5, 2+2+1, 2+1+1+1, 1+1+1+1+1)
```

## Intuition — the [2.16](coin-change.md) table, counting instead of minimizing

`dp[i][j]` = combinations using the first i coins for amount j = skip the coin + use it (stay on the same coin row — unbounded):

```kotlin
val dp = Array(amount + 1) { IntArray(coins.size) { -1 } }
return change(dp, amount, coins, 0)

fun change(dp, amount, coins, i): Int = when {
    amount < 0 || (i == coins.size && amount > 0) -> 0
    amount == 0 -> 1
    else -> {
        if (dp[amount][i] != -1) dp[amount][i]!!
        else {
            dp[amount][i] = change(dp, amount - coins[i], coins, i) +   // take (unbounded)
                            change(dp, amount, coins, i + 1)             // skip
            dp[amount][i]!!
        }
    }
}
```

**Why `stay on i` for the take branch?** Unbounded coins — taking a coin doesn't advance the coin index. The [2.16](coin-change.md) memoized DP with `+` instead of `min` and a count base case.

## Approach 1 — Memoized include/exclude (the repo's version)

## Approach 2 — 1-D table (the `_BottomUp` file, optimal)

`dp[j]` += `dp[j - coin]` for each coin — the unbounded-knapsack counting order.

```kotlin
class CoinChange_II {
    /**
     * @param amount target amount
     * @param coins  coin values
     * @return       number of combinations
     */
    fun change(amount: Int, coins: IntArray): Int {
        val dp = Array(amount + 1) { IntArray(coins.size) { -1 } }
        return change(dp, amount, coins, 0)
    }

    private fun change(dp: Array<IntArray>, amount: Int, coins: IntArray, i: Int): Int {
        return when {
            amount < 0 || (i == coins.size && amount > 0) -> 0
            amount == 0 -> 1
            else -> {
                if (dp[amount][i] != -1) dp[amount][i]!!
                else {
                    dp[amount][i] = change(dp, amount - coins[i], coins, i) +
                                    change(dp, amount, coins, i + 1)
                    dp[amount][i]!!
                }
            }
        }
    }
}
```

```java
import java.util.*;

public class CoinChangeII {
    private int[][] memo;

    private int solve(int[] coins, int amount, int i) {
        if (amount == 0) return 1;
        if (amount < 0 || i == coins.length) return 0;

        if (memo[amount][i] != -1) return memo[amount][i];

        return memo[amount][i] = solve(coins, amount - coins[i], i)     // take
                               + solve(coins, amount, i + 1);           // skip
    }

    /**
     * @param amount target amount
     * @param coins  coin values
     * @return       number of combinations
     */
    public int change(int amount, int[] coins) {
        memo = new int[amount + 1][coins.length];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solve(coins, amount, 0);
    }
}
```

```cpp
#include <vector>
#include <cstring>

class CoinChangeII {
public:
    /**
     * @param amount target amount
     * @param coins  coin values
     * @return       number of combinations
     */
    int change(int amount, std::vector<int>& coins) {
        std::vector<long> dp(amount + 1, 0);
        dp[0] = 1;

        for (int coin : coins) {                    // coin outer: combinations (orderless)
            for (int j = coin; j <= amount; j++) {
                dp[j] += dp[j - coin];
            }
        }
        return (int)dp[amount];
    }
};
```

```python
def change(amount: int, coins: list[int]) -> int:
    """
    @param amount: target amount
    @param coins:  coin values
    @return:       number of combinations
    """
    dp = [0] * (amount + 1)
    dp[0] = 1

    for coin in coins:                      # coin outer: combinations (orderless)
        for j in range(coin, amount + 1):
            dp[j] += dp[j - coin]

    return dp[amount]
```

```rust
impl Solution {
    /// @param amount target amount
    /// @param coins  coin values
    /// @return       number of combinations
    pub fn change(amount: i32, coins: Vec<i32>) -> i32 {
        let mut dp = vec![0u64; amount as usize + 1];
        dp[0] = 1;

        for coin in coins {
            for j in (coin as usize)..=(amount as usize) {
                dp[j] += dp[j - coin as usize];
            }
        }
        dp[amount as usize] as i32
    }
}
```

## Dry run

**Input:** `amount = 5, coins = [1,2,5]` (1-D version).

```
dp = [1,0,0,0,0,0]
coin 1: dp[1..5] += dp[j-1] -> [1,1,1,1,1,1]
coin 2: dp[2]=1+dp[0]=2.  dp[3]=1+dp[1]=2.  dp[4]=1+dp[2]=3.  dp[5]=1+dp[3]=3.
coin 5: dp[5]=3+dp[0]=4.
Output: 4 ✓
```

The coin-outer loop is what makes it *combinations*: each coin's pass adds uses of that coin to existing amounts — order doesn't matter (a permutation-counting version would loop amount-outer).

## Complexity

**Time.** Coins × amount:

$$
T(c, a) = O(c \cdot a)
$$

**Space.** The 1-D table:

$$
S(a) = O(a)
$$

## Variants & follow-ups

- **Coin Change** ([2.16](coin-change.md)) — minimize vs count.
- **01 Knapsack / Unbounded Knapsack** — the same table's siblings.
- **Interview follow-up:** "Why does the coin-outer loop count combinations?" Amount-outer would count *sequences* (1+2 and 2+1 separately). Fixing the coin order means each combination is built in sorted-coin order exactly once — the canonical unbounded-knapsack counting order.
