package graph.tsp

class TravellingSalesmanRecursiveDP {
    fun solveTSP(dist: Array<IntArray>): Int {
        val n = dist.size
        val allVisited = (1 shl n) - 1
        val memo = mutableMapOf<Pair<Int, Int>, Int>()

        fun dp(mask: Int, u: Int): Int {
            // Base case: All cities visited, return cost to home (city 0)
            if (mask == allVisited) return dist[u][0]

            val state = mask to u
            if (memo.containsKey(state)) return memo[state]!!

            val minCost = (0 until n)
                .filter { v -> (mask and (1 shl v)) == 0 } // Take all the unvisited cities...
                .minOfOrNull { v -> dist[u][v] + dp(mask or (1 shl v), v) }
                ?: Int.MAX_VALUE


            return minCost.also { memo[state] = it } // Cache it
        }

        // Start at home (index 0) with home already visited (mask 1)
        return dp(1, 0)
    }
}

// Another Approach where you need to print the Path as well.
class TSPSolver(private val dist: Array<IntArray>) {
    private val n = dist.size
    private val allVisited = (1 shl n) - 1
    private val memo = mutableMapOf<Pair<Int, Int>, Int>()
    private val nextCity = mutableMapOf<Pair<Int, Int>, Int>()

    fun solve() {
        val minFuel = dp(1, 0)
        val path = reconstructPath()

        println("Minimum Fuel/Time: $minFuel")
        println("Optimal Route: ${path.joinToString(" -> ")}")
    }

    private fun dp(mask: Int, u: Int): Int {
        if (mask == allVisited) return dist[u][0]

        val state = mask to u
        if (memo.containsKey(state)) return memo[state]!!

        var minCost = Int.MAX_VALUE
        var bestNext = -1

        // Iterate through cities to find the optimal next step
        for (v in 0 until n) {
            if ((mask and (1 shl v)) == 0) {
                val cost = dist[u][v] + dp(mask or (1 shl v), v)
                if (cost < minCost) {
                    minCost = cost
                    bestNext = v
                }
            }
        }

        nextCity[state] = bestNext
        memo[state] = minCost
        return minCost
    }

    private fun reconstructPath(): List<Int> {
        val path = mutableListOf<Int>()
        var currMask = 1
        var currU = 0

        while (currU != -1) {
            path.add(currU)
            // Retrieve the best next city from our DP transitions
            val v = nextCity[currMask to currU] ?: -1
            if (v != -1) currMask = currMask or (1 shl v)
            currU = v
        }

        path.add(0) // Return to home address
        return path
    }
}