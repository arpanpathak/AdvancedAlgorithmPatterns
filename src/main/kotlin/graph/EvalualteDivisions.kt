package graph

class EvaluateDivisions {
    private data class NodeState(val id: String, val product: Double)

    fun calcEquation(equations: List<List<String>>, values: DoubleArray, queries: List<List<String>>): DoubleArray {
        // Build graph: Map<String, Map<String, Double>>
        // This handles (u -> v) and (v -> u) in a flat, functional flow
        val graph = mutableMapOf<String, MutableMap<String, Double>>()
        equations.forEachIndexed { i, (u, v) ->
            graph.getOrPut(u) { mutableMapOf() }[v] = values[i]
            graph.getOrPut(v) { mutableMapOf() }[u] = 1.0 / values[i]
        }

        fun bfs(start: String, target: String): Double {
            if (start !in graph || target !in graph) return -1.0
            if (start == target) return 1.0

            val queue = ArrayDeque<NodeState>().apply { add(NodeState(start, 1.0)) }
            val visited = mutableSetOf(start)

            while (queue.isNotEmpty()) {
                val (curr, ratio) = queue.removeFirst()
                if (curr == target) return ratio

                graph[curr]?.forEach { (next, weight) ->
                    if (visited.add(next)) {
                        queue.add(NodeState(next, ratio * weight))
                    }
                }
            }
            return -1.0
        }

        return DoubleArray(queries.size) { i -> bfs(queries[i][0], queries[i][1]) }
    }
}
