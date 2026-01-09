package grid

import java.util.*

class TrappingRainwater_II{
    private data class Cell(val height: Int, val r: Int, val c: Int)

    fun trapRainWater(heightMap: Array<IntArray>): Int {
        if (heightMap.isEmpty() || heightMap[0].isEmpty()) return 0

        val rows = heightMap.size
        val cols = heightMap[0].size
        val visited = Array(rows) { BooleanArray(cols) }
        val pq = PriorityQueue<Cell>(compareBy { it.height })

        for (r in 0 until rows) {
            pq.offer(Cell(heightMap[r][0], r, 0))
            pq.offer(Cell(heightMap[r][cols - 1], r, cols - 1))
            visited[r][0] = true
            visited[r][cols - 1] = true
        }

        for (c in 1 until cols - 1) {
            pq.offer(Cell(heightMap[0][c], 0, c))
            pq.offer(Cell(heightMap[rows - 1][c], rows - 1, c))
            visited[0][c] = true
            visited[rows - 1][c] = true
        }

        var water = 0
        val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)

        while (pq.isNotEmpty()) {
            val (h, r, c) = pq.poll()

            for ((dr, dc) in directions) {
                val nr = r + dr
                val nc = c + dc

                if (nr in 0 until rows && nc in 0 until cols && !visited[nr][nc]) {
                    visited[nr][nc] = true
                    water += maxOf(0, h - heightMap[nr][nc])
                    pq.offer(Cell(maxOf(h, heightMap[nr][nc]), nr, nc))
                }
            }
        }

        return water
    }
}