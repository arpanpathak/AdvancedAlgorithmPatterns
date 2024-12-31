package grid

class MakingALargeIsland {
    fun largestIsland(grid: Array<IntArray>): Int {
        val n = grid.size
        val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0) // Dx, Dy
        val islandSizes = mutableMapOf<Int, Int>()
        var maxArea = 0
        var islandId = 2

        fun dfs(r: Int, c: Int, id: Int): Int {
            if (r !in grid.indices || c !in grid[0].indices || grid[r][c] != 1) return 0
            grid[r][c] = id
            var size = 1
            for ((dr, dc) in directions) {
                size += dfs(r + dr, c + dc, id)
            }
            return size
        }

        // First pass: find island sizes
        for (r in grid.indices) {
            for (c in grid[0].indices) {
                if (grid[r][c] == 1) {
                    val size = dfs(r, c, islandId++)
                    islandSizes[islandId - 1] = size
                    maxArea = maxOf(maxArea, size)
                }
            }
        }

        // Second pass: check conversion of 0s to 1s
        fun calculatePotentialArea(r: Int, c: Int): Int {
            val uniqueIslands = mutableSetOf<Int>()
            var potentialSize = 1
            for ((dr, dc) in directions) {
                val nr = r + dr
                val nc = c + dc
                if (nr in grid.indices && nc in grid[0].indices && grid[nr][nc] > 1) {
                    uniqueIslands.add(grid[nr][nc])
                }
            }
            return potentialSize + uniqueIslands.sumOf { islandSizes[it]!! }
        }

        // Apply the conversion and update maxArea
        for (r in grid.indices) {
            for (c in grid[0].indices) {
                if (grid[r][c] == 0) {
                    maxArea = maxOf(maxArea, calculatePotentialArea(r, c))
                }
            }
        }

        return maxArea
    }
}
