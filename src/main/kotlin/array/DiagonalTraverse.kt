package array

class DiagonalTraverse {
    // Enum to represent the direction of diagonal traversal: UP or DOWN
    enum class Direction {
        UP, DOWN
    }

    fun findDiagonalOrder(mat: Array<IntArray>): IntArray {
        val m = mat.size        // Number of rows in the matrix
        val n = mat[0].size     // Number of columns in the matrix
        val result = mutableListOf<Int>()  // List to store the diagonal traversal result

        var i = 0  // Row index
        var j = 0  // Column index
        var direction = Direction.UP  // Start moving diagonally in the UP direction

        // Continue traversing until we collect all matrix elements
        while (result.size < m * n) {
            // Add the current element to the result list
            result.add(mat[i][j])

            // Handle the change in direction and boundary checks using `when`
            direction = when (direction) {
                Direction.UP -> when {
                    // If we're at the top row (i == 0) or last column (j == n - 1)
                    i == 0 || j == n - 1 -> {
                        // If at the last column, move down
                        // Otherwise, move right
                        if (j == n - 1) i++ else j++
                        Direction.DOWN  // Switch direction to DOWN
                    }
                    else -> {
                        // Otherwise, continue moving up-left: decrement i and increment j
                        i--; j++
                        Direction.UP  // Continue moving UP
                    }
                }
                Direction.DOWN -> when {
                    // If we're at the left column (j == 0) or last row (i == m - 1)
                    j == 0 || i == m - 1 -> {
                        // If at the last row, move right
                        // Otherwise, move down
                        if (i == m - 1) j++ else i++
                        Direction.UP  // Switch direction to UP
                    }
                    else -> {
                        // Otherwise, continue moving down-right: increment i and decrement j
                        i++; j--
                        Direction.DOWN  // Continue moving DOWN
                    }
                }
            }
        }

        // Convert the result list to an array and return it
        return result.toIntArray()
    }
}