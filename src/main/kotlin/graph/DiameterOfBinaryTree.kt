package graph

import javax.swing.Spring.height

class DiameterOfBinaryTree {
    fun diameterOfBinaryTree(root: TreeNode?): Int {
        var max = 0
        fun height(root: TreeNode?): Int {
            if (null == root) return -1
            val left = height(root.left)
            val right = height(root.right)
            max = Math.max(max, 2 + left + right)
            return 1 + Math.max(left, right)
        }

        height(root)
        return max
    }
}