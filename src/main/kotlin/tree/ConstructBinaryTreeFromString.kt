package tree

class ConstructBinaryTreeFromString {
    fun str2tree(s: String): TreeNode? {
        if (s.isEmpty()) return null

        return buildTree(s, 0).first // We are only interested in the tree root, not the index
    }

    private fun buildTree(s: String, i: Int): Pair<TreeNode?, Int> {
        if (i >= s.length) return Pair(null, i)

        var currentIndex = i
        val start = currentIndex
        if (s[currentIndex] == '-') currentIndex++  // Handle negative numbers
        while (currentIndex < s.length && s[currentIndex].isDigit()) currentIndex++  // Skip over digits
        val valStr = s.substring(start, currentIndex)
        val root = TreeNode(valStr.toInt())

        // Check for left child (subtree)
        if (currentIndex < s.length && s[currentIndex] == '(') {
            currentIndex++  // Skip '('
            val leftResult = buildTree(s, currentIndex)
            root.left = leftResult.first
            currentIndex = leftResult.second  // Update index after processing left subtree
            if (currentIndex < s.length && s[currentIndex] == ')') currentIndex++  // Skip ')'
        }

        // Check for right child (subtree)
        if (currentIndex < s.length && s[currentIndex] == '(') {
            currentIndex++  // Skip '('
            val rightResult = buildTree(s, currentIndex)
            root.right = rightResult.first
            currentIndex = rightResult.second  // Update index after processing right subtree
            if (currentIndex < s.length && s[currentIndex] == ')') currentIndex++  // Skip ')'
        }

        return root to currentIndex  // Return the node and the updated index
    }
}
