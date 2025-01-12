package greedy

class JumpGame_II {
    fun jump(nums: IntArray): Int {
        var (jumps, currentEnd, farthest) = listOf(0, 0, 0)

        for (i in 0 until nums.lastIndex) {
            farthest = maxOf(farthest, i + nums[i])

            if ( i >= currentEnd) {
                jumps++
                currentEnd = farthest
            }
        }

        return jumps
    }
}