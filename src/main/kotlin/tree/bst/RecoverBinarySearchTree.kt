package tree.bst

class RecoverBinarySearchTree {
    fun recoverTree(root: TreeNode?) {
        var first: TreeNode? = null
        var second: TreeNode? = null
        var prev: TreeNode? = null

        // Helper function to perform in-order traversal
        fun dfs(node: TreeNode?) {
            if (node == null) return

            // Traverse left subtree
            dfs(node.left)

            // Identify swapped nodes
            if (prev != null && prev!!.`val` > node.`val`) {
                if (first == null) {
                    first = prev  // First out-of-order node
                }
                second = node  // Second out-of-order node
            }

            // Update previous node
            prev = node

            // Traverse right subtree
            dfs(node.right)
        }

        // Step 1: In-order traversal to find the swapped nodes
        dfs(root)

        // Step 2: Swap the values of the two nodes
        first?.let { f ->
            second?.let { s ->
                val temp = f.`val`
                f.`val` = s.`val`
                s.`val` = temp
            }
        }
    }
}