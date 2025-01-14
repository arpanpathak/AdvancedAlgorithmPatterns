package grid

class ShortestBridge {
    fun shortestBridge(grid: Array<IntArray>): Int {
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        val queue = ArrayDeque<Pair<Int, Int>>()
        val n = grid.size

        // Helper function to perform DFS
        fun dfs(x: Int, y: Int) {
            if (x !in 0 until n || y !in 0 until n || grid[x][y] != 1) return
            grid[x][y] = 2 // Mark as visited
            queue.add(x to y) // Add to the BFS queue
            directions.forEach { (dx, dy) -> dfs(x + dx, y + dy) }
        }

        // Find the first island and mark it
        outer@for (i in 0 until n) {
            for (j in 0 until n) {
                if (grid[i][j] == 1) {
                    dfs(i, j)
                    break@outer
                }
            }
        }

        // Perform BFS to find the shortest bridge
        var steps = 0
        while (queue.isNotEmpty()) {
            repeat(queue.size) {
                val (x, y) = queue.removeFirst()
                directions.forEach { (dx, dy) ->
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in 0 until n && ny in 0 until n) {
                        when (grid[nx][ny]) {
                            1 -> return steps // Reached the second island
                            0 -> {
                                grid[nx][ny] = 2
                                queue.add(nx to ny)
                            }
                        }
                    }
                }
            }
            steps++
        }
        return -1 // Shouldn't reach here
    }

}