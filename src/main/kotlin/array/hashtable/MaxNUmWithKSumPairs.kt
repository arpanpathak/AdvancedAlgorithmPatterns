package array.hashtable

class MaxNUmWithKSumPairs {
    fun maxOperations(nums: IntArray, k: Int): Int {
        val map = mutableMapOf<Int, Int>()
        var count = 0

        for (num in nums) {
            val remainingSum = k - num
            when {
                map.getOrDefault(remainingSum, 0)  > 0 -> {
                    count++
                    map[remainingSum] = map[remainingSum]!! - 1
                }
                else -> map[num] = map.getOrDefault(num, 0) + 1
            }
        }

        return count
    }
}