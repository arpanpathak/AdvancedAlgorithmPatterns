package graph.bst

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class BinarySearchTreeToGreaterSumTree {

    fun bstToGst(root: TreeNode?): TreeNode? {
        var sum = 0

        // Helper function to perform reverse in-order traversal
        fun dfs (node: TreeNode?) {
            if (node == null)
                return

            dfs(node.right)
            sum += node.`val`
            node.`val` = sum
            dfs(node.left)

        }

        dfs(root)
        return root
    }

}