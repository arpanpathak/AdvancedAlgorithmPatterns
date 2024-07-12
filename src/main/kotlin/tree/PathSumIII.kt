package tree

class PathSumIII {
    // O(N^2) solution
    fun pathSum(root: TreeNode?, targetSum: Int): Int {
        if (root == null)
            return 0

        // Count paths with targetSum starting from the current node
        fun dfs(node: TreeNode?, currentSum: Long): Int {
            if (node == null)
                return 0

            val newSum = currentSum + node.`val`
            val count = if (newSum == targetSum.toLong()) 1 else 0

            return count + dfs(node.left, newSum) + dfs(node.right, newSum)
        }

        // Start counting paths from the root
        return dfs(root, 0L) + pathSum(root.left, targetSum) + pathSum(root.right, targetSum)
    }


    // Revisit I didn't understand
    fun pathSum_prefix_sum(root: TreeNode?, targetSum: Int): Int {
        // HashMap to store prefix sums and their frequencies
        val prefixSumCount = HashMap<Long, Int>()
        // Initialize with a prefix sum of 0 having occurred once (considering no node)
        prefixSumCount[0L] = 1

        // Helper function to perform DFS
        fun dfs(node: TreeNode?, currentSum: Long): Int {
            if (node == null) return 0

            // Calculate the current sum at the current node
            val newSum = currentSum + node.`val`

            // Calculate how many valid paths end at the current node
            var pathCount = prefixSumCount.getOrDefault(newSum - targetSum.toLong(), 0)

            // Update prefix sum count
            prefixSumCount[newSum] = prefixSumCount.getOrDefault(newSum, 0) + 1

            // Recursively count paths in the left and right subtrees
            pathCount += dfs(node.left, newSum)
            pathCount += dfs(node.right, newSum)

            // Backtrack: Remove the current prefix sum to avoid influencing other paths
            prefixSumCount[newSum] = prefixSumCount.getOrDefault(newSum, 0) - 1

            return pathCount
        }

        // Start DFS traversal from the root with initial current sum of 0
        return dfs(root, 0L)
    }
}
