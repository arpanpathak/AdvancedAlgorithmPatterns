package tree

class CountGoodNodeInBInaryTree {
    fun goodNodes(root: TreeNode?): Int {
        return dfs(root, Int.MIN_VALUE)
    }

    private fun dfs(node: TreeNode?, maxSoFar: Int): Int {
        node ?: return 0
        
        val newMax = maxOf(maxSoFar, node.`val`)
        val good = if (node.`val` >= maxSoFar) 1 else 0

        return good + dfs(node.left, newMax) + dfs(node.right, newMax)
    }
}