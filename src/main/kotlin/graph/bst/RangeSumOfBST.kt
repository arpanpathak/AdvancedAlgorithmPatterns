package graph.bst

class RangeSumOfBST {
    fun rangeSumBST(root: TreeNode?, low: Int, high: Int): Int {
        var sum = 0
        fun dfs(node: TreeNode?) {
            if (node == null)
                return

            dfs(node.left)

            if (node.`val` in low..high)
                sum += node.`val`

            dfs(node.right)
        }
        dfs(root)

        return sum
    }
}