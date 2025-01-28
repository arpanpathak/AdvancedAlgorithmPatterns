package tree

class ConstructBinaryTreeFromPreorderAndInOrderTraversal {
    private val rootIndices = mutableMapOf<Int, Int>()

    fun buildTree(preorder: IntArray, inorder: IntArray): TreeNode? {
        // Build Inverted InOrder Index Map
        inorder.forEachIndexed { index, value -> rootIndices[value] = index }
        var rootIndex = 0

        fun buildTree(left: Int, right: Int): TreeNode? {
            return when {
                left > right -> null
                else -> {
                    val rootVal = preorder[rootIndex++]
                    TreeNode(rootVal).apply {
                        this.left = buildTree(left, rootIndices[rootVal]!! - 1)
                        this.right = buildTree(rootIndices[rootVal]!! + 1, right)
                    }
                }
            }
        }

        return buildTree(0, preorder.size - 1)
    }
}
