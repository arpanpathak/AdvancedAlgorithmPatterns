package graph

class WelshPowellClean {
    // Defines the structure for sorting: node index and its degree
    private data class Vertex(val index: Int, val degree: Int)

    fun greedyColor(graph: Array<IntArray>): Int {
        val n = graph.size
        // -1 signifies the vertex is uncolored
        val colors = IntArray(n) { -1 }

        // 1. Calculate degrees and sort vertices in descending order of degree
        val sortedVertices = graph.indices
            .map { i -> Vertex(i, graph[i].size) }
            .sortedByDescending { it.degree }
            .map { it.index }

        var maxColorUsed = 0

        // 2. Sequentially color the graph
        for (u in sortedVertices) {
            if (colors[u] != -1) continue // Already colored! The greedy Algorithm doesn't give a fuck

            // Determine colors used by neighbors (forbidden colors)
            val forbiddenColors = graph[u]
                .filter { v -> colors[v] != -1 }
                .map { v -> colors[v] }
                .toSet()

            // Find the smallest non-forbidden color (starting from 0)
            var c = 0
            while (forbiddenColors.contains(c)) {
                c++
            }

            // Assign color and track maximum color index used
            colors[u] = c
            maxColorUsed = maxOf(maxColorUsed, c + 1)
        }

        return maxColorUsed
    }
}
