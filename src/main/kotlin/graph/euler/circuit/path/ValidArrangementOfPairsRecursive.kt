package graph.euler.circuit.path

class ValidArrangementOfPairsRecursive {
    fun validArrangement(pairs: Array<IntArray>): Array<IntArray> {
        val graph = mutableMapOf<Int, ArrayDeque<Int>>()
        val degree = mutableMapOf<Int, Int>().withDefault { 0 }

        // Build graph and track degree difference (out - in)
        pairs.forEach { (u, v) ->
            graph.getOrPut(u) { ArrayDeque() }.add(v)
            degree[u] = degree.getValue(u) + 1
            degree[v] = degree.getValue(v) - 1
        }

        // Find start node (outDegree > inDegree)
        val start = degree.keys.firstOrNull { degree.getValue(it) == 1 } ?: pairs[0][0]

        val path = mutableListOf<IntArray>()

        // Recursive DFS for Hierholzer's algorithm
        fun dfs(u: Int) {
            while (graph[u]?.isNotEmpty() == true) {
                val v = graph[u]!!.removeFirst()
                dfs(v)
                path.add(intArrayOf(u, v))
            }
        }

        dfs(start)
        return path.asReversed().toTypedArray()
    }
}