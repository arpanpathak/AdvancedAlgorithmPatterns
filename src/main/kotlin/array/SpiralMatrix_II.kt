package array

class SpiralMatrix_II {
    class Solution {
        fun generateMatrix(n: Int): Array<IntArray> {
            val matrix = Array(n) { IntArray(n) }
            var (top, bottom, left, right, num) = arrayOf(0, n - 1, 0, n - 1, 1)

            while (top <= bottom && left <= right) {
                // Fill top row
                for (i in left..right) matrix[top][i] = num++
                top++

                // Fill right column
                for (i in top..bottom) matrix[i][right] = num++
                right--

                // Fill bottom row
                if (top <= bottom) {
                    for (i in right downTo left) matrix[bottom][i] = num++
                    bottom--
                }

                // Fill left column
                if (left <= right) {
                    for (i in bottom downTo top) matrix[i][left] = num++
                    left++
                }
            }

            return matrix
        }
    }
}
