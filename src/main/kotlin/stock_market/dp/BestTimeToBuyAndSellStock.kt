package stock_market.dp

class BestTimeToBuyAndSellStock {
    // The constraint is we can buy a stock in single day and sell in different day. There is no buyback
    // Stock must be bought once and sold once
    fun maxProfit(prices: IntArray): Int {
        if (prices.isEmpty())
            return 0
        var ( maxProfit, minElement ) = (0 to prices[0])

        for (i in 1..prices.lastIndex) {
            maxProfit = maxOf(maxProfit, prices[i] - minElement)
            minElement = minOf(minElement, prices[i])
        }

        return maxProfit
    }
}