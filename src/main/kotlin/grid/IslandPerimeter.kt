package grid

class IslandPerimeter {
    fun islandPerimeter(grid: Array<IntArray>): Int {
        var perimeter = 0

        grid.forEachIndexed { r, row ->
            row.forEachIndexed { c, cell ->
                if (cell == 1) {
                    perimeter += listOf(
                        r == 0 || grid[r - 1][c] == 0,               // Top
                        r == grid.size - 1 || grid[r + 1][c] == 0,   // Bottom
                        c == 0 || grid[r][c - 1] == 0,                // Left
                        c == row.size - 1 || grid[r][c + 1] == 0      // Right
                    ).count { it } // Count how many conditions are true
                }
            }
        }

        return perimeter
    }
}
