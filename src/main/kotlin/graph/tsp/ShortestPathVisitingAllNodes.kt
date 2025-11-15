package graph.tsp

class ShortestPathVisitingAllNodes {
    data class State(val node: Int, val mask: Int, val steps: Int)

    fun shortestPathLength(graph: Array<IntArray>): Int {
        val n = graph.size
        val target = (1 shl n) - 1
        val visited = Array(n) { BooleanArray(target + 1) }
        val queue = ArrayDeque<State>().apply {
            (0 until n).forEach { i ->
                add(State(i, 1 shl i, 0))
                visited[i][1 shl i] = true
            }
        }

        while (queue.isNotEmpty()) {
            val (node, mask, steps) = queue.removeFirst()
            if (mask == target) return steps
            graph[node].forEach { neighbor ->
                val newMask = mask or (1 shl neighbor)
                if (!visited[neighbor][newMask]) {
                    visited[neighbor][newMask] = true
                    queue.add(State(neighbor, newMask, steps + 1))
                }
            }
        }

        return -1
    }
}
