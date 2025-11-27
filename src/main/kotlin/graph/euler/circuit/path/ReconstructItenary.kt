package graph.euler.circuit.path

import java.util.PriorityQueue
import java.util.LinkedList

class ReconstructItenary {
    fun findItinerary(tickets: List<List<String>>): List<String> {
        val adj = mutableMapOf<String, PriorityQueue<String>>()
        val itinerary = LinkedList<String>()

        // Build the graph using functional style
        tickets.forEach { (source, destination) ->
            adj.getOrPut(source) { PriorityQueue<String>() }.add(destination)
        }

        // Recursive Hierholzer's Algorithm (DFS for Eulerian Path)
        fun dfs(airport: String) {
            val destinations = adj[airport]

            while (destinations != null && destinations.isNotEmpty()) {
                // Poll the lexicographically smallest destination.
                val nextAirport = destinations.poll()
                dfs(nextAirport)
            }

            // Add the current airport to the front of the list in post-order.
            itinerary.addFirst(airport)
        }

        dfs("JFK")
        return itinerary
    }
}
