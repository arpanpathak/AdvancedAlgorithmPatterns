package dynamic_programming

data class Item(val weight: Int, val value: Int)

// Return maximum value or profit that one can achieve.,..
fun knapsack(items: List<Item>, capacity: Int): Int {
    val dp = IntArray(capacity + 1) { 0 }

    items.forEach { (w, v) ->
        for (j in capacity downTo w)
            dp[j] = maxOf(dp[j], dp[j - w] + v)
    }

    return dp[capacity]
}

fun knapsack2D(items: List<Item>, capacity: Int): Int {
    val n = items.size
    // dp[i][j] = max value using first 'i' items with capacity 'j'
    val dp = Array(n + 1) { IntArray(capacity + 1) }

    // Using .withIndex() to keep track of item count vs item index
    items.forEachIndexed { idx, (w, v) ->
        val i = idx + 1 // The current row in DP (1-indexed)
        for (j in 0..capacity) {
            if (w <= j) {
                // Choice: Take it or Leave it
                // dp[i-1][j] is the value WITHOUT this item
                // dp[i-1][j-w] + v is the value WITH this item
                dp[i][j] = maxOf(dp[i - 1][j], dp[i - 1][j - w] + v)
            } else {
                // Too heavy: Must carry over the previous best
                dp[i][j] = dp[i - 1][j]
            }
        }
    }

    return dp[n][capacity]
}
