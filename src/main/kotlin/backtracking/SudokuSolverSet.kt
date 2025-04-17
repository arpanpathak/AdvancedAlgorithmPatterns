package backtracking

class SudokuSolverSet {
    fun solveSudoku(board: Array<CharArray>) {
        val seen = mutableSetOf<String>()

        // Add initial values to the seen set
        for (i in 0..8) {
            for (j in 0..8) {
                val ch = board[i][j]
                if (ch != '.') {
                    addToSet(i, j, ch, seen)
                }
            }
        }

        fun isSafe(i: Int, j: Int, c: Char): Boolean {
            return "$c row $i" !in seen &&
                    "$c col $j" !in seen &&
                    "$c block ${i / 3}${j / 3}" !in seen
        }

        fun dfs(): Boolean {
            for (i in 0..8) {
                for (j in 0..8) {
                    if (board[i][j] != '.') continue
                    for (c in '1'..'9') {
                        if (!isSafe(i, j, c)) continue
                        board[i][j] = c
                        addToSet(i, j, c, seen)
                        if (dfs()) return true
                        board[i][j] = '.'
                        removeFromSet(i, j, c, seen)
                    }
                    return false
                }
            }
            return true
        }

        dfs()
    }

    fun addToSet(i: Int, j: Int, c: Char, seen: MutableSet<String>) {
        seen.add("$c row $i")
        seen.add("$c col $j")
        seen.add("$c block ${i / 3}${j / 3}")
    }

    fun removeFromSet(i: Int, j: Int, c: Char, seen: MutableSet<String>) {
        seen.remove("$c row $i")
        seen.remove("$c col $j")
        seen.remove("$c block ${i / 3}${j / 3}")
    }
}
