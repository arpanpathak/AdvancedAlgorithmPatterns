package tree

class DiameterOfNArrayTree {
    class Node {
        val children: List<Node?> = listOf()
    }

    fun diameter(root: Node?): Int {
        var maxDiameter = 0

        fun dfs(node: Node?): Int {
            if (node == null) return 0
            var maxDepth = 0
            var secondMaxDepth = 0

            for (child in node.children) {
                val depth = dfs(child)
                if (depth > maxDepth) {
                    secondMaxDepth = maxDepth
                    maxDepth = depth
                } else if (depth > secondMaxDepth) {
                    secondMaxDepth = depth
                }
            }

            maxDiameter = maxOf(maxDiameter, maxDepth + secondMaxDepth)
            return maxDepth + 1
        }

        dfs(root)
        return maxDiameter
    }
}
