package sliding_window

class LongestContinuousSubarrayWithAbsoluteDifferenceLessThanOrEqualToLimit {
    fun longestSubarray(nums: IntArray, limit: Int): Int {
        val maxQueue = ArrayDeque<Int>()
        val minQueue = ArrayDeque<Int>()

        var left = 0
        var result = 0

        for (right in nums.indices) {
            // Maintain maxQueue in decreasing order
            while (maxQueue.isNotEmpty() && nums[right] > maxQueue.last()) {
                maxQueue.removeLast()
            }
            maxQueue.addLast(nums[right])

            // Maintain minQueue in increasing order
            while (minQueue.isNotEmpty() && nums[right] < minQueue.last()) {
                minQueue.removeLast()
            }
            minQueue.addLast(nums[right])

            // Shrink window if diff between max and min exceeds limit
            while (maxQueue.first() - minQueue.first() > limit) {
                if (maxQueue.first() == nums[left]) maxQueue.removeFirst()
                if (minQueue.first() == nums[left]) minQueue.removeFirst()
                left++
            }

            result = maxOf(result, right - left + 1)
        }

        return result
    }
}
