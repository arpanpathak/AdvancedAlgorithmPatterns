package tree

class LongestUnivaluePath {

    fun longestUnivaluePath(root: TreeNode?): Int {
        var maxLength = 0
        fun dfs(node: TreeNode?): Int {
            if (node == null) return 0

            // Get the maximum univalue chain from both left and right
            val left = dfs(node.left)
            val right = dfs(node.right)

            // Compute the branch lengthes if
            val currentLeft = if (node.left?.`val` == node.`val`) left + 1 else 0
            val currentRight = if (node.right?.`val` == node.`val`) right + 1 else 0

            // Take maximum of max found so far , OD
            maxLength = maxOf(maxLength, currentLeft + currentRight)

            // We can only propagate the maximum branch chain b/w left and right
            return maxOf(currentLeft, currentRight)
        }

        dfs(root)
        return maxLength
    }
}