package sliding_window

class MaximumAverageSubarray_I {
    fun findMaxAverage(nums: IntArray, k: Int): Double {
        var windowSum = 0.0
        var maxAverage = Double.NEGATIVE_INFINITY

        for (i in 0 until nums.size) {
            windowSum += nums[i]

            if (i >= k - 1) {
                maxAverage = maxOf(maxAverage, windowSum / k)
                windowSum -= nums[i - k + 1]
            }
        }

        return maxAverage
    }
}