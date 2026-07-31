package grid.a_star

import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class ShortestPathInGridWithObstaclesElimination {
    data class State(val r: Int, val c: Int, val steps: Int, val k: Int)

    fun shortestPath(grid: Array<IntArray>, k: Int): Int {
        val rows = grid.size
        val cols = grid[0].size
        val directions = arrayOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)

        // Target coordinates for heuristic calculations
        val targetR = rows - 1
        val targetC = cols - 1

        // Helper function to calculate remaining Manhattan distance
        fun heuristic(r: Int, c: Int) = abs(targetR - r) + abs(targetC - c)

        // Custom comparator based on expected total cost (steps + heuristic)
        val pq = PriorityQueue<State> { a, b ->
            val costA = a.steps + heuristic(a.r, a.c)
            val costB = b.steps + heuristic(b.r, b.c)
            costA.compareTo(costB)
        }

        pq.offer(State(0, 0, 0, k))

        // Tracks the maximum remaining K seen for any (r, c) cell
        val visited = Array(rows) { IntArray(cols) { -1 } }
        visited[0][0] = k

        while (pq.isNotEmpty()) {
            val (r, c, steps, remainingK) = pq.poll()

            if (r == targetR && c == targetC) return steps

            for ((dx, dy) in directions) {
                val nr = r + dx
                val nc = c + dy

                if (nr !in 0 until rows || nc !in 0 until cols) continue

                val newK = remainingK - grid[nr][nc]
                if (newK >= 0 && newK > visited[nr][nc]) {
                    pq.offer(State(nr, nc, steps + 1, newK))
                    visited[nr][nc] = newK
                }
            }
        }

        return -1

    }
}


/**
 * Behavior Driven Testing....
 */
fun main() {
    // Enable assertions explicitly if running via JVM flags (-ea)
    // or use custom blocks if you want guaranteed execution in any playground.

    `should find direct path when no obstacles exist`()
    `should utilize K elimination to blast through a wall if it yields a shorter path`()
    `should return negative one when blocked by obstacles and out of K eliminations`()

    println("All behavioral tests executed successfully.")
}

fun `should find direct path when no obstacles exist`() {
    val solver = ShortestPathInGridWithObstaclesElimination()
    val grid = arrayOf(
        intArrayOf(0, 0),
        intArrayOf(0, 0)
    )
    val k = 0

    val actual = solver.shortestPath(grid, k)

    check(actual == 2) { "Expected path of 2 steps, but got $actual" }
}

fun `should utilize K elimination to blast through a wall if it yields a shorter path`() {
    val solver = ShortestPathInGridWithObstaclesElimination()
    val grid = arrayOf(
        intArrayOf(0, 1, 0),
        intArrayOf(1, 1, 0),
        intArrayOf(0, 0, 0)
    )
    val k = 1

    val actual = solver.shortestPath(grid, k)

    check(actual == 4) { "Expected path of 4 steps using k-destruction, but got $actual" }
}

fun `should return negative one when blocked by obstacles and out of K eliminations`() {
    val solver = ShortestPathInGridWithObstaclesElimination()
    val grid = arrayOf(
        intArrayOf(0, 1),
        intArrayOf(1, 1)
    )
    val k = 0

    val actual = solver.shortestPath(grid, k)

    check(actual == -1) { "Expected -1 because path is fully blocked, but got $actual" }
}