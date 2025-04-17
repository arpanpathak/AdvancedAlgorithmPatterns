package array

class SignOfTheProductOfAnArray {
    fun arraySign(nums: IntArray): Int {
        var negativeCount = 0

        nums.forEach {
            if (it == 0)    return 0
            if (it < 0) negativeCount++
        }

        return when {
            negativeCount % 2 == 0 -> 1
            else -> -1
        }
    }
}
