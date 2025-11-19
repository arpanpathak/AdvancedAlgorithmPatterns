package graph

class IsBipartileGraph {
    enum class Color { A, B, NONE }

    fun isBipartite(graph: Array<IntArray>): Boolean {
        val assignedColors = Array<Color>(graph.size) { Color.NONE }

        fun bfs(i: Int): Boolean {
            if (assignedColors[i] == Color.A) return true
            assignedColors[i] = Color.B
            val q = ArrayDeque<Int>()
            q.addLast(i)

            while (q.isNotEmpty()) {
                val cur = q.removeFirst()
                val curColor = assignedColors[cur]
                for (n in graph[cur]) {
                    when(assignedColors[n]) {
                        curColor  -> return false
                        Color.NONE -> {
                            q.addLast(n)
                            assignedColors[n] = if (curColor == Color.A) Color.B else Color.A
                        }

                        else -> {}
                    }
                }
            }
            return true
        }

        for (i in graph.indices) {
            if (!bfs(i)) return false
        }

        return true
    }
}