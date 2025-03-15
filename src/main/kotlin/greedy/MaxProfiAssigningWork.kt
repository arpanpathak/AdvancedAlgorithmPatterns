package greedy

class MaxProfiAssigningWork {
    data class Task(val difficulty: Int, val profit: Int)

    fun maxProfitAssignment(difficulty: IntArray, profit: IntArray, worker: IntArray): Int {
        // Create a list of tasks, each task being a pair of difficulty and profit
        val tasks = difficulty.zip(profit).map { Task(it.first, it.second) }.sortedBy { it.difficulty }

        // Sort workers by their ability
        worker.sort()

        var (taskIndex, maxProfit, currentMaxProfit ) = listOf(0, 0, 0)

        // Iterate over each worker and assign the most profitable task they can handle
        for (ability in worker) {
            // While there are tasks that the worker can do (difficulty <= ability)
            while (taskIndex < tasks.size && tasks[taskIndex].difficulty <= ability) {
                currentMaxProfit = maxOf(currentMaxProfit, tasks[taskIndex].profit)
                taskIndex++
            }
            maxProfit += currentMaxProfit
        }

        return maxProfit
    }
}
