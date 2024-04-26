package array.dp

class MaximumSumSubArray {
    // Kaden's Algorithm Dynamic Programming
    fun maxSubArray(nums: IntArray): Int {
        if (nums.isEmpty())
            return 0
        var (maxSum, currentSum) = listOf( nums[0], nums[0] )


        for (i in 1 until nums.size) {
            currentSum = maxOf(nums[i], currentSum + nums[i])
            maxSum = maxOf(maxSum, currentSum)
        }

        return maxSum
    }
}