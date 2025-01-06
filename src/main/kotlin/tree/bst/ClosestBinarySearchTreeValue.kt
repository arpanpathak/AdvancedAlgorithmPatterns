package tree.bst

import kotlin.math.abs

class ClosestBinarySearchTreeValue {
    fun closestValue(root: TreeNode?, target: Double): Int {
        var closest = root?.`val` ?: 0
        var current = root

        while (current != null) {
            val currentValue = current.`val`

            // Update closest based on the given conditions:
            if (abs(currentValue - target) < abs(closest - target) ||
                (abs(currentValue - target) == abs(closest - target) && currentValue < closest))
                closest = currentValue

            // Move to the left or right subtree based on the target
            current = if (target < currentValue) current.left else current.right
        }

        return closest
    }
}