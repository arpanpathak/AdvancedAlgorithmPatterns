package array.hashtable

class DivideArrayIntoEqualPairs {
    fun divideArray(nums: IntArray): Boolean {
        val freqMap = mutableMapOf<Int, Int>()

        // Count the frequency of each number
        for (num in nums) {
            freqMap[num] = freqMap.getOrDefault(num, 0) + 1
        }

        // Check if every number has an even frequency
        for (count in freqMap.values) {
            if (count % 2 != 0) {
                return false
            }
        }

        return true
    }
}
