package dynamic_programming

fun unboundedKnapsack(items: List<Item>, capacity: Int): Int {
    val dp = IntArray(capacity + 1)

    items.forEach { (w, v) ->
        // INTUITION: We iterate FORWARDS (w to capacity).
        // When we calculate dp[j], we look back at dp[j - w].
        // Because we moved forwards, dp[j - w] was already updated
        // by the CURRENT item in this same loop.
        // This allows an item to "stack" on itself infinitely.
        for (j in w..capacity) {
            dp[j] = maxOf(dp[j], dp[j - w] + v)
        }
    }

    return dp[capacity]
}
