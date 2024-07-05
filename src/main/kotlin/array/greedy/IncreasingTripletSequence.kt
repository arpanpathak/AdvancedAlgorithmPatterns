package array.greedy

class IncreasingTripletSequence {
    fun increasingTriplet(nums: IntArray): Boolean {
        var (smallest, secondSmallest) = Pair(Int.MAX_VALUE, Int.MAX_VALUE)

        for (num in nums) {
            when {
                num <= smallest -> smallest = num
                num <= secondSmallest -> secondSmallest = num
                else -> return true
            }
        }

        return false
    }
}