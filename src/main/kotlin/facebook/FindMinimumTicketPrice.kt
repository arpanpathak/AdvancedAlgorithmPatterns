package facebook

class FindMinimumTicketPrice {
    fun findMinimumTicketCost(departure: IntArray, returnPrices: IntArray): Int {
        val n = returnPrices.size
        var minReturnPrice = Int.MAX_VALUE
        var minCost = Int.MAX_VALUE

        // Traverse both arrays backward to calculate the minimum cost
        for (i in n - 1 downTo 0) {
            // Update the running minimum of return prices
            minReturnPrice = minOf(minReturnPrice, returnPrices[i])
            // Calculate the cost for the current departure day
            minCost = minOf(minCost, departure[i] + minReturnPrice)
        }

        return minCost
    }
}
