package array.backtracking

/**
 * PROBLEM: N-Queens
 * The goal is to place N queens on an NxN board such that no two queens attack each other.
 * * MATH INTUITION:
 * 1. Columns: Straightforward index [col].
 * 2. Forward Diagonals (Top-Right to Bottom-Left): row + col is constant.
 * Range: 0 to 2N-2.
 * 3. Backward Diagonals (Top-Left to Bottom-Right): row - col is constant.
 * Range: -(N-1) to (N-1). We add 'n' to shift this to a positive index.
 */
class NQueens {
    fun solveNQueens(n: Int): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val board = Array(n) { CharArray(n) { '.' } }

        // Trackers for O(1) conflict checks
        val cols = BooleanArray(n)
        val diag1 = BooleanArray(2 * n) // row + col
        val diag2 = BooleanArray(2 * n) // row - col + n

        fun backtrack(row: Int) {
            if (row == n) {
                result.add(board.map { it.toString() })
                return
            }

            for (col in 0 until n) {
                val d1 = row + col
                val d2 = row - col + n

                // Pruning: Skip this branch if a queen already covers this path
                if (cols[col] || diag1[d1] || diag2[d2]) continue

                board[row][col] = 'Q'
                cols[col] = true; diag1[d1] = true; diag2[d2] = true

                backtrack(row + 1)

                // Backtrack: Reset state for the next candidate's "fair share"
                board[row][col] = '.'
                cols[col] = false; diag1[d1] = false; diag2[d2] = false
            }
        }

        backtrack(0)
        return result
    }
}
/**
 * (0,0) (0,1) (0,2) (1,3)
 * (1,0) (1,1) (1,2) (1,3)
 * (2,0) (2,1) (2,2) (2,3)
 * (3,0) (3,1) (3,2) (3,3)
 */