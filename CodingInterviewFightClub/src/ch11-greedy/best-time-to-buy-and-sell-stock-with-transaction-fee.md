# 11.19 Best Time To Buy And Sell Stock With Transaction Fee

> **Source**: [`src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStockWithTransactionFee.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStockWithTransactionFee.kt)
> **Pattern**: hold/cash state machine · **Core page**

## The Problem

Unlimited transactions, each costing `fee`. Max profit.

- Constraints: n ≤ 5×10⁴; fee ≥ 0.

## Examples

```
Input:  prices = [1,3,2,8,4,9], fee = 2   -> Output: 8   (buy 1 sell 8 (7), buy 4 sell 9 (5-2... = 5): total 8)
```

## Intuition — two states: holding a stock or holding cash

The cleanest formulation: `cash` = best profit *not* holding a stock; `hold` = best profit holding one. Each day, decide to sell (cash = max(cash, hold + price - fee)) or buy (hold = max(hold, cash - price)):

```kotlin
var hold = -prices[0]
var cash = 0

for (i in 1..prices.lastIndex) {
    cash = maxOf(cash, hold + prices[i] - fee)   // sell
    hold = maxOf(hold, cash - prices[i])         // buy
}
return cash
```

**Why the two states?** The buy/sell series is a finite automaton: cash ↔ hold via transactions. Each transition's value is the max over days — the [2.0](../ch02-dynamic-programming/pattern-primer.md) state-machine DP, O(1) space.

**Why `cash - price` for buying?** The cash *before* the buy (not the just-updated one) — the buy uses the best non-holding profit from the *previous* day... actually in the loop the order matters: `cash` is updated first, then `hold` uses the *new* cash — which would allow same-day buy-sell. For fee ≥ 0 same-day round trips are never profitable (the fee kills them), so the order is safe — a subtle but correct shortcut.

## Approach 1 — Greedy with fee-aware deltas

Sum positive `price[i+1] - price[i] - fee` segments: correct only when trades don't need splitting — the state machine is the robust answer.

## Approach 2 — Hold/cash machine (the repo's version, optimal)

```kotlin
class BestTimeToBuyAndSellStockWithTransactionFee {
    /**
     * @param prices daily prices
     * @param fee    per-transaction fee
     * @return       max profit
     */
    fun maxProfit(prices: IntArray, fee: Int): Int {
        if (prices.isEmpty()) return 0

        var hold = -prices[0]
        var cash = 0

        for (i in 1..prices.lastIndex) {
            cash = maxOf(cash, hold + prices[i] - fee)
            hold = maxOf(hold, cash - prices[i])
        }
        return cash
    }
}
```

```java
public class BestTimeToBuyAndSellStockWithTransactionFee {
    /**
     * @param prices daily prices
     * @param fee    per-transaction fee
     * @return       max profit
     */
    public int maxProfit(int[] prices, int fee) {
        int hold = -prices[0], cash = 0;

        for (int i = 1; i < prices.length; i++) {
            cash = Math.max(cash, hold + prices[i] - fee);   // sell
            hold = Math.max(hold, cash - prices[i]);         // buy
        }
        return cash;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class BestTimeToBuyAndSellStockWithTransactionFee {
public:
    /**
     * @param prices daily prices
     * @param fee    per-transaction fee
     * @return       max profit
     */
    int maxProfit(std::vector<int>& prices, int fee) {
        int hold = -prices[0], cash = 0;

        for (int i = 1; i < (int)prices.size(); i++) {
            cash = std::max(cash, hold + prices[i] - fee);   // sell
            hold = std::max(hold, cash - prices[i]);         // buy
        }
        return cash;
    }
};
```

```python
def max_profit(prices: list[int], fee: int) -> int:
    """
    @param prices: daily prices
    @param fee:    per-transaction fee
    @return:       max profit
    """
    hold, cash = -prices[0], 0

    for price in prices[1:]:
        cash = max(cash, hold + price - fee)   # sell
        hold = max(hold, cash - price)         # buy

    return cash
```

```rust
impl Solution {
    /// @param prices daily prices
    /// @param fee    per-transaction fee
    /// @return       max profit
    pub fn max_profit(prices: Vec<i32>, fee: i32) -> i32 {
        let mut hold = -prices[0];
        let mut cash = 0;

        for &price in prices.iter().skip(1) {
            cash = cash.max(hold + price - fee);   // sell
            hold = hold.max(cash - price);         // buy
        }
        cash
    }
}
```

## Dry run

**Input:** `prices = [1,3,2,8,4,9], fee = 2`.

```
hold=-1, cash=0
3: cash = max(0, -1+3-2=0) = 0.  hold = max(-1, 0-3=-3) = -1.
2: cash = max(0, -1+2-2=-1) = 0.  hold = max(-1, 0-2) = -1.
8: cash = max(0, -1+8-2=5) = 5.  hold = max(-1, 5-8=-3) = -1.
4: cash = max(5, -1+4-2=1) = 5.  hold = max(-1, 5-4=1) = 1.   (buy at 4!)
9: cash = max(5, 1+9-2=8) = 8.  hold = max(1, 8-9=-1) = 1.

Output: 8 ✓  (buy 1 sell 8, buy 4 sell 9)
```

The state machine's richness: at price 4, `hold` updates to 1 (buying with the cash from the first trade) — the second trade's setup happens inside the same loop. The `cash`/`hold` pair carries both the completed profit and the in-progress position.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Two scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Best Time II** ([11.8](best-time-to-buy-and-sell-stock-ii.md)) — the fee = 0 special case (the up-delta sum).
- **With Cooldown** ([11.18](best-time-to-buy-and-sell-stock-with-cooldown.md)) — the recursive sibling.
- **Interview follow-up:** "Why is updating cash before hold safe?" The buy uses the *new* cash — a same-day buy-sell. With fee ≥ 0 that round trip never improves (it nets −fee), so the order is harmless; with negative fees it would break, but the problem forbids those.
