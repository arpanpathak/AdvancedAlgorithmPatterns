package tree.bst

import java.util.*

class LongestIncreasingSubsequence {
    fun lengthOfLIS(nums: IntArray): Int {
        val tree = TreeSet<Int>()
        nums.forEach { num ->
            tree.ceiling(num)?.let { tree.remove(it) }.also { tree.add(num) }
        }

        return tree.size
    }
}