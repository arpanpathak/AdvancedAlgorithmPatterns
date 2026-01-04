package array.twopointer

class NumberOfArithmaticTriplet {
    fun arithmeticTriplets(nums: IntArray, diff: Int): Int {
        var count = 0
        val seen = nums.toSet()

        for (x in nums) {
            if (seen.contains(x - diff) && seen.contains(x - 2 * diff)) {
                count++
            }
        }

        return count
    }
}