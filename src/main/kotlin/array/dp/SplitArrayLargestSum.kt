package array.dp

class SplitArrayLargestSum {
    fun splitArray(nums: IntArray, k: Int): Int {
        val n = nums.size
        val prefixSum = IntArray(n + 1)
        for (i in nums.indices) {
            prefixSum[i + 1] = prefixSum[i] + nums[i]
        }

        val memo = mutableMapOf<Pair<Int, Int>, Int>()

        fun dfs(i: Int, splitsLeft: Int): Int {
            if (splitsLeft == 1) return prefixSum[n] - prefixSum[i] // Last split takes remaining sum
            if (i == n) return Int.MAX_VALUE
            if (memo.containsKey(i to splitsLeft)) return memo[i to splitsLeft]!!

            var minLargestSum = Int.MAX_VALUE

            for (j in i until n) {
                val currentSum = prefixSum[j + 1] - prefixSum[i]
                val largestInRemainingSplits = dfs(j + 1, splitsLeft - 1)
                val maxSplitSum = maxOf(currentSum, largestInRemainingSplits)

                minLargestSum = minOf(minLargestSum, maxSplitSum)

                // Prune unnecessary recursion
                if (currentSum > minLargestSum) break
            }

            memo[i to splitsLeft] = minLargestSum
            return minLargestSum
        }

        return dfs(0, k)
    }
}
