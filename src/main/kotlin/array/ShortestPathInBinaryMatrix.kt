package array

import java.util.*

class ShortestPathInBinaryMatrix {
    data class State(val row: Int, val col: Int, val dist: Int)

    fun shortestPathBinaryMatrix(grid: Array<IntArray>): Int {
        val n = grid.size
        val m = grid[0].size

        // Early return if start or end is blocked
        if (grid[0][0] == 1 || grid[n - 1][m - 1] == 1) return -1

        // Directions for 8 neighbors (horizontal, vertical, and diagonal)
        val directions = arrayOf(
            -1 to 0, 1 to 0, 0 to -1, 0 to 1,  // Left, right, up, down
            -1 to -1, -1 to 1, 1 to -1, 1 to 1 // Diagonal directions
        )

        val queue = LinkedList<State>()
        queue.add(State(0, 0, 1))  // Start at (0,0) with distance 1

        while (queue.isNotEmpty()) {
            val (row, col, dist) = queue.poll()

            // If we reached the bottom-right corner
            if (row == n - 1 && col == m - 1) return dist

            // Explore all 8 possible directions
            for ((dr, dc) in directions) {
                val (newRow, newCol) = row + dr to col + dc

                // Skip invalid positions or visited cells
                if (newRow !in 0 until n || newCol !in 0 until m || grid[newRow][newCol] != 0)
                    continue

                // Mark the cell as visited and add it to the queue
                grid[newRow][newCol] = 2
                queue.add(State(newRow, newCol, dist + 1))
            }
        }

        return -1  // No path found
    }
}
