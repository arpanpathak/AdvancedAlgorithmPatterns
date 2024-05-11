package array.dp

import java.util.*

class LongestIncreasingSubsequence {
    fun lengthOfLIS(nums: IntArray): Int {
        if (nums.isEmpty()) return 0

        // Create a dp array with each element initialized to 1,
        // as the smallest LIS ending with any element is 1 (the element itself).
        val dp = IntArray(nums.size) { 1 }

        // Fill the dp array
        for (i in 1 until nums.size) {
            for (j in 0 until i) {
                if (nums[i] > nums[j]) {
                    dp[i] = maxOf(dp[i], dp[j] + 1)
                }
            }
        }

        // Return the maximum value from dp array which represents the length of the longest increasing subsequence
        return dp.maxOrNull() ?: 1 // Use ?: 1 to handle the case when nums is empty, although we already checked this.
    }
}