package array.dp

class CoinChange_II {
    fun change(amount: Int, coins: IntArray): Int {
        val dp = Array(amount + 1) { IntArray(coins.size) { -1 } }
        return change(dp, amount, coins, 0)
    }

    private fun change(dp: Array<IntArray>, amount: Int, coins: IntArray, i: Int): Int {
        return when {
            amount < 0 || (i == coins.size && amount > 0) -> 0
            amount == 0 -> 1
            dp[amount][i] != -1 -> dp[amount][i]
            else -> {
                dp[amount][i] = change(dp, amount - coins[i], coins, i) + change(dp, amount, coins, i + 1)
                dp[amount][i]
            }
        }
    }
}