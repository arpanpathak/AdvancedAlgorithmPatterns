package tree

class RecoverATreeFromPreOrderTraversal {
    fun recoverFromPreorder(traversal: String): TreeNode? {
        var index = 0 // Global index to track position in the string

        // Recursive function to build the tree
        fun buildTree(depth: Int): TreeNode? {
            if (index >= traversal.length) return null // Base case: end of string

            // Read the depth of the current node
            var currentDepth = 0
            while (index < traversal.length && traversal[index] == '-') {
                currentDepth++
                index++
            }

            // If the current depth doesn't match the expected depth, backtrack
            if (currentDepth != depth) {
                index -= currentDepth // Rewind the index
                return null
            }

            // Read the value of the current node
            var value = 0
            while (index < traversal.length && traversal[index].isDigit()) {
                value = value * 10 + (traversal[index] - '0')
                index++
            }

            // Create the current node
            val node = TreeNode(value)

            // Recursively build the left and right subtrees
            node.left = buildTree(depth + 1)
            node.right = buildTree(depth + 1)

            return node
        }

        return buildTree(0) // Start building the tree from depth 0
    }
}
