package graph.tsp

const val N = 4

val DISTANCE_MATRIX = arrayOf(
    intArrayOf(0, 10, 15, 20),
    intArrayOf(10, 0, 35, 25),
    intArrayOf(15, 35, 0, 30),
    intArrayOf(20, 25, 30, 0)
)

data class TSPResult(val minCost: Int, val bestPath: List<Int>)

fun solveTSPBruteForce(): TSPResult {
    val startCity = 0
    var minCost = Int.MAX_VALUE
    var bestPath: List<Int> = emptyList()

    val currentPath = mutableListOf(startCity)
    val unvisitedCities = (1 until N).toMutableSet()

    fun permute(currentCost: Int) {
        if (unvisitedCities.isEmpty()) {
            val lastCity = currentPath.last()
            val costToReturn = DISTANCE_MATRIX[lastCity][startCity]
            val totalCost = currentCost + costToReturn

            if (totalCost < minCost) {
                minCost = totalCost
                bestPath = currentPath + startCity
            }
            return
        }

        val unvisitedList = unvisitedCities.toList()
        for (nextCity in unvisitedList) {
            val lastCity = currentPath.last()
            val costToNext = DISTANCE_MATRIX[lastCity][nextCity]
            val newCost = currentCost + costToNext

            if (newCost >= minCost) continue

            unvisitedCities.remove(nextCity)
            currentPath.add(nextCity)

            permute(newCost)

            currentPath.removeAt(currentPath.lastIndex)
            unvisitedCities.add(nextCity)
        }
    }

    permute(0)

    return TSPResult(minCost, bestPath)
}

fun main() {
    val result = solveTSPBruteForce()
    println("--- Brute-Force TSP Solution ---")
    println("Cities: $N")
    println("Minimum Cost: ${result.minCost}")
    println("Best Path: ${result.bestPath.joinToString(" -> ")}")
}