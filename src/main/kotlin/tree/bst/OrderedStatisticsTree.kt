package tree.bst

// Find Kth smallest element
class OrderedStatisticsTree {
    class TreeNode(var `val`: Int) {
        var left: TreeNode? = null
        var right: TreeNode? = null
        var size: Int = 1 // New field
    }

    class Solution {
        fun findKth(node: TreeNode?, k: Int): Int {
            if (node == null) return -1

            val leftSize = node.left?.size ?: 0

            return when {
                k == leftSize + 1 -> node.`val`
                k <= leftSize -> findKth(node.left, k)
                else -> findKth(node.right, k - leftSize - 1)
            }
        }
    }
}