package dynamic_programming

class PartitionEqualSubsetSum {
    fun canPartition(nums: IntArray): Boolean {
        val sum = nums.sum()
        if (sum % 2 != 0)   return false
        val target = sum / 2
        val dp = Array(nums.size) { IntArray(target + 1) { -1 } }


        fun dfs(i: Int, currentSum: Int): Boolean = when {
            currentSum == target -> true
            dp[i][currentSum] != -1 -> dp[i][currentSum] == 1
            else -> (
                dfs(i + 1, currentSum + nums[i]) ||
                dfs(i + 1, currentSum)
            ).also { dp[i][currentSum] = if (it) 1 else 0 }
        }

        return dfs(0, 0)
    }

    fun canPartitionBottomUp(nums: IntArray): Boolean {
        val sum = nums.sum()
        if (sum % 2 != 0)   return false

        val target = sum / 2
        val dp = BooleanArray(target + 1).apply { this[0] = true }
        for (num in nums) {
            for (i in target downTo num)
                dp[i] = dp[i] || dp[i - num]
        }

        return dp[target]
    }
}