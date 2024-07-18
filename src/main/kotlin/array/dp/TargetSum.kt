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
}