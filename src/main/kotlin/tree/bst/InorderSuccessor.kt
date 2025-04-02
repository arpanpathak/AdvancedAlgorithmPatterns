package tree.bst

class InorderSuccessor {
    fun inorderSuccessor(root: TreeNode?, p: TreeNode?): TreeNode? {
        var successor: TreeNode? = null
        var current = root

        while (current != null) {
            if (p!!.`val` < current.`val`) {
                // If p's value is less than current, the successor is possibly current
                successor = current
                current = current.left
            } else {
                // If p's value is greater or equal to current, move to the right subtree
                current = current.right
            }
        }

        return successor
    }
}