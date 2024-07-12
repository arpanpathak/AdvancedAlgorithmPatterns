package tree.bst

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class DeleteNodeinABST {
    fun deleteNode(root: TreeNode?, key: Int): TreeNode? {
        when {
            root == null -> return null
            key < root.`val` -> root.left = deleteNode(root.left, key)
            key > root.`val` -> root.right = deleteNode(root.right, key)
            else -> {
                when {
                    root.left == null -> return root.right
                    root.right == null -> return root.left
                    else -> {
                        root.`val` = minValue(root.right!!)
                        root.right = deleteNode(root.right, root.`val`)
                    }
                }
            }
        }

        return root
    }

    fun minValue(node: TreeNode): Int {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }

        return current.`val`
    }
}