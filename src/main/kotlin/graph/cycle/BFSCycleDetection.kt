package graph.cycle

// Directed Graph - Kahn's Algorithm
fun hasCycleDirected(graph: List<List<Int>>): Boolean {
    val indegree = IntArray(graph.size).apply {
        graph.forEach { neighbors -> neighbors.forEach { this[it]++ } }
    }

    val queue = ArrayDeque(graph.indices.filter { indegree[it] == 0 })

    var processed = 0
    while (queue.isNotEmpty()) {
        processed++
        graph[queue.removeFirst()].forEach { if (--indegree[it] == 0) queue.add(it) }
    }

    return processed != graph.size  // Cycle exists if not all nodes processed
}
