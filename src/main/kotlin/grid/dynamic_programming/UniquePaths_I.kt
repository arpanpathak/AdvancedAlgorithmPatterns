package grid.dynamic_programming

class UniquePaths_I {
    fun uniquePaths(m: Int, n: Int): Int {
        val dp = Array(m) { IntArray(n){1} }

        // Fill the first row and first column with 1's using `forEach`
        dp[0].forEachIndexed { index, _ -> dp[0][index] = 1 }  // First row is filled with 1's
        dp.forEach { it[0] = 1 }  // First column is filled with 1's

        // Fill the rest of the DP table
        for (row in 1 until m) {
            for (col in 1 until n) {
                dp[row][col] = dp[row - 1][col] + dp[row][col - 1]
            }
        }

        // Return the value in the bottom-right corner
        return dp[m - 1][n - 1]
    }
}
