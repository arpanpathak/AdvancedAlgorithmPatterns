package tree

class MaximumProductOfSplittedBinaryTree {
    fun maxProduct(root: TreeNode?): Int {
        val sums = mutableListOf<Long>()
        var maxProd = 0L

        fun calculateSums(node: TreeNode?): Long {
            node ?: return 0L

            // Post order traversal
            val currentSum = node.`val` + calculateSums(node.left) + calculateSums(node.right)
            sums.add(currentSum)
            return currentSum
        }

        val totalSum = calculateSums(root)
        sums.forEach { subtreeSum ->
            val product = subtreeSum * (totalSum - subtreeSum)
            if (product > maxProd) maxProd = product
        }

        return (maxProd % 1_000_000_007).toInt()
    }
}