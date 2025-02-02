package tree

class BinaryTreeRightSideView {
    fun rightSideView(root: TreeNode?): List<Int> {
        val rightSide = mutableListOf<Int>()

        fun dfs(node: TreeNode?, level: Int) {
            when {
                node == null -> return
                level == rightSide.size -> rightSide.add(node.`val`)
            }

            dfs(node?.right, level + 1)
            dfs(node?.left, level + 1)
        }
        dfs(root, 0)
        return rightSide
    }
}
