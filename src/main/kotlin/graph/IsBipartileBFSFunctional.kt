package graph

object IsBipartileBFSFunctional {
    enum class Color { UNCOLORED, RED, BLUE }

    fun isBipartite(graph: List<List<Int>>): Boolean {
        val colors = Array(graph.size) { Color.UNCOLORED }

        fun canAssignColor(u: Int, c: Color): Boolean {
            colors[u] = c
            val nextC = if (c == Color.RED) Color.BLUE else Color.RED

            // Returns true (isBipartite) only if NO neighbor triggers the failure condition
            return !graph[u].any { v ->
                colors[v] == c || (colors[v] == Color.UNCOLORED && !canAssignColor(v, nextC))
            }
        }

        return (graph.indices).none { i ->
            colors[i] == Color.UNCOLORED && !canAssignColor(i, Color.RED)
        }
    }
}
