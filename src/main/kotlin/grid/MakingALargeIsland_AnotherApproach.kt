package grid

class MakingALargeIsland_AnotherApproach {
    fun largestIsland(grid: Array<IntArray>): Int {
        val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
        val islandSizes = mutableMapOf<Int, Int>()
        var ( maxArea, islandId) = 0 to 2

        fun dfs(r: Int, c: Int, id: Int): Int {
            if (r !in grid.indices || c !in grid[0].indices || grid[r][c] != 1) return 0
            grid[r][c] = id
            return 1 + directions.sumOf { (dr, dc) -> dfs(r + dr, c + dc, id) }
        }

        // First pass: find island sizes
        for (r in grid.indices) {
            for (c in grid[0].indices) {
                if (grid[r][c] == 1) {
                    islandSizes[islandId] = dfs(r, c, islandId)
                    maxArea = maxOf(maxArea, islandSizes[islandId++]!!)
                }
            }
        }

        // Second pass: check conversion of 0s to 1s
        for (r in grid.indices) {
            for (c in grid[0].indices) {
                if (grid[r][c] == 0) {
                    val uniqueIslands = directions.mapNotNull { (dr, dc) ->
                        // it != 0 also works
                        grid.getOrNull(r + dr)?.getOrNull(c + dc)?.takeIf { it > 1 }
                    }.toSet()
                    maxArea = maxOf(maxArea, 1 + uniqueIslands.sumOf { islandSizes[it]!! })
                }
            }
        }

        return maxArea
    }
}
