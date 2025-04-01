package array.dp

class MinimumPathSum {
    fun minPathSum(grid: Array<IntArray>?): Int {
        if (grid.isNullOrEmpty()) return 0

        val dp = Array(grid.size) { IntArray(grid[0].size) }

        for (i in grid.indices) {
            for (j in 0 until grid[i].size) {
                dp[i][j] += grid[i][j]
                dp[i][j] += when {
                    i > 0 && j > 0 -> minOf(dp[i - 1][j], dp[i][j - 1])
                    i > 0 -> dp[i - 1][j]
                    j > 0 -> dp[i][j - 1]
                    else -> 0
                }
            }
        }
        return dp[grid.size - 1][grid[0].size - 1]
    }
}