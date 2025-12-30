package grid;

public class SurroundedRegionBfs {
    fun solve(board: Array<CharArray>): Unit {
        if (board.isEmpty() || board[0].isEmpty()) return

        val m = board.size
        val n = board[0].size
        val queue = ArrayDeque<Pair<Int, Int>>()

        // Add border 'O's to queue
        for (i in 0 until m) {
            if (board[i][0] == 'O') queue.add(i to 0)
            if (board[i][n - 1] == 'O') queue.add(i to n - 1)
        }

        for (j in 0 until n) {
            if (board[0][j] == 'O') queue.add(0 to j)
            if (board[m - 1][j] == 'O') queue.add(m - 1 to j)
        }

        // BFS to mark border-connected regions
        val dirs = arrayOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeFirst()
            board[x][y] = '#'

            for ((dx, dy) in dirs) {
                val nx = x + dx
                val ny = y + dy
                if (nx in 0 until m && ny in 0 until n && board[nx][ny] == 'O') {
                    queue.add(nx to ny)
                }
            }
        }

        // Final processing
        for (i in 0 until m) {
            for (j in 0 until n) {
                board[i][j] = when (board[i][j]) {
                    'O' -> 'X'
                    '#' -> 'O'
                    else -> board[i][j]
                }
            }
        }
    }
}
