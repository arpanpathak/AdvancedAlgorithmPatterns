package graph.tsp

class TSPHeldKarpSolver(private val distanceMatrix: Array<IntArray>, private val cityCount: Int) {
    private val INF = 1_000_000_000

    fun solve(startCity: Int = 0): TSPResult {
        if (cityCount < 2 || startCity !in 0 until cityCount) {
            return TSPResult(0, listOf(startCity))
        }

        val totalCityCombinations = 1 shl cityCount

        // cost[visitedCities][currentCity] = minimum cost to reach currentCity having visited these specific cities
        val cost = Array(totalCityCombinations) { IntArray(cityCount) { INF } }

        // cameFrom[visitedCities][currentCity] = which city we were at before coming to currentCity
        val cameFrom = Array(totalCityCombinations) { IntArray(cityCount) { -1 } }

        // Start at startCity with only startCity visited
        cost[1 shl startCity][startCity] = 0

        // Try all possible combinations of which cities we've visited
        for (visitedSoFar in 1 until totalCityCombinations) {
            // Skip if we haven't visited start city yet (invalid state)
            if (visitedSoFar and (1 shl startCity) == 0) continue

            for (currentCity in 0 until cityCount) {
                // Skip if current city isn't in the visited set
                if (visitedSoFar and (1 shl currentCity) == 0) continue

                // Figure out which cities we visited before getting here
                val visitedBeforeThisCity = visitedSoFar xor (1 shl currentCity)

                for (previousCity in 0 until cityCount) {
                    // Previous city must have been in the set we visited before
                    if (visitedBeforeThisCity and (1 shl previousCity) == 0) continue

                    // Calculate total cost to get here via previous city
                    val totalCost = cost[visitedBeforeThisCity][previousCity] +
                            distanceMatrix[previousCity][currentCity]

                    // Update if this is a cheaper path
                    if (totalCost < cost[visitedSoFar][currentCity]) {
                        cost[visitedSoFar][currentCity] = totalCost
                        cameFrom[visitedSoFar][currentCity] = previousCity
                    }
                }
            }
        }

        // Find the complete tour that visits ALL cities and returns to start
        val allCitiesVisited = totalCityCombinations - 1
        var minTourCost = INF
        var lastCityBeforeReturn = -1

        // Try ending at each city and calculate cost to return to start
        for (finalStop in 0 until cityCount) {
            if (finalStop == startCity) continue

            val completeTourCost = cost[allCitiesVisited][finalStop] + distanceMatrix[finalStop][startCity]
            if (completeTourCost < minTourCost) {
                minTourCost = completeTourCost
                lastCityBeforeReturn = finalStop
            }
        }

        // Reconstruct the actual path
        val path = mutableListOf<Int>()
        var citiesVisited = allCitiesVisited
        var currentStop = lastCityBeforeReturn

        // Work backwards to build the path
        while (currentStop != -1) {
            path.add(0, currentStop)
            val previousStop = cameFrom[citiesVisited][currentStop]
            citiesVisited = citiesVisited xor (1 shl currentStop)
            currentStop = previousStop
        }

        return TSPResult(minTourCost, listOf(startCity) + path + startCity)
    }

    fun buildShortestPath(cameFrom: Array<IntArray>): List<String> {
       return buildList {
           
       }
    }
}


fun main() {
    val cityCount = 4
    val startCity = 1

    val distances = arrayOf(
        intArrayOf(0, 10, 15, 20),
        intArrayOf(10, 0, 35, 25),
        intArrayOf(15, 35, 0, 30),
        intArrayOf(20, 25, 30, 0)
    )

    val solver = TSPHeldKarpSolver(distances, cityCount)
    val result = solver.solve(startCity)

    println("--- TSP Solution ---")
    println("Cities: $cityCount")
    println("Start City: $startCity")
    println("Minimum Cost: ${result.minCost}")
    println("Best Path: ${result.bestPath.joinToString(" -> ")}")
}