package array

class TransposeMatrix {
    fun transpose(matrix: Array<IntArray>): Array<IntArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        val transposed = Array(cols) { IntArray(rows) }

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                transposed[j][i] = matrix[i][j]
            }
        }

        return transposed
    }
}