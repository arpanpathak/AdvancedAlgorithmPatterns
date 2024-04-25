package tree

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

data class Result (val sum: Int, val count: Int)

class CountNodeEqualsAverage {
    var count = 0

    fun averageOfSubtree(root: TreeNode?): Int {
        postOrder(root)
        return count;
    }

    private fun postOrder(root: TreeNode?): Result {
        if (root == null)
            return Result(sum = 0, count = 0)

        val left = postOrder(root.left)
        val right = postOrder(root.right)

        val nodeSum = root.`val` + left.sum + right.sum
        val nodeCount = 1 + left.count + right.count
        if ( root.`val` == nodeSum/nodeCount)
            count++

        return Result(sum = nodeSum, count = nodeCount)
    }
}