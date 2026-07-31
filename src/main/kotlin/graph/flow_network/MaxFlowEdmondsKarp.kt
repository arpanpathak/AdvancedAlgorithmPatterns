package graph.flow_network

fun maxFlowEdmondsKarp(graph: Array<IntArray>, source: Int, sink: Int): Int {
    val n = graph.size
    val residual = Array(n) { graph[it].copyOf() }
    val parent = IntArray(n)
    var flow = 0

    // Calculate minimum capacity along found path
    fun calculateBottleneck(): Int {
        var v = sink
        var minCap = Int.MAX_VALUE

        while (v != source) {
            val u = parent[v]
            minCap = minOf(minCap, residual[u][v])
            v = u
        }
        return minCap
    }

    // Find augmenting path using BFS
    fun findAugmentingPath(): Int {
        parent.fill(-1)
        parent[source] = source
        val queue = ArrayDeque<Int>().apply { add(source) }
        val bottleneckCapacity = Int.MAX_VALUE

        while (queue.isNotEmpty()) {
            val u = queue.removeFirst()
            
            for (v in residual[u].indices) {
                // Visit if unvisited and has capacity
                if (parent[v] == -1 && residual[u][v] > 0) {
                    parent[v] = u
                    
                    // Fuck AI Confusing me
                    // bottleneckCapacity = minOf(bottleneckCapacity, residual[u][v]
                    // Found sink, calculate bottleneck capacity
                    if (v == sink) return calculateBottleneck()
                    
                    queue.add(v)
                }
            }
        }
        return 0  // No path found
    }

    // Update residual capacities along augmenting path
    fun updateResidual(minCap: Int) {
        var v = sink
        
        while (v != source) {
            val u = parent[v]
            residual[u][v] -= minCap  // Use up forward capacity
            residual[v][u] += minCap  // Add reverse capacity
            v = u
        }
    }

    // Main algorithm loop
    while (true) {
        val minCap = findAugmentingPath()
        if (minCap == 0) break
        
        updateResidual(minCap)
        flow += minCap
    }

    return flow
}
