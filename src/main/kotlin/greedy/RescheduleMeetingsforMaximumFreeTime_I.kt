package greedy

class RescheduleMeetingsforMaximumFreeTime_I {
    fun maxFreeTime(eventTime: Int, k: Int, startTime: IntArray, endTime: IntArray): Int {
        val n = startTime.size
        val gaps = mutableListOf<Int>()
        gaps.add(startTime[0])
        for (i in 1 until n)
            gaps.add(startTime[i] - endTime[i - 1])

        gaps.add(eventTime - endTime[n - 1])
        var windowSum = 0

        for (i in 0..k)
            windowSum += gaps[i]

        var maxFree = windowSum

        for (i in k + 1 until gaps.size) {
            windowSum += gaps[i] - gaps[i - k - 1]
            maxFree = maxOf(maxFree, windowSum)
        }

        return maxFree
    }
}
