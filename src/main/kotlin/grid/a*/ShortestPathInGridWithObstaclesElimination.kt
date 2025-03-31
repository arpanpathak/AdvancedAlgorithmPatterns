package grid.`a*`

import java.util.*

class ShortestPathInGridWithObstaclesElimination {
    data class State(val r: Int, val c: Int, val steps: Int, val k: Int) : Comparable<State> {
        override fun compareTo(other: State) = (steps + heuristic()).compareTo(other.steps + other.heuristic())
        private fun heuristic() = r + c  // Manhattan distance to the goal
    }

    fun shortestPath(grid: Array<IntArray>, k: Int): Int {
        val rows = grid.size
        val cols = grid[0].size
        val directions = arrayOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)

        val pq = PriorityQueue<State>()
        pq.offer(State(0, 0, 0, k))

        val visited = Array(rows) { Array(cols) { IntArray(k + 1) { -1 } } }
        visited[0][0][k] = 0

        while (pq.isNotEmpty()) {
            val (r, c, steps, remainingK) = pq.poll()

            if (r == rows - 1 && c == cols - 1) return steps

            for ((dx, dy) in directions) {
                val nr = r + dx
                val nc = c + dy

                if (nr !in 0 until rows || nc !in 0 until cols) continue

                val newK = remainingK - grid[nr][nc]
                if (newK >= 0 && (visited[nr][nc][newK] == -1 || steps + 1 < visited[nr][nc][newK])) {
                    pq.offer(State(nr, nc, steps + 1, newK))
                    visited[nr][nc][newK] = steps + 1
                }
            }
        }

        return -1
    }
}
