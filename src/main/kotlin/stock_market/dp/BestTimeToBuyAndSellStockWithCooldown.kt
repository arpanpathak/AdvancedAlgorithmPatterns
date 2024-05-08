package stock_market.dp

// https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/discuss/75924/Most-consistent-ways-of-dealing-with-the-series-of-stock-problems
class BestTimeToBuyAndSellStockWithCooldown {
    val dp = mutableMapOf<Int, Int>()

    fun maxProfit(prices: IntArray): Int {
        return maxProfit(prices, 0)
    }

    fun maxProfit(prices: IntArray, buyAt: Int): Int {
        return when {
            buyAt > prices.lastIndex ->  0
            dp.containsKey(buyAt) ->  dp[buyAt]!!
            else -> {
                var maxProfit = 0
                for (sellAt in buyAt + 1..prices.lastIndex) {
                    maxProfit = maxOf(
                        maxProfit,
                        // HOLD this stock and sell later
                        maxProfit(prices,  sellAt),
                        prices[sellAt] - prices[buyAt] + maxProfit(prices, sellAt + 2)
                    )
                }
                maxProfit
            }
        }.also {
            dp[buyAt] = it
        }
    }

    /**
     * Input: prices = [1,2,3,0,2]
     * Profit = 0
     * Day Timeline :-
     * 0    1   2   3   4   5   6
     * BUY
     */
}
