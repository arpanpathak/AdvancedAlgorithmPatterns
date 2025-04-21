package tree

import java.util.*

class VerticalOrderTraversalOfABinaryTree {
    data class VerticalIndex(val node: TreeNode, val verticalIndex: Int)

    fun verticalTraversal(root: TreeNode?): List<List<Int>> {
        if (root == null) return emptyList()

        val columnTable = mutableMapOf<Int, TreeSet<Int>>()
        var minColumn = 0
        var maxColumn = 0

        val queue: Queue<VerticalIndex> = LinkedList()
        queue.offer(VerticalIndex(root, 0))

        while (queue.isNotEmpty()) {
            val (node, column) = queue.poll()

            columnTable.getOrPut(column) { TreeSet() }.add(node.`val`)

            // Update min and max column indices
            minColumn = minOf(minColumn, column)
            maxColumn = maxOf(maxColumn, column)

            node.left?.let { queue.offer(VerticalIndex(it, column - 1)) }
            node.right?.let { queue.offer(VerticalIndex(it, column + 1)) }
        }

        val result = mutableListOf<List<Int>>()
        for (col in minColumn..maxColumn) {
            result.add(columnTable[col]!!.toList())
        }

        return result
    }
}