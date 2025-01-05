package array

class SpiralMatrix {
    fun spiralOrder(matrix: Array<IntArray>): List<Int> {
        if (matrix.isEmpty()) return emptyList()

        val result = mutableListOf<Int>()
        var top = 0
        var bottom = matrix.size - 1
        var left = 0
        var right = matrix[0].size - 1

        while (top <= bottom && left <= right) {
            // Traverse top row
            for (j in left..right) result.add(matrix[top][j])
            top++

            // Traverse right column
            for (i in top..bottom) result.add(matrix[i][right])
            right--

            // Traverse bottom row
            if (top <= bottom) {
                for (j in right downTo left) result.add(matrix[bottom][j])
                bottom--
            }

            // Traverse left column
            if (left <= right) {
                for (i in bottom downTo top) result.add(matrix[i][left])
                left++
            }
        }

        return result
    }
}