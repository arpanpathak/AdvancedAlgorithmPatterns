package tree.bst

class UniqueBinarySearchTrees_II {
    fun generateTrees(n: Int): List<TreeNode?> {
        if ( n == 0) return emptyList()

        fun construct(start: Int, end: Int): List<TreeNode?> {
            when {
                start > end -> return listOf(null)
                start == end -> return listOf(TreeNode(start))
            }

            val allTrees = mutableListOf<TreeNode?>()

            for (i in start..end) {
                val leftSubtrees = construct(start, i - 1)
                val rightSubtrees = construct(i + 1, end)

                for (leftRoot in leftSubtrees) {
                    for (rightRoot in rightSubtrees) {
                        allTrees.add(TreeNode(i).apply {
                            left = leftRoot
                            right = rightRoot
                        })
                    }
                }
            }

            return allTrees
        }

        return construct(1, n)
    }
}
