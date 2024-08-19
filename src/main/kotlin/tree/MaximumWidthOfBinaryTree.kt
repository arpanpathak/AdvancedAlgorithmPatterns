package tree

import java.util.*

class MaximumWidthOfBinaryTree {
    data class Node(var node: TreeNode?, var index: Int)

    fun widthOfBinaryTree(root: TreeNode?): Int {
        val queue: Queue<Node> = LinkedList<Node>()
        var max = 0
        queue.add(Node(root, 0))

        while (queue.isNotEmpty()) {
            var size = queue.size
            var left = queue.peek().index
            var right = left
            repeat(size) {
                val current = queue.poll()
                right = current.index

                current.node?.left?.let { queue.add(Node(it, 2 * current.index + 1)) }
                current.node?.right?.let { queue.add(Node(it, 2 * current.index + 2)) }
            }

            max = maxOf(max, right - left + 1)
        }

        return max
    }
}