package greedy

import java.util.*

fun minRefuelStops(target: Int, startFuel: Int, stations: Array<IntArray>): Int {
    // Step 1: Max-Heap stores the "best decisions we could have made"
    val maxHeap = PriorityQueue<Int>(compareByDescending { it })

    var currentFuel = startFuel
    var stops = 0
    var i = 0
    val n = stations.size

    // Step 2: Continuous simulation
    while (currentFuel < target) {
        // Add all stations reachable with the fuel we've already "committed"
        while (i < n && stations[i][0] <= currentFuel) {
            maxHeap.offer(stations[i][1])
            i++
        }

        // If we run out of fuel and have no more "backtrack" options
        if (maxHeap.isEmpty()) return -1

        // "Time Travel": Refuel at the best station we passed but didn't stop at
        currentFuel += maxHeap.poll()
        stops++
    }

    return stops
}
