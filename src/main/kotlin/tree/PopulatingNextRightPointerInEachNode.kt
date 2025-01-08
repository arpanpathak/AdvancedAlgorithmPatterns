package tree

import java.util.*

class Node(var `val`: Int) {
    var left: Node? = null
    var right: Node? = null
    var next: Node? = null
}

class PopulatingNextRightPointerInEachNode {
    fun connect(root: Node?): Node? {
        root?.let { queue ->
            val q = LinkedList<Node>().apply { offer(queue) }

            while (q.isNotEmpty()) {
                val levelSize = q.size
                var prev: Node? = null

                repeat(levelSize) {
                    q.poll().also { node ->
                        prev?.next = node
                        prev = node
                        node.left?.let { q.offer(it) }
                        node.right?.let { q.offer(it) }
                    }
                }
            }
        }
        return root
    }
}