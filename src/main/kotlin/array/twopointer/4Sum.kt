package array.twopointer

class `4Sum` {
    fun fourSum(nums: IntArray, target: Int): List<List<Int>> {
        nums.sort()
        return kSum(nums, target.toLong(), 0, 4)
    }

    private fun kSum(
        nums: IntArray,
        target: Long,
        start: Int,
        k: Int
    ): List<List<Int>> {
        val res = mutableListOf<List<Int>>()

        // If we've run out of numbers to add, return res.
        if (start == nums.size) {
            return res
        }

        // The average of the remaining k values is at least target / k.
        val averageValue = target / k

        // Early termination if the smallest number is too large or the largest is too small.
        if (nums[start] > averageValue || averageValue > nums[nums.size - 1]) {
            return res
        }

        // Base case: 2Sum (optimized with two pointers)
        if (k == 2) {
            return twoSum(nums, target, start)
        }

        for (i in start until nums.size) {
            // Skip duplicates
            if (i == start || nums[i - 1] != nums[i]) {
                // Recursively reduce to (k-1)Sum
                for (subset in kSum(nums, target - nums[i], i + 1, k - 1)) {
                    res.add(listOf(nums[i]) + subset)
                }
            }
        }

        return res
    }

    private fun twoSum(
        nums: IntArray,
        target: Long,
        start: Int
    ): List<List<Int>> {
        val res = mutableListOf<List<Int>>()
        val seen = HashSet<Long>()

        for (i in start until nums.size) {
            // Skip duplicates
            if (res.isEmpty() || res.last()[1] != nums[i]) {
                val complement = target - nums[i]
                if (seen.contains(complement)) {
                    res.add(listOf(complement.toInt(), nums[i]))
                }
            }
            seen.add(nums[i].toLong())
        }

        return res
    }
}