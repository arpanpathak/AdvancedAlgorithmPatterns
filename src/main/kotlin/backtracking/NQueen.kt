package backtracking

import javax.swing.text.html.HTML.Attribute.N

class NQueen {
    private lateinit var placed: IntArray

    fun solveNQueens(n: Int): List<List<String>> {
        val results = mutableListOf<List<String>>()
        placed = IntArray(n) { -1 } // Initialize with -1 indicating no queens are placed

        fun dfs(row: Int) {
            if (row == n) {
                // Convert the board configuration to the required output format
                val board = List(n) { CharArray(n) { '.' } }
                for (r in placed.indices) {
                    board[r][placed[r]] = 'Q'
                }
                results.add(board.map { it.joinToString("") })
                return
            }

            for (col in 0 until n) {
                if (isSafe(row, col)) {
                    placed[row] = col
                    dfs(row + 1)
                    placed[row] = -1 // Remove the queen (backtrack)
                }
            }
        }

        dfs(0)
        return results
    }

    private fun isSafe(row: Int, col: Int): Boolean {
        for (prevRow in 0 until row) {
            val prevCol = placed[prevRow]
            if (prevCol == col || Math.abs(prevRow - row) == Math.abs(prevCol - col)) {
                return false
            }
        }
        return true
    }
}