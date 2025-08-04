package graph.dp

object FloydWarshallAlgorithm {
    const val INF = 1_000_000_000

    fun floydWarshall(graph: Array<IntArray>): Array<IntArray> {
        val n = graph.size
        val dist = Array(n) { i -> IntArray(n) { j -> graph[i][j] } }

        // Core DP logic
        for (k in 0 until n) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    if (dist[i][k] < INF && dist[k][j] < INF) {
                        dist[i][j] = minOf(dist[i][j], dist[i][k] + dist[k][j])
                    }
                }
            }
        }

        return dist
    }

    fun hasNegativeCycle(dist: Array<IntArray>): Boolean {
        return dist.indices.any { i -> dist[i][i] < 0 }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val INF = FloydWarshallAlgorithm.INF
        val graph = arrayOf(
            intArrayOf(0,   3,   INF, 5),
            intArrayOf(2,   0,   INF, 4),
            intArrayOf(INF, 1,   0,   INF),
            intArrayOf(INF, INF, 2,   0)
        )

        val dist = floydWarshall(graph)

        println("All-pairs shortest path matrix:")
        dist.forEach { row ->
            println(row.joinToString(" ") { d -> if (d == INF) "∞" else d.toString() })
        }

        println()
        when (hasNegativeCycle(dist)) {
            true -> println("Graph contains a negative weight cycle.")
            false -> println("No negative weight cycles detected.")
        }
    }
}
