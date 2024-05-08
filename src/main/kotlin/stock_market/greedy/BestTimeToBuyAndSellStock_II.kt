package stock_market.greedy

class BestTimeToBuyAndSellStock_II {
    fun maxProfit(prices: IntArray): Int {
        var maxProfit = 0
        for (i in 1..prices.lastIndex) {
            if (prices[i] > prices[i-1]) {
                maxProfit += prices[i] - prices[i - 1]
            }
        }

        return  maxProfit
    }
}