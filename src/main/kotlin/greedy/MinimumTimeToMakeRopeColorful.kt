package greedy

class MinimumTimeToMakeRopeColorful {
    fun minCost(colors: String, neededTime: IntArray): Int {
        var minTime = 0
        for (i in 1 until neededTime.size) {
            if (colors[i] == colors[i - 1]) {
                minTime += minOf(neededTime[i], neededTime[i - 1])
                neededTime[i] = maxOf(neededTime[i], neededTime[i - 1])
            }
        }

        return minTime
    }
}
