package sliding_window

class MinimumSizeSubarraySum {
    fun minSubArrayLen(target: Int, nums: IntArray): Int {
        var (windowStart, windowSum) = 0 to 0
        var minLength = Int.MAX_VALUE

        for(i in nums.indices) {
            windowSum += nums[i]

            // Shrink window
            while (windowSum >= target) {
                minLength = minOf(minLength,  (i - windowStart + 1))
                windowSum -= nums[windowStart++]

            }
        }

        return if (minLength == Int.MAX_VALUE) 0 else minLength
    }
}