package sorting

class LargestNumber {
    fun largestNumber(nums: IntArray): String {
        val strNums = nums.map { it.toString() }.toTypedArray()
        strNums.sortWith { a, b -> (b + a).compareTo(a + b) }

        // If the largest number is "0", return "0"
        if (strNums[0] == "0") {
            return "0"
        }

        // Join the numbers to form the largest number
        return strNums.joinToString("")
    }
}