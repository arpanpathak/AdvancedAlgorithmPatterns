package array.hashtable

class DegreeOfAnArray {
    fun findShortestSubArray(nums: IntArray): Int {
        val first = HashMap<Int, Int>()
        val count = HashMap<Int, Int>()
        var maxFreq = 0
        var minLength = 0

        for ((index, num) in nums.withIndex()) {
            first.putIfAbsent(num, index)
            val freq = (count[num] ?: 0) + 1
            count[num] = freq

            val len = index - first[num]!! + 1

            if (freq > maxFreq) {
                maxFreq = freq
                minLength = len
            } else if (freq == maxFreq) {
                minLength = minOf(minLength, len)
            }
        }
        return minLength
    }
}
