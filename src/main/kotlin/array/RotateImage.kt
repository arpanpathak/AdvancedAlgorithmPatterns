package array

class RotateImage {
    fun rotate(matrix: Array<IntArray>): Unit {
        for (i in matrix.indices)
            for (j in i until matrix[i].size) {
            swap(matrix, i, j, j, i)
        }

        for (i in matrix.indices) {
            var l = 0
            var r: Int = matrix[i].size - 1
            while (l < r) {
                swap(matrix, i, l, i, r)
                l++
                r--
            }
        }
    }

    fun swap(matrix: Array<IntArray>, i: Int, j: Int, m: Int, n: Int) {
        matrix[i][j] = matrix[m][n].also { matrix[m][n] = matrix[i][j] }
    }
}