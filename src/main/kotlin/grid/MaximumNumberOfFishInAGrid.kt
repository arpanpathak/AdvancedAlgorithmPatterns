package grid

class MaximumNumberOfFishInAGrid {
    fun findMaxFish(grid: Array<IntArray>): Int {
        val directions = arrayOf(1 to 0, 0 to 1, -1 to 0, 0 to -1)
        var maxFishes = 0

        fun bfs(row: Int, col: Int): Int {
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(Pair(row, col))
            var totalFish = 0

            while (queue.isNotEmpty()) {
                val (r, c) = queue.removeFirst()
                totalFish += grid[r][c] // Add fish here instead of before the loop
                grid[r][c] = 0 // Mark the cell as visited when processing

                for ((dx, dy) in directions) {
                    val newRow = r + dx
                    val newCol = c + dy

                    if (newRow in grid.indices && newCol in grid[0].indices && grid[newRow][newCol] > 0) {
                        queue.add(Pair(newRow, newCol))
                    }
                }
            }

            return totalFish
        }

        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] > 0) {
                    maxFishes = maxOf(maxFishes, bfs(i, j))
                }
            }
        }

        return maxFishes
    }
}
