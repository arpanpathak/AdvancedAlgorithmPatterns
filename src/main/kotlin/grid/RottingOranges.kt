package grid

import java.util.*

class RottingOranges {
    fun orangesRotting(grid: Array<IntArray>): Int {
        val (rows, cols) = grid.size to grid[0].size
        val directions = arrayOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0))  // Directions
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        var remainingFreshCount = 0

        // Initialize queue with rotten oranges and count fresh ones
        grid.forEachIndexed { r, row ->
            row.forEachIndexed { c, value ->
                when (value) {
                    2 -> queue.add(Pair(r, c)) // Rotten orange
                    1 -> remainingFreshCount++           // Fresh orange
                }
            }
        }

        if (remainingFreshCount == 0) return 0  // No fresh oranges

        var minutes = 0

        // BFS process using repeat to iterate through levels (minutes)
        while (queue.isNotEmpty()) {
            repeat(queue.size) {
                val (r, c) = queue.poll()
                directions.forEach { (dr, dc) ->
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2  // Rot the fresh orange
                        queue.add(Pair(nr, nc))
                        remainingFreshCount--
                    }
                }
            }
            minutes++
        }

        return if (remainingFreshCount == 0) minutes - 1 else -1  // Return the time taken or -1 if not all rot
    }
}