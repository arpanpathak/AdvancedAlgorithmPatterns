package graph.dp

class MaximumVacationDays {
    fun maxVacationDays(flights: Array<IntArray>, days: Array<IntArray>): Int {
        val numCities = flights.size
        val numWeeks = days[0].size

        data class State(val city: Int, val week: Int)
        val cache = mutableMapOf<State, Int>()

        fun solve(city: Int, week: Int): Int = cache.getOrPut(State(city, week)) {
            when (week) {
                numWeeks -> 0
                else -> (0 until numCities)
                    .filter { next -> city == next || flights[city][next] == 1 }
                    .maxOf { next -> days[next][week] + solve(next, week + 1) }
            }
        }

        // We only care about paths starting from city 0
        return solve(0, 0)
    }
}