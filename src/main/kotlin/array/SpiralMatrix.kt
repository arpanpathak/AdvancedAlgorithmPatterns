package array

class SpiralMatrix {
    fun spiralOrder(matrix: Array<IntArray>): List<Int> {
        if (matrix.isEmpty()) return emptyList()

        val result = mutableListOf<Int>()
        var (top, bottom, left, right) = listOf(0, matrix.size - 1, 0, matrix[0].size - 1)
        var count = 0
        val totalElements = matrix.size * matrix[0].size

        while (count < totalElements) {
            // Traverse top row
            for (j in left..right) if (count++ < totalElements) result.add(matrix[top][j])
            top++

            // Traverse right column
            for (i in top..bottom) if (count++ < totalElements) result.add(matrix[i][right])
            right--

            // Traverse bottom row
            for (j in right downTo left) if (count++ < totalElements) result.add(matrix[bottom][j])
            bottom--

            // Traverse left column
            for (i in bottom downTo top) if (count++ < totalElements) result.add(matrix[i][left])
            left++
        }

        return result
    }
}