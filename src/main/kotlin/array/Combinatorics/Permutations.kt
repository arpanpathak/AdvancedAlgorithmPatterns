package array.Combinatorics

class Permutations {
    fun permute(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun permute(nums: IntArray, start: Int) {
            if (start == nums.lastIndex) {
                result.add(nums.copyOf().toList())
                return
            }

            for (i in start..nums.lastIndex) {
                nums[start] = nums[i].also { nums[i] = nums[start] }
                permute(nums, start + 1 )
                nums[start] = nums[i].also { nums[i] = nums[start] }
            }
        }

        permute(nums, 0)

        return result
    }
}