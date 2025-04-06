package greedy

class TaskScheduler {
    fun leastInterval(tasks: CharArray, n: Int): Int {
        val freq = tasks.toList().groupingBy { it }.eachCount().values
        val maxFreq = freq.maxOrNull() ?: 0
        val maxCount = freq.count { it == maxFreq }

        return maxOf(tasks.size, (maxFreq - 1) * (n + 1) + maxCount)
    }
}