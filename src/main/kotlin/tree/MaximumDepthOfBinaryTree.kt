package tree

class MaximumDepthOfBinaryTree {
    fun maxDepth(root: TreeNode?): Int {
        if (root == null) {
            return 0
        }

        return 1 + maxOf(maxDepth(root.left), maxDepth(root.right))
    }
}