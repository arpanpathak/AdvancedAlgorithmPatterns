package graph.mst

import java.util.PriorityQueue

data class Node(val dest: Int, val weight: Int, val src: Int? = null)

class Graph {
    private val graph = mutableMapOf<Int, MutableList<Node>>()

    fun addEdge(source: Int, dest: Int, weight: Int) {
        graph.computeIfAbsent(source) { mutableListOf() }.add(Node(dest, weight))
        graph.computeIfAbsent(dest) { mutableListOf() }.add(Node(source, weight)) // For undirected graph
    }

    fun primsAlgorithm(startVertex: Int): Int {
        val minHeap = PriorityQueue<Node>(compareBy { it.weight })
        val inMST = mutableSetOf<Int>()
        var totalWeight = 0

        // Start by adding a pseudo-edge for the start vertex with zero weight
        minHeap.add(Node(startVertex, 0))

        while (minHeap.isNotEmpty()) {
            val current = minHeap.poll()

            // If current destination is already included in MST, skip it
            if (current.dest in inMST) continue

            // Add current vertex to MST
            inMST.add(current.dest)
            totalWeight += current.weight

            // Add all adjacent edges to the heap if their destinations are not yet in MST
            graph[current.dest]?.forEach {
                if (it.dest !in inMST) {
                    minHeap.add(it)
                }
            }
        }

        // Ensure all vertices are included in the MST to confirm the graph is connected
        if (inMST.size != graph.size) {
            println("The graph is not connected, and thus, a spanning tree cannot be formed.")
            return -1
        }

        return totalWeight
    }
}

fun main() {
    val graph = Graph()
    graph.addEdge(0, 1, 10)
    graph.addEdge(0, 2, 6)
    graph.addEdge(0, 3, 5)
    graph.addEdge(1, 3, 15)
    graph.addEdge(2, 3, 4)

    val totalWeight = graph.primsAlgorithm(0)
    if (totalWeight != -1) {
        println("Total weight of the minimum spanning tree: $totalWeight")
    }
}