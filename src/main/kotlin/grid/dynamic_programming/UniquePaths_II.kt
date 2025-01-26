package grid.dynamic_programming

import oracle.net.aso.m

class UniquePaths_II {
    fun uniquePathsWithObstacles(obstacleGrid: Array<IntArray>): Int {
        val (R, C) = obstacleGrid.size to obstacleGrid[0].size
        val dp = Array(R) { IntArray(C) }.apply { this[0][0] = 1 }

        // Obstacle in the first cell itself hence nothing is feasible
        if (obstacleGrid[0][0] == 1) return 0

        // Fill the first row
        for (col in 1 until C) {
            dp[0][col] = if (obstacleGrid[0][col] == 1) 0 else dp[0][col - 1]
        }

        // Fill the first column
        for (row in 1 until R) {
            dp[row][0] = if (obstacleGrid[row][0] == 1) 0 else dp[row - 1][0]
        }

        // Fill the rest of the DP table
        for (row in 1 until R) {
            for (col in 1 until C) {
                when(obstacleGrid[row][col]) {
                    1 -> dp[row][col] = 0
                    else -> dp[row][col] = dp[row - 1][col] + dp[row][col - 1]
                }
            }
        }
        // Return the value in the bottom-right corner
        return dp[R - 1][C - 1]
    }
}
