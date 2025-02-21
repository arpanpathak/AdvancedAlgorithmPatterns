package array.prefixsum

class ZeroArrayTransformation_I {
    fun isZeroArray(nums: IntArray, queries: Array<IntArray>): Boolean {
        val n = nums.size
        val diff = IntArray(n + 1)

        for (query in queries) {
            val (l, r) = query
            diff[l] += 1
            if (r + 1 < n) {
                diff[r + 1] -= 1
            }
        }

        var total = 0
        for (i in nums.indices) {
            total += diff[i]
            if (total < nums[i]) {
                return false
            }
        }

        return true
    }
}
