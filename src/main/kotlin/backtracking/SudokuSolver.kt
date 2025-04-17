package backtracking

class SudokuSolver {
    fun solveSudoku(board: Array<CharArray>) {
        solve(board)
    }

    private fun solve(board: Array<CharArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == '.') {
                    for (ch in '1'..'9') {
                        if (isValid(board, row, col, ch)) {
                            board[row][col] = ch
                            if (solve(board)) return true
                            board[row][col] = '.' // backtrack
                        }
                    }
                    return false // no valid number found
                }
            }
        }
        return true // board solved
    }

    private fun isValid(board: Array<CharArray>, row: Int, col: Int, ch: Char): Boolean {
        for (i in 0..8) {
            if (board[row][i] == ch || board[i][col] == ch) return false
            val boxRow = 3 * (row / 3) + i / 3
            val boxCol = 3 * (col / 3) + i % 3
            if (board[boxRow][boxCol] == ch) return false
        }
        return true
    }
}