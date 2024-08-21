package tree

class LowestCommonAncestor_III {
    class Node(var `val`: Int) {
        var left: TreeNode? = null
        var right: TreeNode? = null
        var parent: Node? = null
    }

    fun lowestCommonAncestor(p: Node?, q: Node?): Node? {
        var parent1 = p
        var parent2 = q

        while (parent1 != parent2) {
            // Move parent1 upwards, or switch to q if it becomes null
            parent1 = parent1?.parent ?: q
            // Move parent2 upwards, or switch to p if it becomes null
            parent2 = parent2?.parent ?: p
        }

        // parent1 and parent2 are now pointing to the same node, which is the LCA
        return parent1
    }
}

/**
 *       1 <- r
 *      / \
 *     2   3
 *    /\   /\
 *   6 7   8 9 <-l
 *            \
 *            10
 *
 */