package tree

class LeafSimilar {
    fun leafSimilar(root1: TreeNode?, root2: TreeNode?): Boolean {
        val leaves1 = mutableListOf<Int>()
        val leaves2 = mutableListOf<Int>()

        dfs(root1, leaves1)
        dfs(root2, leaves2)

        return leaves1 == leaves2
    }

    fun dfs(node: TreeNode?, leafValues: MutableList<Int>) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                leafValues.add(node.`val`)
            }
            dfs(node.left, leafValues)
            dfs(node.right, leafValues)
        }
    }
}
