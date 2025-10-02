package probability

import java.util.*

data class Edge(val neighbor: Int, val probability: Double)

class PathWithMaximumProbability {
    fun maxProbability(
        n: Int,
        edges: Array<IntArray>,
        succProb: DoubleArray,
        start_node: Int,
        end_node: Int
    ): Double {

        // 1. Build the Graph (Adjacency Map)
        val adj: Map<Int, MutableList<Edge>> = (0 until n).associateWith { mutableListOf() }

        for (i in edges.indices) {
            val (u, v) = edges[i]
            val prob = succProb[i]

            adj[u]?.add(Edge(v, prob))
            adj[v]?.add(Edge(u, prob))
        }

        // 2. Initialize Dijkstra's Structures
        val maxProbabilities = DoubleArray(n) { 0.0 }
        maxProbabilities[start_node] = 1.0

        // Max-Heap Priority Queue
        val pq = PriorityQueue<Edge>(compareByDescending { it.probability })

        pq.offer(Edge(start_node, 1.0))

        // 3. Traversal and Relaxation
        while (pq.isNotEmpty()) {
            val (u, probU) = pq.poll()

            when {
                u == end_node -> return probU
                probU < maxProbabilities[u] -> continue
            }

            // Use ?.forEach to iterate neighbors in a clean, functional style
            adj[u]?.forEach { edge ->
                val (v, probUV) = edge

                val newProbV = probU * probUV

                if (newProbV > maxProbabilities[v]) {
                    maxProbabilities[v] = newProbV
                    pq.offer(Edge(v, newProbV))
                }
            }
        }

        return maxProbabilities[end_node]
    }
}
