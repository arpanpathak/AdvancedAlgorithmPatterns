package graph.dp

import java.util.*

class CheapestFlightsWithinKStops {
    // Define the Node class to encapsulate flight details
    data class Node(var dest: Int, var price: Int)

    // Graph map where each integer key maps to a list of Node objects
    private var graph = mutableMapOf<Int, MutableList<Node>>()

    // Solving using Bellman Ford
    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        // Build the graph from the input flights
        flights.forEach { flight ->
            val from = flight[0]
            val to = flight[1]
            val cost = flight[2]
            // Append the destination and cost to the list of nodes for the source
            graph.computeIfAbsent(from) { mutableListOf() }.add(Node(to, cost))
        }

        // DP table for storing minimum cost to each node with up to i flights
        val dp = Array(k + 2) { IntArray(n) { Int.MAX_VALUE } }
        dp[0][src] = 0  // Cost to reach source from itself is 0

        // Perform relaxation for each possible number of stops
        for (i in 1..k + 1) {
            dp[i][src] = 0  // No cost to stay at the source
            graph.forEach { (from, neighbors) ->
                neighbors.forEach { node ->
                    if (dp[i - 1][from] != Int.MAX_VALUE) { // Ensure the source node is reachable
                        dp[i][node.dest] = minOf(dp[i][node.dest], dp[i - 1][from] + node.price)
                    }
                }
            }
        }

        // Find the minimum cost to the destination node considering all possible stops
        val cheapest = (0..k + 1).minOfOrNull { dp[it][dst] } ?: Int.MAX_VALUE
        return if (cheapest == Int.MAX_VALUE) -1 else cheapest
    }

    fun findCheapestPriceBfs(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        // Build the graph from the input flights
        flights.forEach { flight ->
            val from = flight[0]
            val to = flight[1]
            val cost = flight[2]
            // Append the destination and cost to the list of nodes for the source
            graph.computeIfAbsent(from) { mutableListOf() }.add(Node(to, cost))
        }

        val cost = bfs(src, dst, n, k)

        return if (cost[dst] == Int.MAX_VALUE) -1 else cost[dst]
    }


    fun dijkstra(graph: MutableMap<Int, MutableList<Node>>, src: Int): Map<Int, Int> {
        val dist = mutableMapOf<Int, Int>().apply {
            graph.keys.forEach { this[it] = Int.MAX_VALUE }
        }
        dist[src] = 0

        // Define priority queue with a comparator provided inline
        val priorityQueue = PriorityQueue<Node>(compareBy { it.price })
        priorityQueue.add(Node(src, 0))

        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.poll()

            // If current node's distance in queue is greater than known shortest distance, skip it
            if (current.price > dist[current.dest]!!) continue

            graph[current.dest]?.forEach { neighbor ->
                val newDistance = current.price + neighbor.price
                if (newDistance < dist[neighbor.dest]!!) {
                    dist[neighbor.dest] = newDistance
                    priorityQueue.add(Node(neighbor.dest, newDistance))
                }
            }
        }

        return dist
    }

    fun bfs(src: Int, dest: Int, n: Int, k: Int ): IntArray {
        // To track the minimum cost to reach each destination
        val minCost = IntArray(n) { Int.MAX_VALUE }
        minCost[src] = 0

        // Using a queue for BFS; store the current node, the cost to reach it, and the number of stops made
        val queue: Queue<Triple<Int, Int, Int>> = LinkedList()
        queue.add(Triple(src, 0, 0))

        while (queue.isNotEmpty()) {
            val (current, cost, stops) = queue.poll()

            // Explore each neighbor
            if (stops > k) continue  // Do not explore further if stops exceed k

            graph[current]?.forEach { neighbor ->
                val newCost = cost + neighbor.price
                // Check if this path is better
                if (newCost < minCost[neighbor.dest]) {
                    queue.add(Triple(neighbor.dest, newCost, stops + 1))
                    minCost[neighbor.dest] = newCost
                }
            }
        }

        return minCost
    }

    // Solve using Dijkstra
    data class State(val node: Int, val cost: Int, val stops: Int)

    fun dijkstraWithMaxKStops(graph: MutableMap<Int, MutableList<Node>>, src: Int, k: Int): Map<Int, Int> {
        val pq = PriorityQueue<State>(compareBy { it.cost })
        pq.offer(State(src, 0, 0))

        val minCostAtStops = mutableMapOf<Int, IntArray>().apply {
            graph.keys.forEach { this[it] = IntArray(k + 1) { Int.MAX_VALUE } }
        }
        minCostAtStops[src] = IntArray(k + 1) { 0 }

        while (pq.isNotEmpty()) {
            val (node, cost, stops) = pq.poll()

            if (stops > k) continue

            graph[node]?.forEach { neighbor ->
                val nextCost = cost + neighbor.price
                if (stops < k && nextCost < minCostAtStops.getOrDefault(neighbor.dest, IntArray(k + 1) { Int.MAX_VALUE })[stops + 1]) {
                    minCostAtStops[neighbor.dest]?.set(stops + 1, nextCost)
                    pq.offer(State(neighbor.dest, nextCost, stops + 1))
                }
            }
        }

        return minCostAtStops.mapValues { (_, costs) -> costs.filter { it != Int.MAX_VALUE }.minOrNull() ?: Int.MAX_VALUE }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

        }
    }
}