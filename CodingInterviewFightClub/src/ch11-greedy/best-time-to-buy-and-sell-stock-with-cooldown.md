# 11.18 Best Time To Buy And Sell Stock With Cooldown

> **Source**: [`src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStockWithCooldown.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStockWithCooldown.kt)
> **Pattern**: buy-index memo · **Core page**

## The Problem

Unlimited transactions, but **one day cooldown** after selling before the next buy.

- Constraints: n ≤ 5000.

## Examples

```
Input:  prices = [1,2,3,0,2]   -> Output: 3   (buy 1, sell 2; cooldown; buy 0, sell 2)
```

## Intuition — the decision at each buy: which sell day maximizes this trade + the rest

The repo's buy-index memo: `maxProfit(buyAt)` = best total from buying at `buyAt`. For each possible `sellAt`, the profit is `prices[sellAt] - prices[buyAt] + maxProfit(sellAt + 2)` (the +2 = cooldown day):

```kotlin
fun maxProfit(prices: IntArray, buyAt: Int): Int {
    return when {
        buyAt > prices.lastIndex -> 0
        dp.containsKey(buyAt) -> dp[buyAt]!!
        else -> {
            var maxProfit = 0
            for (sellAt in buyAt + 1..prices.lastIndex) {
                maxProfit = maxOf(
                    maxProfit,
                    prices[sellAt] - prices[buyAt] + maxProfit(prices, sellAt + 2)  // +1 cooldown
                )
            }
            maxProfit
        }
    }
}
```

**Why `sellAt + 2`?** After selling at `sellAt`, the next buy can't be until `sellAt + 2` (one cooldown day). The recursion's index IS the state — [2.0](../ch02-dynamic-programming/pattern-primer.md) memoized DP with the cooldown baked into the transition.

**Why enumerate sell days?** The trade's profit depends on the chosen sell; trying each and taking the max is the brute-force-optimal — memoized over buy days only (O(n²)).

## Approach 1 — Buy-index memo (the repo's version)

```kotlin
class BestTimeToBuyAndSellStockWithCooldown {
    val dp = mutableMapOf<Int, Int>()

    /**
     * @param prices daily prices
     * @return      max profit with one-day cooldown
     */
    fun maxProfit(prices: IntArray): Int {
        return maxProfit(prices, 0)
    }

    fun maxProfit(prices: IntArray, buyAt: Int): Int {
        return when {
            buyAt > prices.lastIndex -> 0
            dp.containsKey(buyAt) -> dp[buyAt]!!
            else -> {
                var maxProfit = 0
                for (sellAt in buyAt + 1..prices.lastIndex) {
                    maxProfit = maxOf(
                        maxProfit,
                        prices[sellAt] - prices[buyAt] + maxProfit(prices, sellAt + 2)
                    )
                }
                maxProfit
            }
        }
    }
}
```

```java
import java.util.*;

public class BestTimeToBuyAndSellStockWithCooldown {
    private Map<Integer, Integer> memo = new HashMap<>();

    private int solve(int[] prices, int buyAt) {
        if (buyAt >= prices.length) return 0;
        if (memo.containsKey(buyAt)) return memo.get(buyAt);

        int best = 0;
        for (int sellAt = buyAt + 1; sellAt < prices.length; sellAt++) {
            best = Math.max(best, prices[sellAt] - prices[buyAt] + solve(prices, sellAt + 2));
        }
        memo.put(buyAt, best);
        return best;
    }

    /**
     * @param prices daily prices
     * @return      max profit with one-day cooldown
     */
    public int maxProfit(int[] prices) {
        return solve(prices, 0);
    }
}
```

```cpp
#include <vector>
#include <unordered_map>

class BestTimeToBuyAndSellStockWithCooldown {
    std::unordered_map<int, int> memo;

    int solve(std::vector<int>& prices, int buyAt) {
        if (buyAt >= (int)prices.size()) return 0;
        if (memo.count(buyAt)) return memo[buyAt];

        int best = 0;
        for (int sellAt = buyAt + 1; sellAt < (int)prices.size(); sellAt++) {
            best = std::max(best, prices[sellAt] - prices[buyAt] + solve(prices, sellAt + 2));
        }
        return memo[buyAt] = best;
    }

public:
    /**
     * @param prices daily prices
     * @return      max profit with one-day cooldown
     */
    int maxProfit(std::vector<int>& prices) {
        return solve(prices, 0);
    }
};
```

```python
from functools import lru_cache

def max_profit(prices: list[int]) -> int:
    """
    @param prices: daily prices
    @return:       max profit with one-day cooldown
    """
    @lru_cache(None)
    def solve(buy_at: int) -> int:
        if buy_at >= len(prices):
            return 0
        return max(
            [0] + [prices[sell] - prices[buy_at] + solve(sell + 2)
                   for sell in range(buy_at + 1, len(prices))]
        )

    return solve(0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param prices daily prices
    /// @return      max profit with one-day cooldown
    pub fn max_profit(prices: Vec<i32>) -> i32 {
        let n = prices.len();
        let mut memo: HashMap<usize, i32> = HashMap::new();

        fn solve(prices: &Vec<i32>, buy_at: usize, memo: &mut HashMap<usize, i32>) -> i32 {
            if buy_at >= prices.len() { return 0; }
            if let Some(&v) = memo.get(&buy_at) { return v; }

            let mut best = 0;
            for sell in (buy_at + 1)..prices.len() {
                best = best.max(prices[sell] - prices[buy_at] + solve(prices, sell + 2, memo));
            }
            memo.insert(buy_at, best);
            best
        }

        solve(&prices, 0, &mut memo)
    }
}
```

## Dry run

**Input:** `prices = [1,2,3,0,2]`.

```
solve(0): try sells:
  sell 1 (price 2): 1 + solve(3).  solve(3): buy 0: sell 4 (2): 2 + solve(5)=0 -> 2.
    total 1 + 2 = 3.
  sell 2 (3): 2 + solve(4): buy 2: no sells -> 0.  total 2.
  sell 3 (0): -1 + ... negative.
  best = 3 ✓
```

The `sellAt + 2` jump is the cooldown: after selling at day 1, the recursion restarts at day 3 (skipping day 2's rest). The memo on `buyAt` collapses repeated subproblems (the same buy day reached via different earlier trades).

## Complexity

**Time.** O(n²) states × transitions:

$$
T(n) = O(n^2)
$$

**Space.** The memo:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Best Time I / III** ([11.16](best-time-to-buy-and-sell-stock.md), [11.17](best-time-to-buy-and-sell-stock-iii.md)) — the family ancestors.
- **With Transaction Fee** ([11.19](best-time-to-buy-and-sell-stock-with-transaction-fee.md)) — the hold/cash state machine.
- **Interview follow-up:** "Why not the hold/cash machine here?" It works too — `hold`/`cash` with a cooldown delay. The buy-index memo is the *recursive* spelling: state = the next buy day, transition = choose the sell. Both are O(n²); the machine is O(n).
