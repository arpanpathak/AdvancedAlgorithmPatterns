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

/**
 * 1,2,3
 * 4,5,6
 * 7,8,9
 *
 * 1,4,7
 * 2,5,8
 * 3,6,9
 */

