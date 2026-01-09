package graph

class ChromaticNumberOptimized {
    fun minColors(adj: List<List<Int>>): Int {
        val n = adj.size
        val colors = IntArray(n)
        val sorted = (0 until n).sortedByDescending { adj[it].size }

        fun canColor(idx: Int, k: Int): Boolean {
            if (idx == n) return true
            val u = sorted[idx]

            return (1..k).any { c ->
                if (adj[u].all { colors[it] != c }) {
                    colors[u] = c
                    if (canColor(idx + 1, k)) return true
                    colors[u] = 0
                }
                false
            }
        }

        // Binary search or linear scan from 1..maxDegree+1
        return (1..n).first { k -> canColor(0, k) }
    }
}
