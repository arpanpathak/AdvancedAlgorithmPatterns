package tree.bfs

import tree.TreeNode
import java.util.*

class AverageOfLevelsInBinaryTree {
    fun averageOfLevels(root: TreeNode?): DoubleArray {
        var result = mutableListOf<Double>()

        val queue = LinkedList<TreeNode>()

        queue.add(root!!)

        while (queue.isNotEmpty()) {
            var sum = 0.0
            val size = queue.size

            repeat(size) {
                val node = queue.poll()
                sum+= node.`val`

                node.left?.let{ queue.add(it) }
                node.right?.let{ queue.add(it) }

            }
            result.add(sum / size)
        }

        return result.toDoubleArray()
    }
}