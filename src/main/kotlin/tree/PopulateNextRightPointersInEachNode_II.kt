package tree

import java.util.*

class PopulateNextRightPointersInEachNode_II {
    fun connect(root: Node?): Node? {
        val queue: Queue<Node> = LinkedList()
        root?.let { queue.add(it) }
        while (queue.isNotEmpty()) {
            var prev: Node? = null
            repeat(queue.size) {
                val node = queue.poll()
                node?.left?.let { queue.add(it) }
                node?.right?.let { queue.add(it) }

                prev?.let{ prev?.next = node }
                prev = node
            }
        }
        return root
    }
}