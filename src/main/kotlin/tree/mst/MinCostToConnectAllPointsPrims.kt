package tree.mst

import java.util.*
import kotlin.math.abs

// Better for Dense Graph
class MinCostToConnectAllPointsPrims {
    data class Node(val dest: Int, val weight: Int)
    fun minCostConnectPoints(points: Array<IntArray>): Int {
        fun dist(from: Int, to: Int) =
            abs(points[from][0] - points[to][0]) + abs(points[from][1] - points[to][1])

        val notVisited = points.indices.toMutableSet()
        val pq = PriorityQueue<Node>(compareBy{ it.weight })
        pq.add(Node(0, 0))
        var totalCost = 0
        while (notVisited.isNotEmpty()) {
            val ( dest, weight ) = pq.poll()
            if (!notVisited.remove(dest)) continue
            totalCost += weight
            for (neighbour in notVisited) pq.add(Node(neighbour , dist(dest, neighbour)))
        }
        return totalCost
    }
}
