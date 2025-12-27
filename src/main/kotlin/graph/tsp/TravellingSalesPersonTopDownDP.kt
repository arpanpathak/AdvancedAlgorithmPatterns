package graph.tsp

class TravellingSalesmanTopDownDP {
    private data class State(val visitedCitiesBitmask: Int, val currentCity: Int)

    private fun Int.isNotVisited(city: Int) = (this and (1 shl city)) == 0
    private fun Int.visitCity(city: Int) = this or (1 shl city)

    fun solveTSP(dist: Array<IntArray>): Int {
        val totalCities = dist.size
        // Represents the bitmask where all bits are set to 1, signaling all cities have been visited
        val allCitiesVisitedMask = (1 shl totalCities) - 1 // in allVisited
        val cache = mutableMapOf<State, Int>()

        fun tspDfs(visitedCitiesBitmask: Int, currentCity: Int): Int =
            cache.getOrPut(State(visitedCitiesBitmask, currentCity)) {
                when (visitedCitiesBitmask) {
                    // When all the cities are visited, return cost to return hometown
                    allCitiesVisitedMask -> dist[currentCity][0]

                    // Find the unvisited city that minimizes the total tour cost
                    else -> (0 until totalCities)
                        .filter { nextCity -> visitedCitiesBitmask.isNotVisited(nextCity) }
                        .minOfOrNull { nextCity ->
                            dist[currentCity][nextCity] + tspDfs(visitedCitiesBitmask.visitCity(nextCity), nextCity)
                        } ?: Int.MAX_VALUE
                }
            }

        return tspDfs(0.visitCity(0), 0)
    }
}