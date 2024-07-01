package array

class TwoSum_II {
    fun twoSum(numbers: IntArray, target: Int): IntArray {
        var start = 0
        var end = numbers.lastIndex

        while ( start < end ) {
            val sum = numbers[start] + numbers[end]

            when {
                sum == target -> {
                    return intArrayOf(start + 1, end + 1)
                }
                sum < target -> start++
                else -> end--
            }
        }

        return intArrayOf()
    }
}