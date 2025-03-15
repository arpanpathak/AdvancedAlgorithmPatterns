package tree

class BinaryTreeMaximumPathSum {
    fun maxPathSum(root: TreeNode?): Int {
        var ans = Int.MIN_VALUE
        fun getMaxPathSum(node: TreeNode?): Int {
            if (node == null) return 0

            val left = getMaxPathSum(node.left)
            val right = getMaxPathSum(node.right)
            val currentMax = maxOf(maxOf(left, right) + node.`val`, node.`val`)
            val maxSoFar = maxOf(currentMax, left + right + node.`val`)
            ans = maxOf(maxSoFar, ans)

            return currentMax
        }

        getMaxPathSum(root)
        return ans
    }
}