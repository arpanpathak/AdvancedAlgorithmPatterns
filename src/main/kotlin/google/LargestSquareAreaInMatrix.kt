package google

fun minimalSquareDp(matrix: Array<IntArray>): Int {
    if (matrix.isEmpty() || matrix[0].isEmpty()) return 0

    val (rows, cols) = matrix.size to matrix[0].size

    val dp = Array(rows) { IntArray(cols) }
    var maxSide = 0

    for (i in 0 until rows) {
        for (j in 0 until cols) {
            when {
                matrix[i][j] == 0 -> continue
                i == 0 || j == 0 -> dp[i][j] = 1
                else -> dp[i][j] = 1 + minOf(dp[i-1][j], dp[i][j -1], dp[i-1][j-1])
            }

            maxSide = maxOf(dp[i][j], maxSide)
        }
    }

    return maxSide
}