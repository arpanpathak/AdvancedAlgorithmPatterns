package array.prefixsum

class SubArrayProductLessThanK {
    fun numSubarrayProductLessThanK(nums: IntArray, k: Int): Int {
        if ( k <=1) return 0

        var (count, product, left) = listOf(0, 1, 0)

        for (right in nums.indices) {
            product *= nums[right]

            while (product >=k ) {
                product /= nums[left++]
            }

            count += (right - left) + 1
        }

        return count
    }
}