package dynamic_programming

class SuperEggDropping {
    fun superEggDrop(k: Int, n: Int): Int {
        val dp = Array(k + 1) { IntArray(n + 1) }
        var m = 0
        while (dp[k][m] < n) {
            m++
            for (eggs in 1..k) {
                dp[eggs][m] = dp[eggs][m - 1] + dp[eggs - 1][m - 1] + 1
            }
        }
        return m
    }
}