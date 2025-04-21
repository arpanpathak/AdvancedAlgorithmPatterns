package tree

class BInaryTreeInOrderTraversalIterative {
    fun inorderTraversal(root: TreeNode?): List<Int> {
        val stack = ArrayDeque<TreeNode>()
        val result = mutableListOf<Int>()
        var current = root

        while (current != null || stack.isNotEmpty()) {
            // Traverse to the leftmost node
            while (current != null) {
                stack.addLast(current)
                current = current.left
            }

            // Visit the node
            current = stack.removeLast()
            result.add(current.`val`)

            // Move to the right subtree
            current = current.right
        }

        return result
    }
}
