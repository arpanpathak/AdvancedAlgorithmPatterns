package graph.dp

class CheapestFlightsWithinKStopsBellman {
    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        var dp = IntArray(n) { Int.MAX_VALUE }
        dp[src] = 0

        // Iterate k + 1 times (for 0 stops up to k stops)
        for (stops in 0..k) {
            val nextDp = dp.clone()

            for ((u, v, price) in flights) {
                if (dp[u] == Int.MAX_VALUE ) continue

                val newPrice = dp[u] + price
                if (newPrice < nextDp[v]) {
                    nextDp[v] = newPrice
                }
            }
            dp = nextDp
        }

        return if (dp[dst] == Int.MAX_VALUE) -1 else dp[dst]
    }
}