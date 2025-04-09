package dynamic_programming

class ClosestSubsequenceSum {
    fun minAbsDifference(nums: IntArray, goal: Int): Int {
        val memo = mutableMapOf<Pair<Int, Int>, Int>()

        fun dfs(index: Int, currentSum: Int): Int {
            if (index == nums.size) {
                return kotlin.math.abs(currentSum - goal)
            }

            val key = Pair(index, currentSum)
            if (key in memo) return memo[key]!!

            val exclude = dfs(index + 1, currentSum)
            val include = dfs(index + 1, currentSum + nums[index])

            val result = minOf(exclude, include)
            memo[key] = result
            return result
        }

        return dfs(0, 0)
    }
}
