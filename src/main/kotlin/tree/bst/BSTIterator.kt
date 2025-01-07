package tree.bst

class BSTIterator(root: TreeNode?) {
    private val stack = ArrayDeque<TreeNode>()

    init {
        // Initialize the stack by adding all the leftmost nodes
        pushAllLeftNodes(root)
    }

    // Push all the left nodes of a given node to the stack
    private fun pushAllLeftNodes(node: TreeNode?) {
        var current = node
        while (current != null) {
            stack.addFirst(current)
            current = current.left
        }
    }

    // Returns the next smallest number
    fun next(): Int {
        val node = stack.removeFirst()  // Pop the node from the stack
        pushAllLeftNodes(node.right)  // Push the leftmost nodes of the right child, if any
        return node.`val`
    }

    // Returns whether we have a next smallest number
    fun hasNext(): Boolean {
        return stack.isNotEmpty()  // If the stack is not empty, there are more nodes to visit
    }
}

/**
 * Your BSTIterator object will be instantiated and called as such:
 * var obj = BSTIterator(root)
 * var param_1 = obj.next()
 * var param_2 = obj.hasNext()
 */