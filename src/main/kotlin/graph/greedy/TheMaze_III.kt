package graph.greedy

import java.util.*

class TheMaze_III {
    data class State(val dist: Int, val x: Int, val y: Int)

    fun findShortestWay(maze: Array<IntArray>, ball: IntArray, hole: IntArray): String {
        val m = maze.size
        val n = maze[0].size
        val directions = listOf(
            Triple(1, 0, "d"),
            Triple(0, -1, "l"),
            Triple(0, 1, "r"),
            Triple(-1, 0, "u")
        )

        val distance = Array(m) { IntArray(n) { Int.MAX_VALUE } }
        val path = Array(m) { Array(n) { "" } }
        val pq = PriorityQueue<State>(compareBy { it.dist })

        distance[ball[0]][ball[1]] = 0
        path[ball[0]][ball[1]] = ""
        pq.offer(State(0, ball[0], ball[1]))

        while (pq.isNotEmpty()) {
            val (d, x, y) = pq.poll()

            if (x == hole[0] && y == hole[1]) {
                return path[x][y]
            }

            if (d > distance[x][y]) continue

            for ((dx, dy, dir) in directions) {
                var nx = x
                var ny = y
                var steps = 0

                while (nx + dx in 0 until m && ny + dy in 0 until n && maze[nx + dx][ny + dy] == 0) {
                    nx += dx
                    ny += dy
                    steps++
                    if (nx == hole[0] && ny == hole[1]) break
                }

                val newDist = d + steps
                val newPath = path[x][y] + dir

                if (newDist < distance[nx][ny]) {
                    distance[nx][ny] = newDist
                    path[nx][ny] = newPath
                    pq.offer(State(newDist, nx, ny))
                } else if (newDist == distance[nx][ny] && newPath < path[nx][ny]) {
                    path[nx][ny] = newPath
                    pq.offer(State(newDist, nx, ny))
                }
            }
        }

        return "impossible"
    }
}
