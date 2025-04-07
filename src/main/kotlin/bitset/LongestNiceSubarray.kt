package bitset

class LongestNiceSubarray {
    fun longestNiceSubarray(nums: IntArray): Int {
        var left = 0
        var bitMask = 0
        var maxLen = 0

        for (right in nums.indices) {
            while ((bitMask and nums[right]) != 0) {
                bitMask = bitMask xor nums[left]
                left++
            }
            bitMask = bitMask or nums[right]
            maxLen = maxOf(maxLen, right - left + 1)
        }

        return maxLen
    }
}