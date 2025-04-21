package tree

class LongestPathWithDifferentAdjacentCharacters {
    fun longestPath(parent: IntArray, s: String): Int {
        val children = Array(parent.size) { mutableListOf<Int>() }
        for (i in 1 until parent.size) {
            children[parent[i]].add(i)
        }

        var maxLength = 1

        fun dfs(node: Int): Int {
            var maxDepth = 1 // At least the node itself

            for (child in children[node]) {
                val childDepth = dfs(child)
                if (s[child] != s[node]) {
                    maxLength = maxOf(maxLength, maxDepth + childDepth)
                    maxDepth = maxOf(maxDepth, childDepth + 1)
                }
            }

            return maxDepth
        }

        dfs(0)
        return maxLength
    }
}