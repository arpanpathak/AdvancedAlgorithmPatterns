package grid

class FloodFill {
    fun floodFill(image: Array<IntArray>, sr: Int, sc: Int, color: Int): Array<IntArray> {
        val initialColor = image[sr][sc]
        if (initialColor == color) return image // Avoid infinite recursion

        fun dfs(row: Int, col: Int) {
            if (row !in 0 until image.size || col !in 0 until image[0].size || image[row][col] != initialColor)
                return

            image[row][col] = color

            for ((dx, dy) in listOf(0 to 1, 1 to 0, -1 to 0, 0 to -1))
                dfs(row + dx, col + dy)
        }

        dfs(sr, sc)
        return image
    }
}