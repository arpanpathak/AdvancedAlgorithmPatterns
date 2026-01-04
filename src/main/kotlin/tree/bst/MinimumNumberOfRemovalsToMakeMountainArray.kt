package tree.bst

import java.util.*

class MinimumNumberOfRemovalsToMakeMountainArray {
    fun minimumMountainRemovals(nums: IntArray): Int {
        val n = nums.size

        // computes LIS length ending at each index using your TreeSet, i.e height balanced BST
        fun getLisLens(arr: IntArray): IntArray {
            val dp = IntArray(n)
            val treeSet = TreeSet<Int>()

            arr.forEachIndexed { i, h ->
                // replace smallest element >= h to keep tails compact
                treeSet.ceiling(h)?.let { treeSet.remove(it) }
                treeSet.add(h)

                // strictly increasing elements before h determine rank
                dp[i] = treeSet.headSet(h).size + 1
            }
            return dp
        }

        val left = getLisLens(nums)
        val right = getLisLens(nums.reversedArray()).reversedArray()

        // find max mountain length where peak has slopes on both sides
        val maxMountain = nums.indices.maxOfOrNull { i ->
            if (left[i] > 1 && right[i] > 1) left[i] + right[i] - 1 else 0
        } ?: 0

        return n - maxMountain
    }
}