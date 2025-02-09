package grid

import oracle.net.aso.c

class ShortestDistanceFromAllBuildings {
    private val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

    fun shortestDistance(grid: Array<IntArray>): Int {
        val rows = grid.size
        val cols = grid[0].size
        val totalDistance = Array(rows) { IntArray(cols) } // Tracks total distance
        var emptyLandValue = 0 // Tracks reachable cells
        var minDistance = Int.MAX_VALUE

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (grid[row][col] == 1) { // Found a building
                    minDistance = bfs(grid, row, col, totalDistance, emptyLandValue)
                    if (minDistance == Int.MAX_VALUE) return -1 // No reachable land
                    emptyLandValue-- // Update for next BFS
                }
            }
        }

        return if (minDistance == Int.MAX_VALUE) -1 else minDistance
    }

    private fun bfs(
        grid: Array<IntArray>,
        startRow: Int,
        startCol: Int,
        totalDistance: Array<IntArray>,
        emptyLandValue: Int
    ): Int {
        val rows = grid.size
        val cols = grid[0].size
        val queue = ArrayDeque<Pair<Int, Int>>().apply { add(startRow to startCol) }
        var steps = 0
        var minDistance = Int.MAX_VALUE

        while (queue.isNotEmpty()) {
            steps++
            repeat(queue.size) {
                val (currentRow, currentCol) = queue.removeFirst()
                for ((dr, dc) in directions) {
                    val newRow = currentRow + dr
                    val newCol = currentCol + dc
                    if (newRow in 0 until rows && newCol in 0 until cols && grid[newRow][newCol] == emptyLandValue) {
                        grid[newRow][newCol]-- // Mark as visited
                        totalDistance[newRow][newCol] += steps
                        queue.add(newRow to newCol)
                        minDistance = minOf(minDistance, totalDistance[newRow][newCol])
                    }
                }
            }
        }

        return minDistance
    }
}
