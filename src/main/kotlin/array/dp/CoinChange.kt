package array.dp

class CoinChange {
    fun coinChange(coins: IntArray, amount: Int): Int {
        val dp = IntArray(amount + 1) { -1 }
        return coinChange(coins, amount, dp).let { if (it != Int.MAX_VALUE) it else -1 }
    }

    private fun coinChange(coins: IntArray, amount: Int, dp: IntArray): Int {
        return when {
            amount == 0 -> 0
            dp[amount] != -1 -> dp[amount]
            else -> {
                var minCoins = Int.MAX_VALUE
                for (coin in coins) {
                    if (coin <= amount) {
                        val result = coinChange(coins, amount - coin, dp)
                        if (result != Int.MAX_VALUE) {
                            minCoins = minOf(minCoins, 1 + result)
                        }
                    }
                }
                minCoins
            }
        }.also { dp[amount] = it }
    }

    // Cleaner Code
    fun coinChangeCleanAf(coins: IntArray, amount: Int): Int {
        val dp = IntArray(amount + 1) { if (it == 0) 0 else amount + 1 }

        for (i in 1..amount) {
            for (coin in coins) {
                if (i >= coin) {
                    dp[i] = minOf(dp[i], dp[i - coin] + 1)
                }
            }
        }

        return dp[amount].takeIf { it <= amount } ?: -1
    }
}
