package backtracking

class PartitionToKEqualSumSubsets {
    fun canPartitionKSubsets(nums: IntArray, k: Int): Boolean {
        val totalSum = nums.sum()
        if (totalSum % k !=0) return false

        val targetSum = totalSum / k
        val used = BooleanArray(nums.size)

        fun backtrack(start: Int, currentSum: Int, remainingSubsets: Int): Boolean {
            if (remainingSubsets == 0)
                return true
            if (currentSum == targetSum)
                return backtrack(0, 0, remainingSubsets - 1)

            for(i in start until nums.size) {
                if (!used[i] && currentSum + nums[i] <= targetSum) {
                    used[i] = true
                    if (backtrack(i + 1, currentSum + nums[i], remainingSubsets))
                        return true
                    // backtracking
                    used[i] = false
                }
            }

            return false
        }

        return backtrack(0, 0, k)
    }
}