package tree

class MaximumLevelSumOfABinaryTreee {
    fun maxLevelSum(root: TreeNode?): Int {
        if (root == null) return 0

        var maxSum = Int.MIN_VALUE
        var minLevel = 1
        var currentLevel = 0
        val queue = ArrayDeque<TreeNode>().apply { add(root) }

        while (queue.isNotEmpty()) {
            currentLevel++
            var levelSum = 0

            repeat(queue.size) {
                queue.removeFirst().run {
                    levelSum += `val`
                    left?.let(queue::add)
                    right?.let(queue::add)
                }
            }

            if (levelSum > maxSum) {
                maxSum = levelSum
                minLevel = currentLevel
            }
        }

        return minLevel
    }
}
