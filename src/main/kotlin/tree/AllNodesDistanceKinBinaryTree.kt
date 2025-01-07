package tree

class AllNodesDistanceKinBinaryTree {
    val parentMap = mutableMapOf<TreeNode, TreeNode?>()  // To store parent references

    fun distanceK(root: TreeNode?, target: TreeNode?, k: Int): List<Int> {
        val result = mutableListOf<Int>()

        // Helper function to perform DFS and populate parent map, then find target
        fun dfs(node: TreeNode?, parent: TreeNode?) {
            if (node == null) return
            parentMap[node] = parent
            if (node == target) collectNodesAtDistanceK(node, k, mutableSetOf(), result)
            dfs(node.left, node)
            dfs(node.right, node)
        }

        // Start DFS from the root to populate parentMap and find the target
        dfs(root, null)
        return result
    }

    // Function to collect nodes at distance K from the target node
    private fun collectNodesAtDistanceK(node: TreeNode?, k: Int, visited: MutableSet<TreeNode>, result: MutableList<Int>) {
        if (node == null || visited.contains(node)) return
        visited.add(node)

        when (k) {
            0 -> result.add(node.`val`)  // If distance is 0, add node's value to result
            else -> {
                collectNodesAtDistanceK(node.left, k - 1, visited, result)
                collectNodesAtDistanceK(node.right, k - 1, visited, result)
                collectNodesAtDistanceK(parentMap[node], k - 1, visited, result)  // Explore parent node
            }
        }
    }
}