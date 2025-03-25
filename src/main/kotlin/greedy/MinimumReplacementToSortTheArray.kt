package greedy

import kotlin.math.ceil

class MinimumReplacementToSortTheArray {
    fun minimumReplacement(nums: IntArray): Long {
        var answer = 0L
        val n = nums.size

        // Start from the second last element, as the last one is always sorted.
        for (i in n - 2 downTo 0) {
            // No need to break if they are already in order.
            if (nums[i] <= nums[i + 1]) {
                continue
            }

            // Count how many elements are made from breaking nums[i].
            val numElements = (nums[i] + nums[i + 1] - 1) / nums[i + 1].toLong()

            // It requires numElements - 1 replacement operations.
            answer += numElements - 1

            // Maximize nums[i] after replacement.
            nums[i] = (nums[i] / numElements).toInt()
        }

        return answer
    }
}