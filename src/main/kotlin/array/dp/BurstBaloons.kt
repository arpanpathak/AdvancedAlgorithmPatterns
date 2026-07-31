package array.dp

// Time COmplexity O(N^3) Space O(N^2)
class BurstBaloons {
    fun maxCoins(nums: IntArray): Int {
        if (nums.isEmpty()) return 0
        val dp = Array(nums.size) { IntArray(nums.size) }

        fun dfs(l: Int, r: Int): Int {
            when {
                l > r -> return 0
                dp[l][r] != 0 -> return dp[l][r]
            }

            var res = 0
            for (mid in l..r) {
                // Calculate the maximum coins for this partition
                val left = if (l == 0) 1 else nums[l - 1]
                val right = if (r == nums.size - 1) 1 else nums[r + 1]

                res = maxOf(res, dfs(l, mid - 1) +
                        left * nums[mid] * right +
                        dfs(mid + 1, r))
            }

            return res.also {  dp[l][r] = res }
        }
        return dfs(0, nums.size - 1)
    }
}
