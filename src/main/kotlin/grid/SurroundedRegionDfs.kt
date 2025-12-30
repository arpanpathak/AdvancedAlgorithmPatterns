package grid

class SurroundedRegionDfs {
    fun solve(board: Array<CharArray>): Unit {
        if (board.isEmpty() || board[0].isEmpty()) return

        val m = board.size
        val n = board[0].size

        // Mark 'O's connected to border with temporary marker '#'
        val directions = listOf( 0 to 1, 1 to 0, 0 to -1, -1 to 0)
        fun dfs(i: Int, j: Int) {
            if (i !in 0 until m || j !in 0 until n || board[i][j] != 'O') return

            board[i][j] = '#' // Visited marker

            // Explore all four directions
            directions.forEach { (dx, dy) -> dfs(i + dx, j + dy)}
        }

        // Check borders
        for (i in 0 until m) {
            dfs(i, 0)           // Left border
            dfs(i, n - 1)       // Right border
        }

        for (j in 0 until n) {
            dfs(0, j)           // Top border
            dfs(m - 1, j)       // Bottom border
        }

        // Process entire board
        for (i in 0 until m) {
            for (j in 0 until n) {
                board[i][j] = when (board[i][j]) {
                    'O' -> 'X'                 // Surrounded region
                    '#' -> 'O'  // Restore border-connected region
                    else -> 'X'
                }
            }
        }
    }
}
