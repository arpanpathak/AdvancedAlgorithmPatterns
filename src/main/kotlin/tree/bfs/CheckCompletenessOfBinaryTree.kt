package tree.bfs

import tree.TreeNode

class CheckCompletenessOfBinaryTree {
    fun isCompleteTree(root: TreeNode?): Boolean {
        if (root == null) return true // Edge case: An empty tree is considered complete

        val queue = ArrayDeque<TreeNode?>()
        queue.add(root)

        var foundNull = false // A flag to indicate if a null node has been encountered

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            // If we have already seen a null node, we shouldn't encounter any non-null nodes after that.
            if (current == null) {
                foundNull = true
            } else {
                if (foundNull) return false // If we encounter a non-null node after a null node, it's not complete

                queue.add(current.left)
                queue.add(current.right)
            }
        }

        return true
    }
}