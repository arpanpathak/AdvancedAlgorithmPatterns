package array.dp


class TargetSum {
    private val dp = mutableMapOf<String, Int>()

    fun findTargetSumWays(nums: IntArray, target: Int): Int {
        return ways(nums, target, 0, 0)
    }

    private fun ways(nums: IntArray, target: Int, index: Int, sum: Int): Int {
        val state = "$index $sum"
        return when {
            index >= nums.size -> if (sum == target) 1 else 0
            dp.containsKey(state) -> dp[state]!!
            else -> {
                val count = ways(nums, target, index + 1, sum + nums[index]) +
                        ways(nums, target, index + 1, sum - nums[index])
                dp[state] = count
                count
            }
        }
    }


    /**
     * Finds the number of ways to assign a plus or minus sign to elements in the given array `nums`
     * such that their sum equals `target`.
     *
     * This problem is transformed into a subset sum problem using dynamic programming.
     * We calculate the total sum of `nums` and determine if it's possible to partition `nums`
     * into two subsets with sums `P` and `N` such that:
     *     P - N = target
     *
     * This can be simplified to finding the number of subsets of `nums` that sum up to
     * (target + sum(nums)) / 2. If this value is not an integer or is negative, return 0.
     *
     * @param nums The array of integers to assign plus or minus signs.
     * @param target The target sum to achieve.
     * @return The number of ways to achieve the target sum using plus or minus signs.
     */
    fun findTargetSumWays_subsetSum(nums: IntArray, target: Int): Int {
        val sumAll = nums.sum()
        // If the target sum is not reachable, return 0
        // If the sumAll + target is not non-negative or even, it's impossible to partition
        if ((target + sumAll) % 2 != 0 || sumAll < Math.abs(target)) return 0
        val newTarget = (target + sumAll) / 2

        // DP array to store the number of ways to reach each sum
        val dp = IntArray(newTarget + 1)
        dp[0] = 1

        for (num in nums) {
            for (j in newTarget downTo num) {
                dp[j] += dp[j - num]
            }
        }

        return dp[newTarget]
    }
}