package array.hashtable

class FirstMissingPositive {
    fun firstMissingPositive(nums: IntArray): Int {
        val n = nums.size

        // Step 1: Replace negative numbers and zeros with a placeholder > n
        for (i in nums.indices) {
            if (nums[i] <= 0) nums[i] = n + 1
        }

        // Step 2: Mark the presence by making nums[val - 1] negative
        for (i in nums.indices) {
            val num = kotlin.math.abs(nums[i])
            if (num in 1..n) {
                val idx = num - 1
                if (nums[idx] > 0) {
                    nums[idx] = -nums[idx]
                }
            }
        }

        // Step 3: Find the first positive number
        for (i in nums.indices) {
            if (nums[i] > 0) return i + 1
        }

        return n + 1
    }
}
