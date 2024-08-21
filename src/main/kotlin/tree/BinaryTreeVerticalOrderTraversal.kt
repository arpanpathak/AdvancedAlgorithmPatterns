package tree

import java.util.*

class BinaryTreeVerticalOrderTraversal {
//    fun verticalOrder(root: TreeNode?): List<List<Int>> {
//        val result = TreeMap<Int, LinkedList<Int>>()
//
//        fun dfs(node: TreeNode?, verticalIndex: Int = 0) {
//            if (node == null)
//                return
//            val bucket = result.getOrPut(verticalIndex) { LinkedList() }
//
//            bucket.add(node.`val`)
//            dfs(node.left, verticalIndex - 1)
//            dfs(node.right, verticalIndex + 1)
//        }
//
//        dfs(root)
//
//        return result.map { it.value }
//    }

    // Define a data class for holding a node and its vertical index
    data class VerticalIndex(val node: TreeNode, val verticalIndex: Int)

    fun verticalOrder(root: TreeNode?): List<List<Int>> {
        if (root == null) return emptyList()

        val result = TreeMap<Int, ArrayList<Int>>()
        val queue: Queue<VerticalIndex> = LinkedList()

        // Start with the root node at vertical index 0
        queue.offer(VerticalIndex(root, 0))

        while (queue.isNotEmpty()) {
            val size = queue.size
            val levelNodes = mutableMapOf<Int, ArrayList<Int>>()

            for (i in 0 until size) {
                val (node, verticalIndex) = queue.poll()

                // Add node value to the corresponding vertical index in this level
                val bucket = levelNodes.getOrPut(verticalIndex) { ArrayList() }
                bucket.add(node.`val`)

                // Add left and right children to the queue with updated vertical indices
                node.left?.let { queue.offer(VerticalIndex(it, verticalIndex - 1)) }
                node.right?.let { queue.offer(VerticalIndex(it, verticalIndex + 1)) }
            }

            // Merge the level nodes into the result map
            for ((index, nodes) in levelNodes) {
                val bucket = result.getOrPut(index) { ArrayList() }
                bucket.addAll(nodes)
            }
        }

        // Return the values from the TreeMap, which are already sorted by vertical index
        return result.values.toList()
    }
}