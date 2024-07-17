package tree

class StepByStepDirectionsFromANodeToAnother {
    fun findNode(root: TreeNode?, searchKey: Int): TreeNode? {
        return when {
            root == null -> null
            root.`val` == searchKey -> root
            else -> findNode(root.left, searchKey) ?: findNode(root.right, searchKey)
        }
    }

    fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
        if (root == null || root === p || root === q) return root

        val left = lowestCommonAncestor(root.left, p, q)
        val right = lowestCommonAncestor(root.right, p, q)

        return if (left != null && right != null) root else left ?: right

    }

    fun getDirections(root: TreeNode?, startValue: Int, destValue: Int): String {
        val sourceNode = findNode(root, startValue)
        val destNode = findNode(root, destValue)

        if (sourceNode == null || destNode == null) {
            return "" // No output for missing source or destination
        }

        val lca = lowestCommonAncestor(root, sourceNode, destNode)

        val sourcePath = StringBuilder()
        val destPath = StringBuilder()

        buildPath(lca, sourceNode, sourcePath)
        buildPath(lca, destNode, destPath)

        val upMoves = "U".repeat(sourcePath.length)
        return "$upMoves${destPath.toString()}"
    }

    private fun buildPath(node: TreeNode?, target: TreeNode?, path: StringBuilder): Boolean {
        if (node == null) return false
        if (node === target) return true

        path.append('L')
        if (buildPath(node.left, target, path)) return true
        path.deleteCharAt(path.length - 1)

        path.append('R')
        if (buildPath(node.right, target, path)) return true
        path.deleteCharAt(path.length - 1)

        return false
    }
}
