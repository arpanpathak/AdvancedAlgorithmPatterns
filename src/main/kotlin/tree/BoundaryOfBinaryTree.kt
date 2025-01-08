package tree

class BoundaryOfBinaryTree {
    fun boundaryOfBinaryTree(root: TreeNode?): List<Int> {
        val result = mutableListOf<Int>()
        if (root == null) return result

        // Step 1: Add the root node
        result.add(root.`val`)

        // Step 2: Add the left boundary (excluding leaves)
        addLeftBoundary(root.left, result)

        // Step 3: Add all leaf nodes (excluding root if it is a leaf)
        addLeaves(root, result, isRoot = true)

        // Step 4: Add the right boundary (excluding leaves, in reverse order)
        val rightBoundary = mutableListOf<Int>()
        addRightBoundary(root.right, rightBoundary)
        result.addAll(rightBoundary.reversed())

        return result
    }

    private fun addLeftBoundary(node: TreeNode?, result: MutableList<Int>) {
        var current = node
        while (current != null) {
            // Don't add leaf nodes to the left boundary
            if (current.left != null || current.right != null) {
                result.add(current.`val`)
            }
            // Move down the left child, or the right if left is null
            current = current.left ?: current.right
        }
    }

    private fun addLeaves(node: TreeNode?, result: MutableList<Int>, isRoot: Boolean) {
        if (node == null) return
        // If it's a leaf node and it's not the root, add it to the result
        if (node.left == null && node.right == null && !isRoot) {
            result.add(node.`val`)
            return
        }
        // Otherwise, continue the traversal for both subtrees
        addLeaves(node.left, result, isRoot = false)
        addLeaves(node.right, result, isRoot = false)
    }

    private fun addRightBoundary(node: TreeNode?, result: MutableList<Int>) {
        var current = node
        while (current != null) {
            // Don't add leaf nodes to the right boundary
            if (current.left != null || current.right != null) {
                result.add(current.`val`)
            }
            // Move down the right child, or the left if right is null
            current = current.right ?: current.left
        }
    }
}
