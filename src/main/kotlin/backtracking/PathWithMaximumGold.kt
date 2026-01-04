package backtracking

class PathWithMaximumGold {
    private val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)

    fun getMaximumGold(grid: Array<IntArray>): Int {
        fun dfs(r: Int, c: Int): Int {
            if (r !in grid.indices || c !in grid[0].indices || grid[r][c] == 0) return 0

            val gold = grid[r][c]
            grid[r][c] = 0 // Mark visited

            val maxSubPath = directions.maxOf { (dr, dc) ->
                dfs(r + dr, c + dc)
            }

            grid[r][c] = gold // Backtrack
            return gold + maxSubPath
        }

        return grid.indices.maxOf { r ->
            grid[0].indices.maxOf { c -> dfs(r, c) }
        }
    }
}