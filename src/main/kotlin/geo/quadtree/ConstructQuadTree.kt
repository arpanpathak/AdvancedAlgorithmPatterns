package geo.quadtree

class ConstructQuadTree {
    data class Node(
        var `val`: Boolean,
        var isLeaf: Boolean,
        var topLeft: Node? = null, var topRight: Node? = null,
        var bottomLeft: Node? = null, var bottomRight: Node? = null)

    fun construct(grid: Array<IntArray>): Node? {
        return buildTree(grid, 0, 0, grid.size)
    }

    // A separate, highly efficient helper for checking uniformity (no allocation, early exit).
    private fun isUniform(grid: Array<IntArray>, row: Int, col: Int, size: Int): Boolean {
        if (size == 0) return true

        val firstVal = grid[row][col]

        // Use clear ranges for iteration.
        for (r in row until row + size) {
            for (c in col until col + size) {
                if (grid[r][c] != firstVal) return false
            }
        }
        return true
    }

    // The main recursive function with clear parameter names.
    private fun buildTree(grid: Array<IntArray>, row: Int, col: Int, size: Int): Node {

        // 1. Base Case: Check for uniformity.
        if (isUniform(grid, row, col, size)) {
            val value = grid[row][col] == 1
            return Node(value, true)
        }

        // 2. Recursive Step: Not uniform, so divide.
        val halfSize = size / 2

        // Create the internal node and directly assign the results of the four recursive calls.
        return Node(true, false).apply {
            topLeft = buildTree(grid, row, col, halfSize)
            topRight = buildTree(grid, row, col + halfSize, halfSize)
            bottomLeft = buildTree(grid, row + halfSize, col, halfSize)
            bottomRight = buildTree(grid, row + halfSize, col + halfSize, halfSize)
        }
    }
}
