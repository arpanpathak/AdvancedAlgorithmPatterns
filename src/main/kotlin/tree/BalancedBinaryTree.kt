package tree

import kotlin.math.abs

class BalancedBinaryTree {
    fun isBalanced(root: TreeNode?): Boolean {
        fun checkHeight(node: TreeNode?): Int {
            if (node == null) return 0

            val leftHeight = checkHeight(node.left)
            val rightHeight = checkHeight(node.right)

            // If subtree is unbalanced, return -1 to signal it
            if (leftHeight == -1 || rightHeight == -1 || abs(leftHeight - rightHeight) > 1) {
                return -1
            }

            // Return the height of the tree rooted at this node
            return 1 + maxOf(leftHeight, rightHeight)
        }

        return checkHeight(root) != -1
    }
}