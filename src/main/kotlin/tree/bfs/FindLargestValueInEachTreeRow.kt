package tree.bfs

import tree.TreeNode
import java.util.*

class FindLargestValueInEachTreeRow {
    fun largestValues(root: TreeNode?): List<Int> {
        val result = mutableListOf<Int>()

        val queue: Queue<TreeNode> = LinkedList()
        root?.let{ queue.offer(it) }

        while (queue.isNotEmpty()) {
            val size = queue.size
            var max = Int.MIN_VALUE

            repeat(size) {
                val current = queue.poll()
                max = maxOf(max, current.`val`)

                current.left?.let{ queue.offer(it) }
                current.right?.let{ queue.offer(it) }
            }

            result.add(max)
        }

        return result
    }
}
