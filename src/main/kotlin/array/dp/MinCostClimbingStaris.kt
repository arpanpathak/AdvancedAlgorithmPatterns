package array.dp

class MinCostClimbingStaris {
    fun minCostClimbingStairs_iterative(cost: IntArray): Int {
        // Handle base cases
        if (cost.size <= 2) return cost.min()

        // Initialize the dp array with the size of the cost array
        val dp = IntArray(cost.size)

        // Base cases
        dp[0] = cost[0]
        dp[1] = cost[1]

        // Fill the dp array
        for (i in 2 until cost.size) {
            dp[i] = cost[i] + minOf(dp[i - 1], dp[i - 2])
        }

        // The result is the minimum cost to reach the last or the second-to-last step
        return minOf(dp[cost.size - 1], dp[cost.size - 2])
    }

    /**
     * Constant space iterative approach
     */
    fun minCostClimbingStairs_iterative_constant_space(cost: IntArray): Int {
        // Handle base cases
        if (cost.size <= 2) return cost.min()

        // Initialize the dp array with the size of the cost array
        val dp = IntArray(cost.size)

        // Base cases
        var prev2 = cost[0] // Previous 2 steps
        var prev1 = cost[1]  // Previous 1 steps

        // Fill the dp array
        for (i in 2 until cost.size) {
            val cur = cost[i] + minOf(prev2, prev1)

            prev2 = prev1
            prev1 = cur
        }

        // The result is the minimum cost to reach the last or the second-to-last step
        return minOf(prev2, prev1)
    }


    /**
     * Top Down Dynamic Programming Approach
     */
    fun minCostClimbingStairs(cost: IntArray): Int {
        val memo = IntArray(cost.size) { -1 }
        val n = cost.size

        // We can start from step 0 or step 1, hence we take the minimum of both
        return minOf(minCost(cost, n - 1, memo), minCost(cost, n - 2, memo))
    }
    private fun minCost(cost: IntArray, i: Int, memo: IntArray): Int {
        // Base cases
        if (i < 0) return 0
        if (i == 0 || i == 1) return cost[i]

        // If already calculated, return the stored result
        if (memo[i] != -1) return memo[i]

        // Recursive calculation with memoization
        memo[i] = cost[i] + minOf(minCost(cost, i - 1, memo), minCost(cost, i - 2, memo))
        return memo[i]
    }
    /** End of top down Dynamic Programming approach **/
}