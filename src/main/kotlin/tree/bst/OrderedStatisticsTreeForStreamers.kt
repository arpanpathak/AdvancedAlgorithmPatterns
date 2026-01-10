package tree.bst

/**
 * ==============================================================================
 * PROBLEM STATEMENT: Real-Time Streamer Leaderboard
 * ==============================================================================
 * Design a leaderboard system for a streaming platform with millions of users.
 * View counts (scores) update frequently.
 *
 * Requirements:
 * 1. updateScore(id, score): Update a streamer's score efficiently (O(log N)).
 * 2. getStreamerAtRank(k): Return the Streamer ID at the k-th rank.
 * (Rank 1 = Highest Score). Target: O(log N).
 * ==============================================================================
 */
class LeaderboardOrderedStatisticsTree {
    private val scores = mutableMapOf<String, Int>()
    private var root: TreeNode? = null

    class TreeNode(var score: Int, var id: String) {
        var left: TreeNode? = null
        var right: TreeNode? = null
        var size: Int = 1
    }

    fun updateScore(id: String, newScore: Int) {
        // 1. Remove old score if exists
        scores[id]?.let { oldScore ->
            root = delete(root, oldScore)
        }

        // 2. Insert new score
        scores[id] = newScore
        root = insert(root, newScore, id)
    }

    fun getStreamerAtRank(k: Int): String? = findKthLargest(root, k)

    // ------------------------------------------------------------------
    // TREE LOGIC (Using strict `when`)
    // ------------------------------------------------------------------

    private fun insert(node: TreeNode?, score: Int, id: String): TreeNode = when {
        node == null -> TreeNode(score, id)
        else -> {
            when {
                score < node.score -> node.left = insert(node.left, score, id)
                else -> node.right = insert(node.right, score, id)
            }
            // Update size on the way up
            node.size = (node.left?.size ?: 0) + (node.right?.size ?: 0) + 1
            node
        }
    }

    private fun delete(node: TreeNode?, score: Int): TreeNode? = when {
        node == null -> null
        score < node.score -> {
            node.left = delete(node.left, score)
            node.apply { size = (left?.size ?: 0) + (right?.size ?: 0) + 1 }
        }
        score > node.score -> {
            node.right = delete(node.right, score)
            node.apply { size = (left?.size ?: 0) + (right?.size ?: 0) + 1 }
        }
        else -> deleteNode(node) // Found the node to delete
    }

    private fun deleteNode(node: TreeNode): TreeNode? = when {
        node.left == null -> node.right
        node.right == null -> node.left
        else -> {
            // Two children: Swap with successor (Smallest in Right Subtree)
            val successor = minValueNode(node.right!!)
            node.score = successor.score
            node.id = successor.id
            node.right = delete(node.right, successor.score)
            node.apply { size = (left?.size ?: 0) + (right?.size ?: 0) + 1 }
        }
    }

    private fun minValueNode(node: TreeNode): TreeNode = when (node.left) {
        null -> node
        else -> minValueNode(node.left!!)
    }

    // Rank 1 = Highest Score, so we check RIGHT subtree first
    private fun findKthLargest(node: TreeNode?, k: Int): String? {
        if (node == null) return null

        val rightSize = node.right?.size ?: 0

        return when {
            k == rightSize + 1 -> node.id
            k <= rightSize -> findKthLargest(node.right, k)
            else -> findKthLargest(node.left, k - rightSize - 1)
        }
    }
}
