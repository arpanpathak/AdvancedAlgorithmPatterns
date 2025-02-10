package math

class TicTacToe(n: Int) {
    private val rows = IntArray(n)
    private val cols = IntArray(n)
    private var diagonal = 0
    private var antiDiagonal = 0

    // Method to make a move and check for a winner
    fun move(row: Int, col: Int, player: Int): Int {
        val mark = if (player == 1) 1 else -1

        // Update the row, column, and diagonals
        rows[row] += mark
        cols[col] += mark

        if (row == col) {
            diagonal += mark
        }
        if (row + col == rows.size - 1) {
            antiDiagonal += mark
        }

        // Check if the current move results in a win
        if (Math.abs(rows[row]) == rows.size ||
            Math.abs(cols[col]) == cols.size ||
            Math.abs(diagonal) == rows.size ||
            Math.abs(antiDiagonal) == rows.size
        ) {
            return player
        }

        return 0 // No winner yet
    }
}

/**
 * Your TicTacToe object will be instantiated and called as such:
 * var obj = TicTacToe(n)
 * var param_1 = obj.move(row,col,player)
 */
