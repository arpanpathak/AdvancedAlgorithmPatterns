package array.prefixsum

class ContiguousArray {
    fun findMaxLength(nums: IntArray): Int {
        val map = mutableMapOf<Int, Int>()
        var sum = 0
        var maxLen = 0

        map[0] = -1

        for (i in nums.indices) {
            sum += if (nums[i] == 0) -1 else 1

            if (map.containsKey(sum)) {
                maxLen = maxOf(maxLen, i - map[sum]!!)
            } else {
                map[sum] = i
            }
        }

        return maxLen
    }
}