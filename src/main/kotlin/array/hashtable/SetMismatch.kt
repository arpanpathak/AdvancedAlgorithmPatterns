package array.hashtable

class SetMismatch {
    fun findErrorNums(nums: IntArray): IntArray {
        val seen = mutableSetOf<Int>()
        var duplicate = -1
        var actualSum = 0
        val n = nums.size

        for (num in nums) {
            if (!seen.add(num)) {
                duplicate = num
            }
            actualSum += num
        }

        val expectedSum = n * (n + 1) / 2
        val missing = expectedSum - (actualSum - duplicate)

        return intArrayOf(duplicate, missing)
    }
}
