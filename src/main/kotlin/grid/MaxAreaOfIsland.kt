package grid

class MaxAreaOfIsland {
    fun maxAreaOfIsland(grid: Array<IntArray>): Int {
        // Edge case: if the grid is empty, return 0
        if (grid.isEmpty()) return 0

        // Get the grid dimensions
        val (rows, cols ) = grid.size to grid[0].size
        var maxArea = 0

        // Define directions using 'to' to create pairs (row, column) offsets for up, down, left, and right
        val directions = arrayOf(-1 to 0,  1 to 0,   0 to -1,  0 to 1)

        // DFS helper function to explore the island
        fun dfs(r: Int, c: Int): Int {
            // If out of bounds or the cell is water (0), return area 0
            if (r < 0 || c < 0 || r >= rows || c >= cols || grid[r][c] == 0) {
                return 0
            }

            // Mark the current cell as visited by setting it to 0
            grid[r][c] = 0

            // Initialize the area for the current island (starting with 1 for the current land)
            var area = 1

            // Explore all 4 directions using the directions array
            for ((dr, dc) in directions) {
                area += dfs(r + dr, c + dc)
            }

            return area
        }

        // Loop through each cell in the grid
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                // If it's land, start a DFS to calculate the island area
                if (grid[r][c] == 1) {
                    val area = dfs(r, c)
                    maxArea = maxOf(maxArea, area)
                }
            }
        }

        return maxArea
    }
}
