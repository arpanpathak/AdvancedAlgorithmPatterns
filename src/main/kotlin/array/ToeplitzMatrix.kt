package array

class ToeplitzMatrix {
    fun isToeplitzMatrix(matrix: Array<IntArray>): Boolean {
        val m = matrix.size
        val n = matrix[0].size

        var row = m - 1
        var col = 0

        while (row < m && col < n) {
            var i = row
            var j = col

            val first = matrix[i][j]

            while (i < m && j < n) {
                if (matrix[i][j] != first)
                    return false
                i++
                j++
            }

            when(row) {
                0 -> col++
                else -> row --
            }
        }

        return true
    }
}