package tree.bst

import java.util.*

// Patience sorting algorithm
class LongestIncreasingSubsequence {
    fun lengthOfLIS(nums: IntArray): Int {
        val tree = TreeSet<Int>()
        nums.forEach { num ->
            // Find the smallest element in the tree which is greater than or equal to num
            tree.ceiling(num)?.also {
                // If such an element is found, it means num can replace it to potentially
                // form a new valid increasing subsequence or extend the current one
                tree.remove(it)
            }
            // Add num to the tree. If num is not replacing any element,
            // it is extending the subsequence with a new larger element.
            tree.add(num)
        }
        // The size of the tree at the end will represent the length of the longest
        // increasing subsequence
        return tree.size
    }
}
