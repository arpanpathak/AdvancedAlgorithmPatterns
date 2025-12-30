package google

import java.lang.Math.abs

/**
 * Kinda same as https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/
 * --- THE PROBLEM: THE GREAT TOWN SPLIT ---
 * * Imagine a kingdom of N regions connected by roads. Since it's a "tree,"
 * there are no loops—every region is connected, but only by the bare minimum
 * number of roads (N-1).
 *
 * Each region has a certain number of towns (population).
 *
 * THE GOAL:
 * You need to pick EXACTLY ONE road to destroy. This will split the kingdom
 * into two separate islands. Your job is to pick the road that makes the
 * populations of these two islands as close as possible. We want to find
 * the SMALLEST difference between them.
 *
 * --- SAMPLE CASE ---
 * Towns: [10, 20, 15, 5] (Region 1 has 10, Region 2 has 20, etc.)
 * Roads: (1-2), (2-3), (2-4)
 * * Visualization:
 * (10) [Reg 1]
 * |
 * (20) [Reg 2]
 * /    \
 * (15)[3] (5)[4]
 *
 * Total Population = 10 + 20 + 15 + 5 = 50.
 *
 * Potential Splits:
 * 1. Cut road (2-3):
 * - Island A: {Reg 3} = 15
 * - Island B: {Reg 1, 2, 4} = 10 + 20 + 5 = 35
 * - Difference: |15 - 35| = 20
 *
 * 2. Cut road (1-2):
 * - Island A: {Reg 1} = 10
 * - Island B: {Reg 2, 3, 4} = 20 + 15 + 5 = 40
 * - Difference: |10 - 40| = 30
 *
 * RESULT: The minimum difference is 20.
 */
fun minTownsDiff(n: Int, towns: IntArray, roads: Array<IntArray>): Long {
    val adj = Array(n + 1) { mutableListOf<Int>() }
    roads.forEach { (u, v) ->
        adj[u].add(v)
        adj[v].add(u)
    }

    // Use Long to prevent overflow, as town counts can be large
    val totalSum = towns.sumOf { it.toLong() }
    var minDifference = Long.MAX_VALUE

    // Returns the sum of the subtree rooted at 'curr'
    fun dfs(curr: Int, parent: Int): Long {
        // towns[curr - 1] because nodes are 1-indexed
        var currentSubtreeSum = towns[curr - 1].toLong()

        for (neighbor in adj[curr]) {
            if (neighbor != parent) {
                val childSubtreeSum = dfs(neighbor, curr)

                // If we cut the edge between 'curr' and 'neighbor':
                val diff = abs(totalSum - 2 * childSubtreeSum)
                minDifference = minOf(minDifference, diff)

                currentSubtreeSum += childSubtreeSum
            }
        }
        return currentSubtreeSum
    }

    dfs(1, -1)
    return minDifference
}