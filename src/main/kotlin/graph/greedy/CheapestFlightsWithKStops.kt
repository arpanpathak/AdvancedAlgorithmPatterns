package graph.greedy

import java.util.*

class CheapestFlightsWithKStops {
    // Define the Node class to encapsulate flight details
    data class Node(val dest: Int, val cost: Int)

    // Define the State class for priority queue elements
    data class State(val node: Int, val cost: Int, val stops: Int)

    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        // Build the graph from the input flights
        val graph = mutableMapOf<Int, MutableList<Node>>()
        flights.forEach { flight ->
            val from = flight[0]
            val to = flight[1]
            val cost = flight[2]
            graph.getOrPut(from) { mutableListOf() }.add(Node(to, cost))
        }

        // E + EK log

        // Initialize the priority queue and cost tracking
        val minStops = Array(n) { Int.MAX_VALUE }
        val pq = PriorityQueue<State>(compareBy { it.cost })
        pq.offer(State(0, src, 0))
        minStops[src] = 0

        while (pq.isNotEmpty()) {
            val (node, currentCost, stops) = pq.poll()

            // Skip if the number of stops exceeds the limit or if the path is not optimal
            if (stops > k + 1 || currentCost > minStops[node]) continue

            minStops[node] = currentCost
            // Return the cost if the destination is reached
            if (node == dst) return currentCost

            // Explore neighbors
            graph[node]?.forEach { neighbor ->
                    pq.offer(State(neighbor.dest, currentCost + neighbor.cost, stops + 1))
            }
        }

        // If no path was found within the constraints
        return -1
    }
}

