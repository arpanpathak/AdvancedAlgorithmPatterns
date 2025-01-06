package stock_market.dp

class BestTimeToBuyAndSellStockWithTransactionFee {

    fun maxProfit(prices: IntArray, fee: Int): Int {
        if (prices.isEmpty()) return 0

        var hold = -prices[0]
        var cash = 0

        for (i in 1..prices.lastIndex) {
            cash = maxOf( cash, hold + prices[i] - fee) // Sell the stock
            hold = maxOf(hold, cash - prices[i]) // buy the stock
        }

        return cash
    }
}
