package backtracking

fun solveNQueens(n: Int): List<List<String>> {
    val results = mutableListOf<List<String>>()
    val board = Array(n) { CharArray(n) { '.' } }
    val occupied = mutableSetOf<String>()

    fun isSafe(r: Int, c: Int) =
        listOf("c$c", "d${r - c}", "a${r + c}").none { it in occupied }

    fun toggleQueen(r: Int, c: Int, remove: Boolean = false) {
        val keys = listOf("c$c", "d${r - c}", "a${r + c}")
        when(remove) {
            true -> occupied.removeAll(keys)
            else -> occupied.addAll(keys)
        }
    }

    fun backtrack(r: Int) {
        if (r == n) {
            results.add(board.map { String(it) })
            return
        }

        for (c in 0 until n) {
            if (isSafe(r, c)) {
                board[r][c] = 'Q'
                toggleQueen(r, c, remove = false)

                backtrack(r + 1)

                toggleQueen(r, c, remove = true)
                board[r][c] = '.'
            }
        }
    }

    backtrack(0)
    return results
}
