package array.dp

fun coinChange(coins: IntArray, amount: Int): Int {
    val maxVal = amount + 1
    val dp = IntArray(amount + 1) { maxVal }
    dp[0] = 0

    (1..amount).forEach { i->
        dp[i] = coins
            .filter { it <= i }
            .minOfOrNull { coin -> dp[i - coin] + 1 } ?: maxVal
    }

    return dp[amount].takeIf { it < maxVal } ?: -1
}

// For Loop based
fun coinChangeForLoop(coins: IntArray, amount: Int): Int {
    val max = amount + 1
    val dp = IntArray(amount + 1) { max }
    dp[0] = 0

    for (i in 1..amount) {
        for (coin in coins) {
            if (coin <= i) {
                dp[i] = minOf(dp[i], dp[i - coin] + 1)
            }
        }
    }

    return if (dp[amount] > amount) -1 else dp[amount]
}