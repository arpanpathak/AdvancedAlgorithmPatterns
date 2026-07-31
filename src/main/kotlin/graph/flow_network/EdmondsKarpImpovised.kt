package graph.flow_network

fun maxFlowEdmondsKarps(graph: Array<IntArray>, source: Int, sink: Int): Int {
    val n = graph.size
    // The residual graph tracks capacity available for flow, including reverse edges.
    val residual = Array(n) { index -> graph[index].copyOf() }
    val parent = IntArray(n)
    var maxFlow = 0

    // BFS finds an augmenting path (s to t) and simultaneously computes the min-cut capacity along that path.
    fun findAugmentingPath(): Int {
        parent.fill(-1)
        val pathCapacity = IntArray(n) { 0 } // Stores max capacity found to reach this node
        val queue = ArrayDeque<Int>().apply { add(source) }
        pathCapacity[source] = Int.MAX_VALUE 

        while (queue.isNotEmpty()) {
            val u = queue.removeFirst()

            for (v in 0 until n) {
                // Explore unvisited nodes with available forward capacity.
                if (parent[v] == -1 && residual[u][v] > 0) {
                    parent[v] = u
                    // Capacity to v is limited by the current bottleneck pathCapacity[u] or the edge capacity.
                    pathCapacity[v] = minOf(pathCapacity[u], residual[u][v])
                    
                    if (v == sink) {
                        return pathCapacity[sink] // Path found: return the bottleneck flow amount.
                    }
                    queue.add(v)
                }
            }
        }
        return 0 // No more augmenting paths exist. Max flow achieved.
    }

    // Updates residual capacities based on the flow sent along the path found by parent array.
    fun updateResidual(flowAmount: Int) {
        var v = sink
        while (v != source) {
            val u = parent[v]
            residual[u][v] -= flowAmount // Consume forward capacity.
            residual[v][u] += flowAmount // Increase reverse capacity for potential flow cancellation (backtracking).
            v = u
        }
    }

    // Main loop: Ford-Fulkerson method using BFS (Edmonds-Karp)
    while (true) {
        val flowAmount = findAugmentingPath()
        if (flowAmount == 0) break

        updateResidual(flowAmount)
        maxFlow += flowAmount
    }

    return maxFlow
}

/*
 * Need to dive deep and dry run to get this understanding click like a light emitting diode and bright future like sharp me.
 *
 */
