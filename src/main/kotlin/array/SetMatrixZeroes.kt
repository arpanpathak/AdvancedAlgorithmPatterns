package array

class SetMatrixZeroes {
    fun setZeroes(matrix: Array<IntArray>): Unit {

        val firstRowHasZero = matrix[0].any { it == 0 }
        val firstColHasZero = matrix.any { it[0] == 0 }

        // Use first row/col as markers
        for (i in 1 until matrix.size) {
            for (j in 1 until matrix[0].size) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0
                    matrix[0][j] = 0
                }
            }
        }

        // Zero out based on markers
        for (i in 1 until matrix.size) {
            for (j in 1 until matrix[0].size) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0
                }
            }
        }

        // Handle first row and column
        if (firstRowHasZero) matrix[0].fill(0)
        if (firstColHasZero) matrix.forEach { it[0] = 0 }
    }
}