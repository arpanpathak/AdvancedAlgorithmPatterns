package tree

import java.util.*
import kotlin.collections.ArrayList


class BinaryTreeLevelOrderTraversal {
    fun levelOrder(root: TreeNode?): List<List<Int>> {
        val result: MutableList<List<Int>> = ArrayList()
        if (root == null)
            return result
        val queue: Queue<TreeNode> = LinkedList()
        queue.add(root)

        while (!queue.isEmpty()) {
            val lst: MutableList<Int> = ArrayList()
            var size: Int = queue.size

            while (size-- > 0) {
                val temp: TreeNode = queue.poll()
                lst.add(temp.`val`)

                if (temp.left != null) queue.add(temp.left)

                if (temp.right != null) queue.add(temp.right)
            }

            result.add(lst)
        }
        return result
    }
}