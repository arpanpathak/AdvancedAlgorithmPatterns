package array.dp

class LongestIncreasingSequenceInAMatrix {
    private val dirs = arrayOf(0 to 1, 1 to 0, -1 to 0, 0 to -1)

    fun longestIncreasingPath(matrix: Array<IntArray>): Int {
        val (m, n) = matrix.size to matrix[0].size
        val cache = Array(m) { IntArray(n) }

        fun dfs(i: Int, j: Int): Int {
            if (cache[i][j] != 0) return cache[i][j]

            cache[i][j] = 1
            for (delta in dirs) {
                val (x, y) = i + delta.first to j + delta.second

                if (x in matrix.indices && y in matrix[0].indices && matrix[x][y] > matrix[i][j]) {
                    cache[i][j] = maxOf(cache[i][j], 1 + dfs( x, y))
                }
            }

            return cache[i][j]
        }

        var max = 0
        for (i in 0 until m) {
            for (j in 0 until n) {
                max = maxOf(max, dfs(i, j))
            }
        }

        return max
    }
}