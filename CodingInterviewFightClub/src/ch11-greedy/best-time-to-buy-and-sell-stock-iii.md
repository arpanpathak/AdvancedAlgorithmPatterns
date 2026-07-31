# 11.17 Best Time To Buy And Sell Stock III

> **Source**: [`src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStock_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stock_market/dp/BestTimeToBuyAndSellStock_III.kt) (a stub in the repo — the canonical solution below)
> **Pattern**: two-pass partition or 4-state machine · **Core page**

## The Problem

**At most two** transactions (buy-sell pairs, non-overlapping). Max profit.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  prices = [3,3,5,0,0,3,1,4]   -> Output: 6   (3→5 and 0→3→4... = 2+4)
Input:  prices = [1,2,3,4,5]         -> Output: 4   (one transaction suffices)
```

## Intuition — split at the second buy: left profit + right profit

With two transactions, there's a **cut day** where the first ends and the second begins. Compute for each day: `left[i]` = max profit in `prices[0..i]` ([11.16](best-time-to-buy-and-sell-stock.md) forward) and `right[i]` = max profit in `prices[i..n-1]` (backward). The answer is `max(left[i] + right[i])`:

```kotlin
val left = IntArray(n)        // best single trade in [0..i]
var minPrice = prices[0]
for (i in 1 until n) {
    minPrice = minOf(minPrice, prices[i])
    left[i] = maxOf(left[i - 1], prices[i] - minPrice)
}

val right = IntArray(n)       // best single trade in [i..n-1]
var maxPrice = prices[n - 1]
for (i in n - 2 downTo 0) {
    maxPrice = maxOf(maxPrice, prices[i])
    right[i] = maxOf(right[i + 1], maxPrice - prices[i])
}

return (0 until n).maxOf { left[it] + right[it] }
```

**Why the cut works?** The two transactions are non-overlapping — some day `i` is the boundary. The left/right arrays make every boundary's total O(1); the max over boundaries is the optimum. The [11.16](best-time-to-buy-and-sell-stock.md) engine, run twice.

**Why "at most" two?** A one-trade optimum appears as a boundary where one side is 0 (e.g. cutting at the best sell day). The `maxOf` over cuts includes it.

## Approach 1 — Partition with left/right arrays (the canonical, optimal)

```kotlin
class BestTimeToBuyAndSellStock_III {
    /**
     * @param prices daily prices
     * @return      max profit with at most two transactions
     */
    fun maxProfit(prices: IntArray): Int {
        val n = prices.size
        if (n < 2) return 0

        val left = IntArray(n)
        var minPrice = prices[0]
        for (i in 1 until n) {
            minPrice = minOf(minPrice, prices[i])
            left[i] = maxOf(left[i - 1], prices[i] - minPrice)
        }

        val right = IntArray(n)
        var maxPrice = prices[n - 1]
        for (i in n - 2 downTo 0) {
            maxPrice = maxOf(maxPrice, prices[i])
            right[i] = maxOf(right[i + 1], maxPrice - prices[i])
        }

        var best = 0
        for (i in 0 until n) best = maxOf(best, left[i] + right[i])
        return best
    }
}
```

```java
public class BestTimeToBuyAndSellStockIII {
    /**
     * @param prices daily prices
     * @return      max profit with at most two transactions
     */
    public int maxProfit(int[] prices) {
        int n = prices.length;
        if (n < 2) return 0;

        int[] left = new int[n];
        int min = prices[0];
        for (int i = 1; i < n; i++) {
            min = Math.min(min, prices[i]);
            left[i] = Math.max(left[i - 1], prices[i] - min);
        }

        int[] right = new int[n];
        int max = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            max = Math.max(max, prices[i]);
            right[i] = Math.max(right[i + 1], max - prices[i]);
        }

        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, left[i] + right[i]);
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class BestTimeToBuyAndSellStockIII {
public:
    /**
     * @param prices daily prices
     * @return      max profit with at most two transactions
     */
    int maxProfit(std::vector<int>& prices) {
        int n = prices.size();
        if (n < 2) return 0;

        std::vector<int> left(n), right(n);
        int min = prices[0];
        for (int i = 1; i < n; i++) {
            min = std::min(min, prices[i]);
            left[i] = std::max(left[i - 1], prices[i] - min);
        }

        int max = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            max = std::max(max, prices[i]);
            right[i] = std::max(right[i + 1], max - prices[i]);
        }

        int best = 0;
        for (int i = 0; i < n; i++) best = std::max(best, left[i] + right[i]);
        return best;
    }
};
```

```python
def max_profit(prices: list[int]) -> int:
    """
    @param prices: daily prices
    @return:       max profit with at most two transactions
    """
    n = len(prices)
    if n < 2:
        return 0

    left = [0] * n
    min_price = prices[0]
    for i in range(1, n):
        min_price = min(min_price, prices[i])
        left[i] = max(left[i - 1], prices[i] - min_price)

    right = [0] * n
    max_price = prices[-1]
    for i in range(n - 2, -1, -1):
        max_price = max(max_price, prices[i])
        right[i] = max(right[i + 1], max_price - prices[i])

    return max(left[i] + right[i] for i in range(n))
```

```rust
impl Solution {
    /// @param prices daily prices
    /// @return      max profit with at most two transactions
    pub fn max_profit(prices: Vec<i32>) -> i32 {
        let n = prices.len();
        if n < 2 { return 0; }

        let mut left = vec![0; n];
        let mut min_price = prices[0];
        for i in 1..n {
            min_price = min_price.min(prices[i]);
            left[i] = left[i - 1].max(prices[i] - min_price);
        }

        let mut right = vec![0; n];
        let mut max_price = prices[n - 1];
        for i in (0..n - 1).rev() {
            max_price = max_price.max(prices[i]);
            right[i] = right[i + 1].max(max_price - prices[i]);
        }

        (0..n).map(|i| left[i] + right[i]).max().unwrap()
    }
}
```

## Dry run

**Input:** `prices = [3,3,5,0,0,3,1,4]`.

```
left:  [0,0,2,2,2,3,3,4]   (best single trade in prefixes)
right: [4,4,4,4,4,3,3,0]   (best single trade in suffixes)
sums:  4,4,6,6,6,6,6,4  -> max 6 ✓

(2 transactions: 3→5 (profit 2) on days 0-2, 0→4 (profit 4) on days 3-7 — the cut at day 2)
```

## Complexity

**Time.** Three passes:

$$
T(n) = O(n)
$$

**Space.** Two arrays (or the 4-state machine's O(1)):

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Best Time I** ([11.16](best-time-to-buy-and-sell-stock.md)) — one transaction (a single left[] pass).
- **Best Time With Cooldown** ([11.18](best-time-to-buy-and-sell-stock-with-cooldown.md)) — the state-machine upgrade.
- **Interview follow-up:** "What's the O(1)-space alternative?" The 4-state machine: `buy1, sell1, buy2, sell2` updated per day — `buy2 = max(buy2, sell1 - price); sell2 = max(sell2, buy2 + price)`. Same result, no arrays; the partition version is the *explanation*, the machine is the *optimization*.
