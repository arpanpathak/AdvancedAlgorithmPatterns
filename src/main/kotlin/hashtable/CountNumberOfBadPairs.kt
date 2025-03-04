package hashtable

class CountNumberOfBadPairs {
    fun countBadPairs(nums: IntArray): Long {
        val freq = mutableMapOf<Int, Long>()
        var goodPairs = 0L

        for (i in nums.indices) {
            val key = nums[i] - i
            goodPairs += freq.getOrDefault(key, 0)
            freq[key] = freq.getOrDefault(key, 0) + 1
        }

        val n = nums.size.toLong()
        val totalPairs = (n * (n - 1)) / 2
        return totalPairs - goodPairs
    }
}