package tree

import java.util.*

class PopulateNextRightPointersInEachNode_II_Constant {
    fun connect(root: Node?): Node? {
        var current: Node? = root

        while (current != null) {
            var nextLevelStart: Node? = null
            var prev: Node? = null
            var node = current

            // Traverse the current level
            while (node != null) {
                // Connect left child
                if (node.left != null) {
                    if (prev != null) prev.next = node.left
                    prev = node.left
                    if (nextLevelStart == null) nextLevelStart = node.left
                }

                // Connect right child
                if (node.right != null) {
                    if (prev != null) prev.next = node.right
                    prev = node.right
                    if (nextLevelStart == null) nextLevelStart = node.right
                }

                node = node.next
            }

            // Move to the next level
            current = nextLevelStart
        }

        return root
    }
}