package array.prefixsum

class ProductOfArrayExceptSelf {
    fun productExceptSelf(nums: IntArray): IntArray? {
        var zeroes = 0
        var product = 1
        val N = nums.size
        val productArray = IntArray(N)
        for (num in nums) {
            if (num == 0) zeroes++ else product *= num
        }
        for (i in 0 until N) {
            productArray[i]  = when {
                nums[i] == 0 && zeroes == 1 -> product
                nums[i] == 0 || zeroes >= 1 -> 0
                else -> product / nums[i]
            }
        }
        return productArray
    }
}