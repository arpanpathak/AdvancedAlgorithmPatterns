# 11.8 Best Time To Buy And Sell Stock II

> **Source:** [`src/main/kotlin/stock_market/greedy/BestTimeToBuyAndSellStock_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stock_market/greedy/BestTimeToBuyAndSellStock_II.kt)
> **Pattern:** greedy on price differences · **Core page**

## The Problem

Given `prices[i]` (day i's price), return the **maximum profit** from any number of buy/sell transactions — you may buy then sell, sell then buy again, but not hold overlapping positions.

- Constraints: $1 \le n \le 3 \times 10^4$; prices fit in `Int`.

## Examples

```
Input:  prices = [7,1,5,3,6,4]   -> Output: 7   (buy 1 sell 5, buy 3 sell 6)
Input:  prices = [1,2,3,4,5]     -> Output: 4   (buy 1 sell 5 — same as each +1 step)
Input:  prices = [7,6,4,3,1]     -> Output: 0   (never profitable)
```

## Intuition — every *up-step* is profit, every *down-step* is skipped

With unlimited transactions, the optimal strategy is stunningly simple: **buy at every local minimum, sell at every local maximum.** And that decomposes into per-day increments:

$$
\text{profit} = \sum_{i} \max(0,\; prices[i] - prices[i-1])
$$

**Why does summing up-steps equal "buy low, sell high"?** `[1,2,3]`: buying at 1 and selling at 3 = 2; summing the steps `(2-1) + (3-2)` = 2 — identical. A multi-day climb is exactly the sum of its daily up-moves, so the greedy never needs to know where the peak is; it just banks every positive day-over-day difference. `[7,1,5,3,6]`: steps `-6, +4, -2, +3` → profit = 4 + 3 = 7 ✓.

**Why is this optimal (not just plausible)?** Exchange argument: any transaction `buy at a, sell at b` splits into the sum of day steps between a and b. Maximizing profit = maximizing the sum of included steps; since steps can be taken independently (each buy-sell is one step), taking *all* positive steps and none negative is globally optimal — no transaction can do better than collecting every positive step, and the greedy collects exactly them.

**The zero-profit fallback:** a strictly decreasing array has no positive steps → 0. Holding is always an option.

## Approach 1 — Peak-valley tracking (also O(n))

Walk to each valley, then each peak, add the difference. Equivalent; the one-liner below is the compressed form.

## Approach 2 — Sum positive day-differences (the repo's version, optimal)

```kotlin
class BestTimeToBuyAndSellStock_II {
    /**
     * @param prices daily prices
     * @return      maximum profit with unlimited transactions
     */
    fun maxProfit(prices: IntArray): Int {
        var maxProfit = 0
        for (i in 1..prices.lastIndex) {
            if (prices[i] > prices[i - 1]) {
                maxProfit += prices[i] - prices[i - 1]   // bank every up-step
            }
        }
        return maxProfit
    }
}
```

```java
public class BestTimeToBuyAndSellStockII {
    /**
     * @param prices daily prices
     * @return      maximum profit with unlimited transactions
     */
    public int maxProfit(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];     // bank every up-step
            }
        }
        return profit;
    }
}
```

```cpp
#include <vector>

class BestTimeToBuyAndSellStockII {
public:
    /**
     * @param prices daily prices
     * @return      maximum profit with unlimited transactions
     */
    int maxProfit(std::vector<int>& prices) {
        int profit = 0;
        for (int i = 1; i < (int)prices.size(); i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];     // bank every up-step
            }
        }
        return profit;
    }
};
```

```python
def max_profit(prices: list[int]) -> int:
    """
    @param prices: daily prices
    @return:       maximum profit with unlimited transactions
    """
    return sum(max(0, prices[i] - prices[i - 1]) for i in range(1, len(prices)))
```

```rust
impl Solution {
    /// @param prices daily prices
    /// @return      maximum profit with unlimited transactions
    pub fn max_profit(prices: Vec<i32>) -> i32 {
        prices.windows(2)
            .map(|w| (w[1] - w[0]).max(0))   // bank every up-step
            .sum()
    }
}
```

## Dry run

**Input:** `prices = [7,1,5,3,6,4]`.

```
i=1: 1 - 7 = -6 < 0 -> skip.
i=2: 5 - 1 = +4  -> profit = 4.
i=3: 3 - 5 = -2  -> skip.
i=4: 6 - 3 = +3  -> profit = 7.
i=5: 4 - 6 = -2  -> skip.

Output: 7 ✓   (buy at 1, sell at 5; buy at 3, sell at 6)
```

The up-steps `+4` and `+3` reconstruct the two transactions exactly; the down-steps are skipped as "don't buy before a fall". `[1,2,3,4,5]` gives `+1+1+1+1 = 4` — the single climb decomposes into its daily increments. `[7,6,4,3,1]` gives 0.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** One variable:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Best Time To Buy And Sell Stock I** (`stock_market/dp/`) — one transaction only: track the *min price seen* and the best spread; the "one" version of this greedy.
- **III / With Cooldown / With Transaction Fee** (`stock_market/dp/`) — the DP versions: state machines over (holding, not-holding) replace the greedy once the number of transactions or extra rules arrive.
- **Interview follow-up:** "Why does summing day-differences equal optimal trading?" The telescoping identity: any buy-sell over `[a, b]` equals the *sum of all day steps* in `[a, b]`. Taking exactly the positive steps (and only them) is therefore optimal — each positive step is an independent profitable micro-transaction, and negative steps are never forced into any deal.
