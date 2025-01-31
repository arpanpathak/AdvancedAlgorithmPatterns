package tree

class MaximumSumBSTInBinaryTree {
    data class Result(val isBST: Boolean, val sum: Int, val min: Int, val max: Int)
    private var maxSum = 0

    fun maxSumBST(root: TreeNode?): Int {
        dfs(root)
        return maxSum
    }

    private fun dfs(node: TreeNode?): Result {
        // Base case: if the node is null, it's trivially a BST with sum = 0
        if (node == null) {
            return Result(true, 0, Int.MAX_VALUE, Int.MIN_VALUE)
        }

        // Recursively traverse the left and right subtrees
        val left = dfs(node.left)
        val right = dfs(node.right)

        // Check if the current node is a valid BST
        if (left.isBST && right.isBST && node.`val` > left.max && node.`val` < right.min) {
            // Current node is part of a valid BST
            val sum = node.`val` + left.sum + right.sum
            maxSum = maxOf(maxSum, sum) // Update the global max sum if needed
            // Return the result for this subtree
            return Result(
                isBST = true,
                sum = sum,
                min = minOf(node.`val`, left.min),
                max = maxOf(node.`val`, right.max)
            )
        }

        // If it's not a valid BST, return invalid information
        return Result(false, sum=0, min=0, max=0)
    }
}
