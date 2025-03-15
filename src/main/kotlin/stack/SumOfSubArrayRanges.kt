package stack

import kotlin.collections.ArrayDeque

class SumOfSubArrayRanges {
    fun subArrayRanges(nums: IntArray): Long {
        val n = nums.size
        var answer: Long = 0

        // Helper function to calculate the sum of all minimums or maximums
        fun calculateSum(comparator: (Int, Int) -> Boolean): Long {
            val stack = ArrayDeque<Int>()
            var sum: Long = 0
            for (right in 0..n) {
                while (!stack.isEmpty() &&
                    (right == n || comparator(nums[stack.last()], nums[right]))
                ) {
                    val mid: Int = stack.removeLast()
                    val left = if (stack.isEmpty()) -1 else stack.last()
                    sum += nums[mid].toLong() * (right - mid) * (mid - left)
                }
                stack.add(right)
            }
            return sum
        }

        // Find the sum of all the minimums and subtract from the answer
        answer -= calculateSum { a, b -> a >= b }

        // Find the sum of all the maximums and add to the answer
        answer += calculateSum { a, b -> a <= b }

        return answer
    }
}