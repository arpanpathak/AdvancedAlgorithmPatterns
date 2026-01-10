package tree.bst

import java.util.TreeMap

/**
 * ==============================================================================
 * PROBLEM STATEMENT: Real-Time Streamer Leaderboard
 * ==============================================================================
 * Design a leaderboard system for a streaming platform with millions of users.
 * View counts (scores) update frequently (thousands of updates/sec).
 *
 * Requirements:
 * 1. updateScore(id, score):
 * - Update the viewer count for a specific streamer.
 * - If the streamer doesn't exist, add them.
 * - Must be efficient (Target: O(log N)).
 *
 * 2. getStreamerAtRank(k):
 * - Return the Streamer ID at the k-th rank (1-indexed).
 * - Rank 1 is the highest score.
 * - Must be efficient (Target: O(log N) or O(N) depending on constraints).
 *
 * 3. getRank(id):
 * - Return the current rank of a specific streamer.
 * - Must be efficient.
 *
 * Constraints:
 * - High concurrency.
 * - Scores are non-unique (ties must be handled).
 * ==============================================================================
 */
class Leaderboard {
    // Maps StreamerID -> Score for O(1) lookup
    private val scores = mutableMapOf<String, Int>()

    // Maps Score -> Set of StreamerIDs (Ordered by Score Descending)
    // NOTE: In a perfect world, this would be an Order Statistic Tree for O(log N) rank queries.
    // Using TreeMap here gives O(log N) updates but O(N) for rank queries.
    private val tree = TreeMap<Int, MutableSet<String>>(compareByDescending { it })

    fun updateScore(id: String, newScore: Int) {
        // 1. Remove the old score entry if it exists
        scores[id]?.let { oldScore ->
            tree[oldScore]?.let { streamers ->
                streamers.remove(id)
                if (streamers.isEmpty()) tree.remove(oldScore)
            }
        }

        // 2. Update the lookup map
        scores[id] = newScore

        // 3. Insert into the tree
        tree.getOrPut(newScore) { mutableSetOf() }.add(id)
    }

    fun getStreamerAtRank(k: Int): String? {
        var countSoFar = 0

        // Iterate through scores (High -> Low)
        for ((score, streamers) in tree) {
            // Check if the k-th rank falls within this score bucket
            if (countSoFar + streamers.size >= k) {
                // Determine index within the bucket (k is 1-based)
                // index = k - (count before this bucket) - 1
                return streamers.elementAt(k - countSoFar - 1)
            }
            countSoFar += streamers.size
        }

        return null // k is out of bounds
    }
}
