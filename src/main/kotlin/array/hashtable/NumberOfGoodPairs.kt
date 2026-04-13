package array.hashtable

class NumberOfGoodPairs {
    fun numIdenticalPairs(nums: IntArray): Int {
        var goodPairs = 0
        val counts = mutableMapOf<Int, Int>()

        for (num in nums) {
            val count = counts.getOrDefault(num, 0)
            goodPairs += count
            counts[num] = count + 1
        }

        return goodPairs
    }
}
