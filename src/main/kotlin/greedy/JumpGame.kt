package greedy

class JumpGame {
    fun canJump(nums: IntArray): Boolean {
        var maxIndex = 0
        var i = 0
        while (i <= maxIndex) {
            maxIndex = maxOf(maxIndex, i + nums[i])

            if (maxIndex >= nums.size - 1) {
                return true
            }

            i++
        }

        return false
    }
}