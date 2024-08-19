package tree

import java.util.*

class BinaryTreeZigZagLevelOrderTraversal {
    data class IndexedNode(var node: TreeNode?, var index: Int)

    fun zigzagLevelOrder(root: TreeNode?): List<List<Int>> {
        val result = mutableListOf<MutableList<Int>>()
        if (root == null) return result

        val queue: Queue<IndexedNode> = LinkedList()
        queue.add(IndexedNode(root, 0))
        var level = 0

        while (queue.isNotEmpty()) {
            val levelSize = queue.size
            val currentLevel = LinkedList<Int>()

            for (i in 0 until levelSize) {
                val (currentNode, _) = queue.poll()

                if (level % 2 == 0) {
                    currentLevel.add(currentNode?.`val` ?: 0)
                } else {
                    currentLevel.addFirst(currentNode?.`val` ?: 0)
                }

                currentNode?.left?.let { queue.add(IndexedNode(it, 2 * i + 1)) }
                currentNode?.right?.let { queue.add(IndexedNode(it, 2 * i + 2)) }
            }

            result.add(currentLevel)
            level++
        }

        return result
    }
}