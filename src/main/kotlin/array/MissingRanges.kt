package array

class MissingRanges {
    fun findMissingRanges(nums: IntArray, lower: Int, upper: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        var currentRangePointer = lower

        for(num in nums) {
            if (num > currentRangePointer)
                result.add(listOf(currentRangePointer, num - 1))
            currentRangePointer = num + 1
        }

        if (currentRangePointer <= upper) {
            result.add(listOf(currentRangePointer, upper))
        }

        return result
    }
}