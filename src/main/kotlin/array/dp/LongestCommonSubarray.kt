package array.dp

class LongestCommonSubarray {
    val memo = mutableMapOf<String, Int>()

    fun findLength(nums1: IntArray, nums2: IntArray): Int {
        return dp(nums1, nums2, nums1.lastIndex, nums2.lastIndex)

    }

//    fun dp(nums1: IntArray, nums2: IntArray, i: Int, j: Int, max: Int): Int {
//        if (i == 0 || j == 0)
//            return max
//        val key ="$i.$j"
//        if (memo.containsKey(key))
//            return memo[key]!!
//
//        var maxSoFar = max
//
//        if (nums1[i] == nums1[j])
//            maxSoFar = dp(nums1, nums2, i + 1, j + 1, max + 1)
//
//        var
//    }

    fun findLength_tabular(nums1: IntArray, nums2: IntArray): Int {
        val m = nums1.size
        val n = nums2.size
        val dp = Array(m + 1) { IntArray(n + 1) }
        var maxLength = 0

        // Fill the dp array
        for (i in 1..m) {
            for (j in 1..n) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1
                    maxLength = maxOf(maxLength, dp[i][j])
                } else {
                    dp[i][j] = 0
                }
            }
        }

        return maxLength
    }

}
