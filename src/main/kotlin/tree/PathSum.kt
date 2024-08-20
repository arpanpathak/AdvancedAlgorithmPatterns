package tree

class PathSum {
    fun hasPathSum(root: TreeNode?, targetSum: Int): Boolean {
        return when {
            root == null -> false
            root.left == null && root.right == null && targetSum == root.`val` -> true
            else -> hasPathSum(root.left, targetSum - root.`val`)
                    || hasPathSum(root.right, targetSum - root.`val`)
        }
    }
}