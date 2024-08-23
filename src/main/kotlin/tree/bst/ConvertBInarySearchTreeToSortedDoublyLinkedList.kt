package tree.bst
/**
 * Definition for a Node.
 class Node(var `val`: Int) {
    var left: Node? = null
    var right: Node? = null
 }
 */
class ConvertBInarySearchTreeToSortedDoublyLinkedList {
    class Node(var `val`: Int) {
        var left: Node? = null
        var right: Node? = null
    }

    private var head: Node? = null
    private var tail: Node? = null

    fun treeToDoublyList(root: Node?): Node? {
        if (root == null) return null

        // Perform in-order traversal and link nodes
        inorderTraversal(root)

        // Complete the doubly linked list by linking head and tail
        head?.left = tail
        tail?.right = head

        return head
    }

    private fun inorderTraversal(node: Node?) {
        if (node == null) return

        inorderTraversal(node.left) // Traverse left subtree

        if (tail != null) {
            // Link the current node with the previous node (tail)
            tail!!.right = node
            node.left = tail
        } else {
            // This is the first node (head of the doubly linked list)
            head = node
        }

        // Update the tail to the current node
        tail = node

        inorderTraversal(node.right) // Traverse right subtree
    }
}