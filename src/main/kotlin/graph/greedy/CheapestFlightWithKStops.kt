package graph.greedy

import java.util.*

class CheapestFlightWithKStops {
    data class Flight(val node: Int, val cost: Int, val stops: Int)

    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        val graph = flights.groupBy({ it[0] }) { Flight(it[1], it[2], 0) }
        val pq = PriorityQueue<Flight>(compareBy { it.cost })
        val minStops = IntArray(n) { Int.MAX_VALUE }

        pq.offer(Flight(src, 0, 0))

        while (pq.isNotEmpty()) {
            val (node, currentCost, stops) = pq.poll()

            if (stops > k + 1 || stops >= minStops[node]) continue
            minStops[node] = stops

            if (node == dst) return currentCost

            graph[node]?.forEach { (next, cost, _) ->
                pq.offer(Flight(next, currentCost + cost, stops + 1))
            }
        }
        return -1
    }
}
