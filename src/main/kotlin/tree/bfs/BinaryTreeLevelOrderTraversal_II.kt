package tree.bfs

import tree.TreeNode
import java.util.*

class BinaryTreeLevelOrderTraversal_II {
    fun levelOrderBottom(root: TreeNode?): List<List<Int>> {
        val result = LinkedList<List<Int>>()
        val queue = LinkedList<TreeNode>()

        if (root == null)
            return listOf()
        queue.add(root!!)

        while (queue.isNotEmpty()) {
            val size = queue.size
            val currentLevel = mutableListOf<Int>()
            repeat(size) {
                val node = queue.poll()
                currentLevel.add(node.`val`)

                node.left?.let{ queue.add(it) }
                node.right?.let{ queue.add(it) }

            }
            result.addFirst(currentLevel)
        }

        return result.toList()
    }
}