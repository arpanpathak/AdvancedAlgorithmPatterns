package array.sorting

import kotlin.math.abs

class SquaresOfASortedArray {
    fun sortedSquares(nums: IntArray): IntArray {
        val result = IntArray(nums.size)

        var (left, right, index) = listOf(0, nums.lastIndex, nums.lastIndex)

        while (left <= right) {
            when {
                abs(nums[left]) > abs(nums[right]) -> result[index--] = nums[left] * nums[left++]
                else -> result[index--] = nums[right] * nums[right--]
            }
        }

        return result
    }
}
