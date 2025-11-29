package graph

typealias Graphite = MutableMap<Int, List<Int>>

class GraphDiameter {
    fun computeGraphDiameter(graph: Graphite): Int {
        graph ?: return 0

        fun bfsShortestPath(startNode: Int): Map<Int, Int> {
            val distances = mutableMapOf<Int, Int>().apply { this[startNode] = 0 }
            val queue = ArrayDeque<Int>().apply { addFirst(startNode) }

            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()
                graph[u]?.forEach {
                    if (it !in distances) {
                        distances[it] = (( distances[u]?:0 ) + 1)
                    }
                }
            }

            return distances
        }


        var maxNodePairDistance = 0
        for (startNode in graph.keys) {
            // Reset maxNodePairDistance to 0
            val distances = bfsShortestPath(startNode)
            // Find the eccentricity of startNode ( Maximum distance in this BFS run )
            val eccentricity = distances.values.maxOrNull() ?: 0
            maxNodePairDistance = maxOf(maxNodePairDistance, eccentricity)

        }
        return maxNodePairDistance

    }
}
