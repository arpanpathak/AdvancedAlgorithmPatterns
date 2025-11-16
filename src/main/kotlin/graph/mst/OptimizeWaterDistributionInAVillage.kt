package graph.mst

import java.util.PriorityQueue

class OptimizeWaterDistributionInAVillage {
    private data class Edge(val node: Int, val cost: Int)

    fun minCostToSupplyWater(n: Int, wells: IntArray, pipes: Array<IntArray>): Int {
        val graph = List(n + 1) { mutableListOf<Edge>() }
        val visited = BooleanArray(n + 1)
        val pq = PriorityQueue<Edge>(compareBy { it.cost })

        // Add wells as edges from virtual node 0
        wells.forEachIndexed { i, cost ->
            pq.add(Edge(i + 1, cost))
        }

        // Add pipes
        pipes.forEach { (from, to, cost) ->
            graph[from].add(Edge(to, cost))
            graph[to].add(Edge(from, cost))
        }

        var totalCost = 0
        var nodesConnected = 0

        while (pq.isNotEmpty() && nodesConnected <= n) {
            val (node, cost) = pq.poll()
            if (visited[node]) continue

            visited[node] = true
            totalCost += cost
            nodesConnected++

            graph[node].forEach { edge ->
                if (!visited[edge.node]) {
                    pq.add(edge)
                }
            }
        }

        return totalCost
    }
}
