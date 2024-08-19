package array.hashtable

import javax.swing.UIManager.put

class ValidSudoku {
    fun isValidSudoku(board: Array<CharArray>): Boolean {
        // Initialize sets for rows, columns, and subgrids
        val rows = Array(9) { mutableSetOf<Char>() }
        val cols = Array(9) { mutableSetOf<Char>() }
        val subgrids = Array(3) { Array(3) { mutableSetOf<Char>() } }

        for (i in board.indices) {
            for (j in board[i].indices) {
                val num = board[i][j]
                if (num != '.') {
                    // Check row
                    if (!rows[i].add(num)) return false

                    // Check column
                    if (!cols[j].add(num)) return false

                    // Check subgrid without calculating box index directly
                    if (!subgrids[i / 3][j / 3].add(num)) return false
                }
            }
        }

        return true
    }
}