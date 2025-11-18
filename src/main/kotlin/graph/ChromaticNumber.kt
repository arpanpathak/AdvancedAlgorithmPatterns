package graph

class Graph<VertexType>(private val vertices: List<VertexType>) where VertexType : Any {
    data class Edge<VertexType>(val dest: VertexType, val weight: Double)

    // Immutable Graph...
    private val graph: Map<VertexType, MutableList<Edge<VertexType>>> = vertices
        .associateWith { mutableListOf<Edge<VertexType>>() }
        .toMap()

    fun addEdgeUndirected(src: VertexType, dst: VertexType, weight: Double) {
        graph[src]?.add(Edge(dst, weight))
        graph[dst]?.add(Edge(src, weight))
    }

    fun findChromaticNumber(): Int {
        if (vertices.isEmpty()) return 0

        val maxColors = vertices.size

        var colorAssignment = mutableMapOf<VertexType, Int>()

        fun isColorSafe(v: VertexType, color: Int): Boolean {
            return graph[v]?.none { edge ->
                colorAssignment[edge.dest] == color
            } ?: true;
        }

        // O(V^V) worst case time complexity....
        fun assignColorDfs(vIndex: Int, numColors: Int): Boolean {
            if (vIndex == vertices.size) return true

            val currV = vertices[vIndex]

            for (color in 1..numColors) {
                if (isColorSafe(currV, color)) {
                    colorAssignment[currV] = color

                    if (assignColorDfs(vIndex + 1, numColors)) return true
                    // Backtrack and try new color to expolore more possibilties...
                    colorAssignment.remove(currV)
                }
            }
            return false
        }

        for (numColors in 1..maxColors) {
            colorAssignment = mutableMapOf()

            if (assignColorDfs(0, numColors)) {
                println("Graph colored with $numColors colors: $colorAssignment")
                return numColors
            }
        }

        return maxColors
    }
}

// TODO: Use BFS