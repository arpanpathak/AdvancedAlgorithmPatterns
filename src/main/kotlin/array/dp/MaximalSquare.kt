package array.dp

class MaximalSquare {
    fun maximalSquare(matrix: Array<CharArray>): Int {
        if (matrix.isEmpty() || matrix[0].isEmpty()) return 0
        val m = matrix.size
        val n = matrix[0].size
        val dp = Array(m) { IntArray(n) }
        var maxSize = 0

        for (i in 0 until m) {
            for (j in 0 until n) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1
                    } else {
                        dp[i][j] = minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
                    }
                    maxSize = maxOf(maxSize, dp[i][j])
                }
            }
        }

        return maxSize * maxSize
    }

    class MaximalSquareRecursive {
        fun maximalSquare(matrix: Array<CharArray>): Int {
            if (matrix.isEmpty()) return 0

            val m = matrix.size
            val n = matrix[0].size
            val memo = Array(m) { IntArray(n) { -1 } }
            var maxSide = 0

            fun solve(i: Int, j: Int): Int {
                if (i >= m || j >= n || matrix[i][j] == '0') return 0
                if (memo[i][j] != -1) return memo[i][j]

                val right = solve(i, j + 1)
                val down = solve(i + 1, j)
                val diag = solve(i + 1, j + 1)

                memo[i][j] = 1 + minOf(right, down, diag)
                maxSide = maxOf(maxSide, memo[i][j])
                return memo[i][j]
            }

            solve(0, 0)
            return maxSide * maxSide
        }
    }
}