package sliding_window

class MaximumSumOfDistinctSubarraysWithLengthK {
    fun maximumSubarraySum(nums: IntArray, k: Int): Long {
        if (nums.size < k) {
            return 0
        }

        val window = mutableSetOf<Int>()
        var (windowSum, maxSum) = 0L to 0L
        var windowStart = 0

        for (i in nums.indices) {

            // If the current element is already in the set or the set size has reached 'k',
            // then we slide the window to the right until the set can accommodate the current element

            while (window.contains(nums[i]) || window.size == k) {
                window.remove(nums[windowStart])
                windowSum -= nums[windowStart++]
            }

            windowSum += nums[i]
            window.add(nums[i])

            if (window.size == k) {
                maxSum = maxOf(maxSum, windowSum)
            }
        }

        return maxSum
    }
}