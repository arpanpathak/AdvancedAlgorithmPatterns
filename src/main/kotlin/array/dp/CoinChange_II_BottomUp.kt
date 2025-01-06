package array.dp

class CoinChange_II_BottomUp {
    fun change(amount: Int, coins: IntArray): Int {
        val dp = IntArray(amount + 1) { 0 }
        dp[0] = 1  // Base case: 1 way to make amount 0 (using no coins)

        coins.forEach { coin ->
            for (j in coin..amount)
                dp[j] += dp[j - coin]
        }

        return dp[amount]
    }
}
