package array.prefixsum

class NumberOfZeroFilledSubArrays {
    fun zeroFilledSubarray(nums: IntArray): Long {
        var count = 0L
        var currentZeroCount = 0

        for (num in nums) {
            if (num == 0) {
                currentZeroCount++
                count += currentZeroCount
            } else {
                currentZeroCount = 0
            }
        }

        return count
    }
}