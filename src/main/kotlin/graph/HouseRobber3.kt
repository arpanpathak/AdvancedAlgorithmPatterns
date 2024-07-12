package graph

import kotlin.math.max

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class HouseRobber3 {
    fun rob(root: TreeNode?): Int {
        fun dfs (node: TreeNode?): IntArray {
            if (node == null)
                return intArrayOf(0 , 0)


            val (left, right) = arrayOf( dfs(node?.left), dfs(node?.right) )

            // If we rob this house then we can't rob children
            val toRob = node.`val` + left[1] + right[1]

            // If we don't rob then we could rob children and add them up
            val notToRob = maxOf(left[0], left[1]) + max(right[0], right[1])

            return intArrayOf(toRob, notToRob)
        }

        val result = dfs(root)

        return result.max()
    }

}