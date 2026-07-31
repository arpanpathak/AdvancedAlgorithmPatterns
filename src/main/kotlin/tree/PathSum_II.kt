package tree

class PathSum_II {
    fun pathSum(root: TreeNode?, targetSum: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun dfs(node: TreeNode?, currentSum: Int, path: MutableList<Int> = mutableListOf()) {
            if (node == null) return

            // Add the current node's value to the path
            path.add(node.`val`)

            // If the node is a leaf and the path sum equals targetSum, add the path to the result
            if (node.left == null && node.right == null && currentSum == node.`val`) {
                result.add(path.toList()) // copy the current path list and add
            } else {
                // Continue the search on the left and right subtree
                dfs(node.left, currentSum - node.`val`, path)
                dfs(node.right, currentSum - node.`val`, path)
            }

            // Backtrack: remove the current node from the path
            path.removeAt(path.size - 1)
        }

        dfs(root, targetSum)
        return result
    }

    fun pathSumClean(root: TreeNode?, targetSum: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val currentPath = mutableListOf<Int>()

        fun isLeaf(node: TreeNode?) = node?.left == null && node?.right == null

        fun dfs(node: TreeNode?, remainingSum: Int) {
            node ?: return

            currentPath.add(node.`val`)
            val newSum = remainingSum - node.`val`

            when {
                newSum == 0 &&  isLeaf(node) -> result.add(currentPath.toList())
                else ->   {
                    dfs(node.left, newSum);
                    dfs(node.right, newSum)
                }
            }
            // Backtrack once the DFS is done
            currentPath.removeLast()
        }

        dfs(root, targetSum).also { return result }
    }
}