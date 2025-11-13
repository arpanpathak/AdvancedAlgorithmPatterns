package array.dp

class KadensAlgorithm {
    // Kaden's Algorithm with a DP array of size 2
    fun maxSubArray(nums: IntArray): Int {
        if (nums.isEmpty())
            return 0

        // dp[i]=max(nums[i],dp[i−1]+nums[i])
        // dp[0]: max sum ending at current index
        // dp[1]: overall max sum found so far
        val dp = IntArray(2)
        dp[0] = nums[0]
        dp[1] = nums[0]

        for (i in 1 until nums.size) {
            // The maximum sum ending at the current index is either
            // the current number itself, or the current number plus
            // the max sum ending at the previous index (dp[0]).
            dp[0] = maxOf(nums[i], dp[0] + nums[i])

            // The overall maximum is the maximum of the overall max so far
            // and the new max sum ending at the current index.
            dp[1] = maxOf(dp[1], dp[0])
        }

        return dp[1]
    }
}
