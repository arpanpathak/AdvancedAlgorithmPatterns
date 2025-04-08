package tree

class ConstructBinaryTreeFromInorderAndPostOrderTraversal {
    fun buildTree(inorder: IntArray, postorder: IntArray): TreeNode? {
        // Build Inverted InOrder Index Map
        val rootIndices = mutableMapOf<Int, Int>()
        inorder.forEachIndexed { index, value -> rootIndices[value] = index }

        var postIndex = postorder.size - 1

        fun buildTree(left: Int, right: Int): TreeNode? {
            return when {
                left > right -> null
                else -> {
                    val rootVal = postorder[postIndex--]
                    TreeNode(rootVal).apply {
                        this.right = buildTree(rootIndices[rootVal]!! + 1, right)  // Build right subtree first
                        this.left = buildTree(left, rootIndices[rootVal]!! - 1)    // Build left subtree
                    }
                }
            }
        }

        return buildTree(0, inorder.size - 1)
    }
}
