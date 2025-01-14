package sliding_window

class MaximumErasureValue {
    fun maximumUniqueSubarray(nums: IntArray): Int {
        val set = mutableSetOf<Int>()
        var ( windowSum, windowStart, maxSum) = listOf( 0, 0, 0)

        nums.forEach { num ->
            while (set.contains(num)) {
                windowSum -= nums[windowStart]
                set.remove(nums[windowStart++])
            }
            windowSum += num
            maxSum = maxOf(maxSum, windowSum)
            set.add(num)
        }

        return maxSum
    }
}
