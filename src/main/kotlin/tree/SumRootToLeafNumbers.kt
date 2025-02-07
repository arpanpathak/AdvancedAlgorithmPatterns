package tree

class SumRootToLeafNumbers {
    fun sumNumbers(root: TreeNode?): Int {
        var sum = 0

        fun dfs(node: TreeNode?, sumSoFar: Int) {
            if (node == null) return
            val newSum = (sumSoFar * 10 + node.`val` )
            when {
                node.left == null && node.right == null -> sum+= newSum
                else ->  {
                    dfs(node.left, newSum)
                    dfs(node.right, newSum)
                }
            }
        }

        dfs(root, 0)
        return sum
    }
}
