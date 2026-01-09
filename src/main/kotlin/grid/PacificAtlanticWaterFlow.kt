package grid

import java.util.*

class PacificAtlanticWaterFlow {
    data class Cell(val r: Int, val c: Int)

    private val directions = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)

    fun pacificAtlantic(heights: Array<IntArray>): List<List<Int>> {
        val rows = heights.size
        val cols = heights[0].size

        fun bfs(starts: List<Cell>): Array<BooleanArray> {
            val reachable = Array(rows) { BooleanArray(cols) }.apply {
                starts.forEach { (r, c) -> this[r][c] = true }
            }

            val queue: Queue<Cell> = LinkedList(starts)

            while (queue.isNotEmpty()) {
                val (r, c) = queue.poll()
                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc

                    if (nr in 0 until rows && nc in 0 until cols &&
                        !reachable[nr][nc] && heights[nr][nc] >= heights[r][c]) {
                        reachable[nr][nc] = true
                        queue.add(Cell(nr, nc))
                    }
                }
            }
            return reachable
        }

        val pMap = bfs(List(rows) { Cell(it, 0) } + List(cols) { Cell(0, it) })
        val aMap = bfs(List(rows) { Cell(it, cols - 1) } + List(cols) { Cell(rows - 1, it) })

        val result = mutableListOf<List<Int>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (pMap[r][c] && aMap[r][c]) {
                    result.add(listOf(r, c))
                }
            }
        }
        return result
    }
}