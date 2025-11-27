package graph

class ChromaticNumber {
    fun minColors(graph: Array<IntArray>): Int {
        val n = graph.size
        val colors = IntArray(n)

        fun isValid(u: Int, c: Int): Boolean = graph[u].none { colors[it] == c }

        fun canColor(u: Int, k: Int): Boolean {
            if (u == n) return true

            for (c in 1..k) {
                if (isValid(u, c)) {
                    colors[u] = c
                    if (canColor(u + 1, k)) return true
                    colors[u] = 0
                }
            }
            return false
        }

        for (k in 1..n) {
            if (canColor(0, k)) return k
        }

        return n
    }
}