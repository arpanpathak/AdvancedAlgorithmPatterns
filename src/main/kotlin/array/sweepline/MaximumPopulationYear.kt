package array.sweepline

class MaximumPopulationYear {
    fun maximumPopulation(logs: Array<IntArray>): Int {
        val deltas = mutableMapOf<Int, Int>()

        // Can use tree map too. But range of year is small compared to sorting numbers
        var (minYear, maxYear) = Int.MAX_VALUE to Int.MIN_VALUE

        for ((birth, death) in logs) {
            deltas[birth] = (deltas[birth] ?: 0) + 1
            deltas[death] = (deltas[death] ?: 0) - 1

            minYear = minOf(minYear, birth)
            maxYear = maxOf(maxYear, death)
        }

        var maxP = 0
        var ansYear = 0
        var count = 0

        for (year in minYear until maxYear) {
            count += deltas.getOrDefault(year, 0)

            if (count > maxP) {
                maxP = count
                ansYear = year
            }
        }

        return if (ansYear == 0 && logs.isNotEmpty()) minYear else ansYear
    }
}
