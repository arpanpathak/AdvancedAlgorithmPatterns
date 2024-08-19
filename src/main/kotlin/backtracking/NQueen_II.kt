package backtracking

class NQueen_II {
    private lateinit var placed: IntArray

    fun totalNQueens(n: Int): Int {
        placed = IntArray(n) { -1 } // Initialize with -1 indicating no queens are placed
        var solutionCount = 0

        fun backtrack(row: Int) {
            if (row == n) {
                // Found a valid solution, increment the count
                solutionCount++
                return
            }

            for (col in 0 until n) {
                if (isSafe(row, col)) {
                    placed[row] = col
                    backtrack(row + 1)
                    placed[row] = -1 // Remove the queen (backtrack)
                }
            }
        }

        backtrack(0)
        return solutionCount
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