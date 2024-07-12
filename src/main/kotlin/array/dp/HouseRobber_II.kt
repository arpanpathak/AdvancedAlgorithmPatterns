package array.dp

class HouseRobber_II {
    fun rob(nums: IntArray): Int {
        return when {
            nums.size == 1 -> nums[0]
            else -> maxOf(rob(nums, 0 , nums.size ), rob(nums, 1, nums.size - 1))
        }
    }

    /**
     * Recusrive Solution
     */
    fun rob(nums: IntArray, start: Int, end: Int): Int {
        var (include, exclude, max) = Triple(0, 0, 0)

        for (i in start until  end) {
            max = maxOf(include + nums[i], exclude)

            include = exclude
            exclude = max
        }

        return max
    }


    /**
     * Bottom UP DP
     */
    fun robRange(nums: IntArray, start: Int, end: Int): Int {
        if (nums.isEmpty() || start > end) return 0
        if (start == end) return nums[start]

        // Initialize an array to store the maximum amount that can be robbed up to each house
        val dp = IntArray(nums.size)

        // Base cases
        dp[start] = nums[start]
        dp[start + 1] = maxOf(nums[start], nums[start + 1])

        // Fill the dp array from start + 2 to end
        for (i in start + 2..end) {
            dp[i] = maxOf(nums[i] + (if (i >= start + 2) dp[i - 2] else 0), dp[i - 1])
        }

        // The last element of dp array contains the maximum amount that can be robbed
        return dp[end]
    }
}