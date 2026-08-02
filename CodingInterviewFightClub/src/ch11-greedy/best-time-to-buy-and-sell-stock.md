# 11.16 Best Time To Buy And Sell Stock

> **Source**: [`src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStock.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStock.kt)
> **Pattern**: running minimum + best delta · **Core page**

## The Problem

One buy, one sell (later day). Max profit, or 0.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  prices = [7,1,5,3,6,4]   -> Output: 5   (buy 1, sell 6)
Input:  prices = [7,6,4,3,1]     -> Output: 0
```

## Intuition — the best sell day's profit is today minus the *cheapest so far*

Walking left to right, track the minimum seen; today's potential profit is `prices[i] - minSoFar`. The answer is the max over all days:

```kotlin
var (maxProfit, minElement) = (0 to prices[0])

for (i in 1..prices.lastIndex) {
    maxProfit = maxOf(maxProfit, prices[i] - minElement)
    minElement = minOf(minElement, prices[i])
}
return maxProfit
```

**Why one pass?** The buy must precede the sell — the running minimum is the best *eligible* buy for every future sell. The [11.8](best-time-to-buy-and-sell-stock-ii.md) greedy's ancestor: one transaction instead of unlimited.

## Approach 1 — Brute force all pairs (O(n²))

Check every (buy, sell): correct, slow.

## Approach 2 — Running minimum (the repo's version, optimal)

```kotlin
class BestTimeToBuyAndSellStock {
    /**
     * @param prices daily prices
     * @return      max profit from one buy-sell
     */
    fun maxProfit(prices: IntArray): Int {
        if (prices.isEmpty()) return 0

        var (maxProfit, minElement) = (0 to prices[0])

        for (i in 1..prices.lastIndex) {
            maxProfit = maxOf(maxProfit, prices[i] - minElement)
            minElement = minOf(minElement, prices[i])
        }
        return maxProfit
    }
}
```

```java
public class BestTimeToBuyAndSellStock {
    /**
     * @param prices daily prices
     * @return      max profit from one buy-sell
     */
    public int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE, profit = 0;

        for (int price : prices) {
            min = Math.min(min, price);
            profit = Math.max(profit, price - min);
        }
        return profit;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <climits>

class BestTimeToBuyAndSellStock {
public:
    /**
     * @param prices daily prices
     * @return      max profit from one buy-sell
     */
    int maxProfit(std::vector<int>& prices) {
        int min = INT_MAX, profit = 0;

        for (int price : prices) {
            min = std::min(min, price);
            profit = std::max(profit, price - min);
        }
        return profit;
    }
};
```

```python
def max_profit(prices: list[int]) -> int:
    """
    @param prices: daily prices
    @return:       max profit from one buy-sell
    """
    min_price = float("inf")
    profit = 0

    for price in prices:
        min_price = min(min_price, price)
        profit = max(profit, price - min_price)

    return profit
```

```rust
impl Solution {
    /// @param prices daily prices
    /// @return      max profit from one buy-sell
    pub fn max_profit(prices: Vec<i32>) -> i32 {
        let mut min_price = i32::MAX;
        let mut profit = 0;

        for price in prices {
            min_price = min_price.min(price);
            profit = profit.max(price - min_price);
        }
        profit
    }
}
```

## Reading the code — what's actually happening

```kotlin
var (maxProfit, minElement) = (0 to prices[0])
for (i in 1..prices.lastIndex) {
    maxProfit = maxOf(maxProfit, prices[i] - minElement)
    minElement = minOf(minElement, prices[i])
}
return maxProfit
```

The constraint that makes this easy is **the buy must come before the sell**. As we walk the days left to right, every price we pass is a potential *sell* day — and the best possible buy for that sell day is simply the cheapest price we've already seen. So two running values are all the memory we need:

- **`minElement` is the best buy so far.** Updated with `minOf(minElement, prices[i])`, it always holds the lowest price among days `0..i`. The order of the two updates matters: we compute the profit *first*, because selling on day `i` can only use buys from days `< i` — including `prices[i]` itself as a buy would create a zero-profit "same-day" transaction that can never beat the max anyway.
- **`maxProfit = maxOf(maxProfit, prices[i] - minElement)` scores today as a sell day.** `prices[i] - minElement` is "if I sell today, buying at the cheapest earlier day, what do I make?" The running max keeps the best of all sell days. If today's price is below the min (a new low), the delta is negative and `maxOf` keeps the old profit — which is also why `maxProfit` starts at 0: the problem allows *not* trading (profit 0) rather than a loss.
- **The single pass is complete because every (buy, sell) pair is covered.** Any optimal pair (buy at `b`, sell at `s`) is considered implicitly: on day `s`, `minElement` is at most `prices[b]` (it's the *minimum* over days `≤ s`), so the computed delta is at least `prices[s] - prices[b]`. The answer is never worse than the true optimum.

Trace `[7,1,5,3,6,4]`: day 1 → min 1; day 2 → profit `5-1 = 4`; day 4 → profit `6-1 = 5` (the max); day 5 → `4-1 = 3` (no improvement). Answer 5 ✓.

## Dry run

**Input:** `prices = [7,1,5,3,6,4]`.

```
min=7, profit=0
7: min 7.  profit max(0, 0) = 0.
1: min 1.  profit 0.
5: min 1.  profit 4.
3: profit 4.  6: profit 5.  4: profit 5.

Output: 5 ✓  (buy at 1, sell at 6)
```

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

- **Best Time II** ([11.8](best-time-to-buy-and-sell-stock-ii.md)) — unlimited trades (sum the up-deltas).
- **Best Time III** ([11.17](best-time-to-buy-and-sell-stock-iii.md)) — two transactions (partition + this page).
- **With Cooldown** ([11.18](best-time-to-buy-and-sell-stock-with-cooldown.md)) / **With Fee** ([11.19](best-time-to-buy-and-sell-stock-with-transaction-fee.md)) — the state-machine family.
- **Interview follow-up:** "Why is the running minimum sufficient?" Any sell day's optimal buy is the minimum of all *previous* days — tracking it captures every day's best in one pass; the max over those is the global optimum.
