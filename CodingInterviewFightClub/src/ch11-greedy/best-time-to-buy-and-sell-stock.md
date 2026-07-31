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
