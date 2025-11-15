package graph

class MaximumPathQualityOfAGraph {
    fun maximalPathQuality(values: IntArray, edges: Array<IntArray>, maxTime: Int): Int {
        data class Edge(val node: Int, val time: Int)

        val n = values.size
        val graph = Array(n) { mutableListOf<Edge>() }

        // Build graph
        edges.forEach { (u, v, time) ->
            graph[u].add(Edge(v, time))
            graph[v].add(Edge(u, time))
        }

        var maxQuality = 0
        val visited = IntArray(n)

        fun dfs(node: Int, time: Int, quality: Int) {
            if (time > maxTime) return

            val newQuality = if (visited[node] == 0) quality + values[node] else quality
            visited[node]++

            if (node == 0) maxQuality = maxOf(maxQuality, newQuality)

            graph[node].forEach { edge ->
                dfs(edge.node, time + edge.time, newQuality)
            }

            visited[node]--
        }

        dfs(0, 0, 0)
        return maxQuality
    }
}