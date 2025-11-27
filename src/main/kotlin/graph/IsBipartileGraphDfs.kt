package graph

class IsBipartileGraphDfs{
    enum class Color { A, B, NONE }

    fun isBipartite(graph: Array<IntArray>): Boolean {
        val assignedColors = Array(graph.size) { Color.NONE }

        fun dfs(u: Int, targetColor: Color): Boolean {
            if (assignedColors[u] != Color.NONE) {
                return assignedColors[u] == targetColor
            }

            assignedColors[u] = targetColor
            val nextColor = if (targetColor == Color.A) Color.B else Color.A

            for (v in graph[u]) {
                if (!dfs(v, nextColor)) return false
            }

            return true
        }

        for (i in graph.indices) {
            if (assignedColors[i] == Color.NONE) {
                if (!dfs(i, Color.A)) return false
            }
        }

        return true
    }
}