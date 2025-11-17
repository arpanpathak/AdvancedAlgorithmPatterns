package array.dp

class MaximumSumOfNonAdjacentElements {
    fun maximumSumSubsequence(nums: IntArray, queries: Array<IntArray>): Int {
        when (nums.size) {
            0 -> return 0
            1 -> return nums[0]
        }

        val dp = IntArray(2).apply { this[0] = nums[0] }

        for (i in 1 until nums.size) {
            val newMax = maxOf(
                dp[1],
                dp[0] + nums[i]
            )

            dp[0] = dp[1].also { dp[1] = newMax }
        }

        return dp[1]
    }
}
