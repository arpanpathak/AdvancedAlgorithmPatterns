package grid.dynamic_programming

class CherryPickup_II {
    data class State(val r: Int, val c1: Int, val c2: Int)

    fun cherryPickup(grid: Array<IntArray>): Int {
        val (rows, cols) = grid.size to grid[0].size
        val cache = mutableMapOf<State, Int>()

        // Column offsets for robots
        val dirs = intArrayOf(-1, 0, 1)

        fun pickupCherry(row: Int, robot1Col: Int, robot2Col: Int)
            = if (robot1Col == robot2Col) grid[row][robot1Col] else grid[row][robot1Col] + grid[row][robot2Col]

        fun solve(r: Int, c1: Int, c2: Int): Int =
            cache.getOrPut(State(r, c1, c2)) {
                when {
                    c1 !in 0 until cols || c2 !in 0 until cols -> Int.MIN_VALUE
                    r == rows - 1 -> pickupCherry(r, c1, c2)
                    else -> {
                        pickupCherry(r, c1, c2) + dirs.maxOf { dc1 ->
                            dirs.maxOf { dc2 ->
                                solve(r + 1, c1 + dc1, c2 + dc2)
                            }
                        }
                    }
                }
            }

        return solve(0, 0, cols - 1)
    }
}