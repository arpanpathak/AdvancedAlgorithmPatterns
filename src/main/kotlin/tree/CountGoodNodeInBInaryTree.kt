package tree

class CountGoodNodeInBInaryTree {
    fun goodNodes(root: TreeNode?): Int {
        return dfs(root, Int.MIN_VALUE)
    }

    fun dfs(root: TreeNode?, prevMax: Int): Int {
        if (root == null)
            return 0

        var count = 0
        val currentMax = maxOf(prevMax, root.`val`)

        if (root.`val` >= prevMax )
            count = 1

        count += dfs(root.left, currentMax) + dfs(root.right, currentMax)
        return count

    }
}