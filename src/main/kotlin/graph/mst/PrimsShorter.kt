package graph.mst

import java.util.PriorityQueue

data class Edge(val dest: Int, val weight: Int)

data class MSTEdge(val source: Int, val dest: Int, val weight: Int)

data class SpanningTree(
    val mstEdges: List<MSTEdge>,
    val totalWeight: Int
)

class PrimsGraph {
    private val graph = mutableMapOf<Int, MutableList<Edge>>()

    fun addEdge(source: Int, dest: Int, weight: Int) {
        graph.computeIfAbsent(source) { mutableListOf() }.add(Edge(dest, weight))
        graph.computeIfAbsent(dest) { mutableListOf() }.add(Edge(source, weight))
    }

    fun primsAlgorithm(startVertex: Int): SpanningTree {
        val minHeap = PriorityQueue<Edge>(compareBy { it.weight })
        val inMST = mutableSetOf<Int>()
        val minWeight = mutableMapOf<Int, Int>().withDefault { Int.MAX_VALUE }
        val parent = mutableMapOf<Int, Int>()

        val mstEdges = arrayListOf<MSTEdge>()
        var totalWeight = 0

        minHeap.add(Edge(startVertex, 0))
        minWeight[startVertex] = 0
        parent[startVertex] = -1

        while (minHeap.isNotEmpty()) {
            val (u, w) = minHeap.poll()

            // Skip if already processed or a lighter path exists
            if (u in inMST || w > minWeight[u]!!) continue

            inMST.add(u)
            if (u != startVertex) {
                mstEdges.add(MSTEdge(source = parent[u]!!, dest = u, weight = w))
                totalWeight += w
            }

            graph[u]?.forEach { (v, edgeWeight) ->
                if (v !in inMST && edgeWeight < minWeight[v]!!) {
                    minWeight[v] = edgeWeight
                    parent[v] = u
                    minHeap.add(Edge(v, edgeWeight))
                }
            }
        }

        require(inMST.size == graph.size) {
            "The graph is not connected. Spanning tree size: ${inMST.size}, Graph size: ${graph.size}."
        }

        return SpanningTree(mstEdges, totalWeight)
    }
}

fun main() {
    val graph = PrimsGraph()
    graph.addEdge(0, 1, 10)
    graph.addEdge(0, 2, 6)
    graph.addEdge(0, 3, 5)
    graph.addEdge(1, 3, 15)
    graph.addEdge(2, 3, 4)

    val result = graph.primsAlgorithm(0)

    println("Total weight of the minimum spanning tree: ${result.totalWeight}")
    println("MST Edges:")
    result.mstEdges.forEach { edge ->
        println("  ${edge.source} -- ${edge.dest} (${edge.weight})")
    }
}