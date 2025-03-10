package graph

import java.util.*

import java.util.*

class BusRoutes {
    data class Node(val stop: Int, val busCount: Int)

    fun numBusesToDestination(routes: Array<IntArray>, source: Int, target: Int): Int {
        if (source == target) return 0

        val graph = mutableMapOf<Int, MutableList<Int>>() // Stop -> List of buses passing through

        // Build the graph
        for (bus in routes.indices) {
            for (stop in routes[bus]) {
                graph.getOrPut(stop) { mutableListOf() }.add(bus)
            }
        }

        val queue: Queue<Node> = LinkedList()
        val visitedBuses = mutableSetOf<Int>()
        val visitedStops = mutableSetOf<Int>()

        queue.offer(Node(source, 0))
        visitedStops.add(source)

        while (queue.isNotEmpty()) {
            val (currentStop, busCount) = queue.poll()

            if (currentStop == target) return busCount

            // Explore all buses passing through the current stop
            graph[currentStop]?.let { buses ->
                for (bus in buses) {
                    if (bus !in visitedBuses) {
                        visitedBuses.add(bus)

                        for (stop in routes[bus]) {
                            if (stop !in visitedStops) {
                                queue.offer(Node(stop, busCount + 1))
                                visitedStops.add(stop)
                            }
                        }
                    }
                }
            }
        }
        return -1
    }
}
