package simulation

class FindWinnerOnATicTacToeGame {
    private val board = Array(3) { CharArray(3) } // 3x3 Tic-Tac-Toe board
    private val players = charArrayOf('A', 'B') // Player A and Player B

    fun tictactoe(moves: Array<IntArray>): String {
        for ((i, move) in moves.withIndex()) {
            val player = players[i % 2] // Alternate between Player A and B
            val (row, col) = move
            board[row][col] = player

            // Check if the current move resulted in a win
            if (checkRow(row, player) ||
                checkColumn(col, player) ||
                checkDiagonal(player) ||
                checkAntiDiagonal(player)
            ) {
                return player.toString()
            }
        }

        // If all moves are completed and no winner, return "Draw" or "Pending"
        return if (moves.size == 9) "Draw" else "Pending"
    }

    // Check if the entire row is filled by the player
    private fun checkRow(row: Int, player: Char): Boolean {
        for (col in 0 until 3) {
            if (board[row][col] != player) return false
        }
        return true
    }

    // Check if the entire column is filled by the player
    private fun checkColumn(col: Int, player: Char): Boolean {
        for (row in 0 until 3) {
            if (board[row][col] != player) return false
        }
        return true
    }

    // Check if the main diagonal is filled by the player
    private fun checkDiagonal(player: Char): Boolean {
        for (i in 0 until 3) {
            if (board[i][i] != player) return false
        }
        return true
    }

    // Check if the anti-diagonal is filled by the player
    private fun checkAntiDiagonal(player: Char): Boolean {
        for (i in 0 until 3) {
            if (board[i][2 - i] != player) return false
        }
        return true
    }
}
