package array.dp

class HouseRobber {
    fun rob(nums: IntArray): Int {
        val dp = IntArray(nums.size) { -1 }
        return rob(dp, nums, 0)
    }

    fun rob(dp: IntArray, nums: IntArray, i: Int): Int {
        return when {
            i >= nums.size -> 0
            dp[i] != -1 -> dp[i]
            else -> {
                dp[i] = maxOf(nums[i] + rob(dp, nums, i + 2), rob(dp, nums, i + 1))
                dp[i]
            }
        }
    }

    fun rob_iterative(nums: IntArray): Int {
        if (nums.isEmpty()) return 0

        var include = 0
        var exclude = 0

        for (num in nums) {
            val temp = include
            include = num + exclude
            exclude = maxOf(temp, exclude)
        }

        return maxOf(include, exclude)
    }
}