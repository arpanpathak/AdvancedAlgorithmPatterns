package grid.dynamic_programming

class CherryPickup {

    private val dp = mutableMapOf<Triple<Int, Int, Int>, Int>()
    private lateinit var grid: Array<IntArray>
    private var n: Int = 0

    fun cherryPickup(grid: Array<IntArray>): Int {
        this.grid = grid
        n = grid.size
        dp.clear()
        return maxOf(0, dfs(0, 0, 0))
    }

    private fun dfs(row: Int, col1: Int, col2: Int): Int {
        val row2 = row + col1 - col2
        val state = Triple(row, col1, col2)

        return when {
            dp.contains(state) -> dp[state] !!
            isInvalidPosition(row, row2, col1, col2) -> Int.MIN_VALUE
            isFinalPosition(row, col1) -> grid[row][col1]
            else -> {
                val cherryCount = when {
                    (col1 != col2) -> grid[row][col1] + grid[row2][col2]
                    else ->  grid[row][col1]
                }
                val maxCherries = maxOf(
                    dfs(row, col1 + 1, col2 + 1),
                    dfs(row, col1 + 1, col2),
                    dfs(row + 1, col1, col2),
                    dfs(row + 1, col1, col2 + 1)
                )
                (cherryCount + maxCherries)
            }
        }.also { dp[state] = it }
    }

//    private fun isInvalidPosition(row: Int, row2: Int, col1: Int, col2: Int) =
//        row >= n || row2 >= n || col1 >= n || col2 >= n || grid[row][col1] == -1 || grid[row2][col2] == -1

    private fun isInvalidPosition(row: Int, row2: Int, col1: Int, col2: Int) =
        listOf(row, row2, col1, col2).any { it >= n } || grid[row][col1] == -1 || grid[row2][col2] == -1

    private fun isFinalPosition(row: Int, col1: Int) =
        row == n - 1 && col1 == n - 1

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

        }
    }
}
