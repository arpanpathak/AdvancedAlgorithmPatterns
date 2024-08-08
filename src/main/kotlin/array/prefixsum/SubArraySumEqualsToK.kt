package array.prefixsum

class SubArraySumEqualsToK {
    fun subarraySum(nums: IntArray, k: Int): Int {
        val preSumFreq = mutableMapOf<Int, Int>()
        preSumFreq[0] = 1 // covers the case where sum - k = 0, meaning the current sub-array is valid

        var count = 0
        var sum = 0
        for (num in nums) {
            sum += num
            count += preSumFreq[sum - k] ?: 0
            preSumFreq[sum] = (preSumFreq[sum]?: 0) + 1
        }

        return count
    }
}