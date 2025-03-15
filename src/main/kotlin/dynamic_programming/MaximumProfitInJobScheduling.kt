package dynamic_programming

class MaximumProfitInJobScheduling {
    data class Job(val start: Int, val end: Int, val profit: Int)

    fun jobScheduling(startTime: IntArray, endTime: IntArray, profit: IntArray): Int {
        // Create a list of jobs and sort them by end time
        val jobs = startTime.indices.map { Job(startTime[it], endTime[it], profit[it]) }
            .sortedBy { it.end }

        // Initialize dp array
        val dp = IntArray(jobs.size)
        dp[0] = jobs[0].profit

        for (i in 1 until jobs.size) {
            // Profit if the current job is not included
            dp[i] = dp[i - 1]

            // Find the last non-overlapping job
            val prevJobIndex = findLastNonOverlappingJob(jobs, i)

            // Profit if the current job is included
            val currentProfit = jobs[i].profit + if (prevJobIndex != -1) dp[prevJobIndex] else 0

            // Take the maximum profit between including and excluding the current job
            dp[i] = maxOf(dp[i], currentProfit)
        }

        return dp[dp.size - 1]
    }

    private fun findLastNonOverlappingJob(jobs: List<Job>, currentIndex: Int): Int {
        val currentJob = jobs[currentIndex]
        var low = 0
        var high = currentIndex - 1
        var best = -1

        while (low <= high) {
            val mid = (low + high) / 2
            if (jobs[mid].end <= currentJob.start) {
                best = mid
                low = mid + 1  // Search in the right half
            } else {
                high = mid - 1  // Search in the left half
            }
        }

        return best
    }
}
