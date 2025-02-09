package array.twopointer

import kotlin.math.abs

class ThreeSumClosest {
    fun threeSumClosest(nums: IntArray, target: Int): Int {
        nums.sort()
        var closestSum = nums[0] + nums[1] + nums[2]

        for (i in 0 until nums.size - 2) {
            var left = i + 1
            var right = nums.size - 1

            while (left < right) {
                val sum = nums[i] + nums[left] + nums[right]
                if (abs(target - sum) < abs(target - closestSum)) {
                    closestSum = sum
                }
                when {
                    sum < target -> left++
                    sum > target -> right--
                    else -> return sum
                }
            }
        }
        return closestSum
    }
}
