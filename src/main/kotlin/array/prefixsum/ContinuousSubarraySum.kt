package array.prefixsum

// Pattern https://leetcode.com/problems/continuous-subarray-sum/discuss/5276981/prefix-sum-hashmap-patterns-7-problems
class ContinuousSubarraySum {
    fun checkSubarraySum(nums: IntArray, k: Int): Boolean {
        val sumsSet: HashSet<Int> = HashSet(nums.size)
        var sum = 0
        var prevSum: Int

        for (num in nums) {
            prevSum = sum
            sum = (sum + num) % k
            if (sumsSet.contains(sum)) {
                return true
            }
            sumsSet.add(prevSum)
        }

        return false
    }
}