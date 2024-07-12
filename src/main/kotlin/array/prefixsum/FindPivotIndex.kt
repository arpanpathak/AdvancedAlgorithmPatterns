package array.prefixsum

class FindPivotIndex {
    fun pivotIndex(nums: IntArray): Int {
        val totalSum = nums.sum()
        var prefixSum = 0

        for (i in nums.indices ) {
            if (prefixSum == totalSum - prefixSum - nums[i])
                return i

            prefixSum += nums[i]
        }

        return -1
    }
}