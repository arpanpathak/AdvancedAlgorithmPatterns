package binarysearch

class HouseRobber_IV {
    fun minCapability(nums: IntArray, k: Int): Int {
        var left = nums.minOrNull() ?: 0
        var right = nums.maxOrNull() ?: 0

        fun canRob(cap: Int): Boolean {
            var robbed = 0
            var i = 0
            while (i < nums.size) {
                if (nums[i] <= cap) {
                    robbed++
                    i += 2  // skip adjacent
                } else {
                    i++
                }
            }
            return robbed >= k
        }

        while (left < right) {
            val mid = (left + right) / 2
            if (canRob(mid)) {
                right = mid
            } else {
                left = mid + 1
            }
        }

        return left
    }
}