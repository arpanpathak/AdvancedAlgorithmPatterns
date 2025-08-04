package graph.dp


object BellmanFordAlgorithm {
    data class Edge(val from: Int, val to: Int, val weight: Int)

    fun bellmanFord(vertices: Int, edges: List<Edge>, source: Int): Pair<IntArray, Boolean> {
        val dist = IntArray(vertices) { Int.MAX_VALUE }.apply { this[source] = 0 }

        repeat(vertices - 1) {
            edges.forEach { (u, v, w) ->
                if (dist[u] != Int.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w
                }
            }
        }

        val hasNegativeCycle = edges.any { (u, v, w) ->
            dist[u] != Int.MAX_VALUE && dist[u] + w < dist[v]
        }

        return dist to hasNegativeCycle
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val edges = listOf(
            Edge(0, 1, 4),
            Edge(0, 2, 5),
            Edge(1, 2, -3),
            Edge(2, 3, 4),
            Edge(3, 1, -6)
        )

        val (distances, hasNegativeCycle) = bellmanFord(vertices = 4, edges = edges, source = 0)

        if (hasNegativeCycle) {
            println("Graph contains a negative weight cycle.")
        } else {
            println("Shortest distances from source 0:")
            distances.forEachIndexed { i, d ->
                val label = if (d == Int.MAX_VALUE) "∞" else d.toString()
                println("To vertex $i: $label")
            }
        }
    }
}
