package array

class SearchA2dMatrix_II {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        var (row, col) = matrix.size - 1 to 0

        while (row >=0 && col < matrix[0].size) {
            when {
                matrix[row][col] > target -> row--
                matrix[row][col] < target -> col++
                else -> return true
            }
        }

        return false
    }
}
