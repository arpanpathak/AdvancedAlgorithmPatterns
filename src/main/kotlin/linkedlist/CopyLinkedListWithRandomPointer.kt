package linkedlist

class CopyLinkedListWithRandomPointer {
    /**
     * Example:
     * var ti = Node(5)
     * var v = ti.`val`
     * Definition for a Node.
     * class Node(var `val`: Int) {
     *     var next: Node? = null
     *     var random: Node? = null
     * }
     */

    class Node(var `val`: Int) {
        var next: Node? = null
        var random: Node? = null
    }

    class Solution {
        fun copyRandomList(node: Node?): Node? {
            if (node == null) return null

            val nodeMap = mutableMapOf<Node, Node>()

            // Step 1: Create deep copies of all nodes and store the mapping
            var ptr = node
            while (ptr != null) {
                nodeMap[ptr] = Node(ptr.`val`)
                ptr = ptr.next
            }

            // Step 2: Set the next and random pointers for the copied nodes
            ptr = node
            while (ptr != null) {
                nodeMap[ptr]?.next = nodeMap[ptr.next]
                nodeMap[ptr]?.random = nodeMap[ptr.random]
                ptr = ptr.next
            }

            // Return the head of the new copied list
            return nodeMap[node]
        }
    }
}