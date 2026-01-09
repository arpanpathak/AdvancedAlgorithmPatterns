package dynamic_programming

class MinimumCostToMergeStones {
    fun mergeStones(stones: IntArray, k: Int): Int {
        val n = stones.size

        /**
         * WHY (n 1) % (k 1) != 0?
         * Each merge takes K piles and creates 1, reducing total piles by (K 1).
         * To reach 1 pile from N, we must remove exactly (N 1) stones.
         * Therefore, total reduction (N 1) must be a multiple of the reduction step (K 1).
         */
        if ((n - 1) % (k - 1) != 0) return -1

        val prefixSum = IntArray(n + 1)
        stones.forEachIndexed { index, stone -> prefixSum[index + 1] = prefixSum[index] + stone }
        // or use running fold

        /**
         * DP Table Dimensions:
         * i => Start index of the subarray in the original stones array.
         * j => End index of the subarray in the original stones array.
         * m => Current target: the number of piles we want to reduce the range [i to j] into.
         */
        val dp = Array(n) { Array(n) { IntArray(k + 1) { -1 } } }

        fun solve(i: Int, j: Int, m: Int): Int {
            return when {
                // Cached result already present
                dp[i][j][m] != -1 -> dp[i][j][m]

                i == j -> if (m == 1) 0 else Int.MAX_VALUE
                /**
                 * A range can't become 1 pile unless it first becomes K piles.
                 * Once K piles exist, they merge into 1 final pile.
                 * The cost is the sum of all stone weights in [i to j].
                 */
                m == 1 -> (solve(i, j, k) + (prefixSum[j + 1] - prefixSum[i]))
                else -> {
                    var minCost = Int.MAX_VALUE
                    /**
                     * WHY step (k 1)?
                     * Splits [i ..j] into [i .. p] (1 pile) and [p+1..j] (m 1 piles).
                     * Range [i .. p] only becomes 1 pile if length matches 1 + z(k 1).
                     * This divisibility avoids calculating impossible split points.
                     */
                    for (p in i until j step k - 1) {
                        minCost = minOf(minCost, solve(i, p, 1) + solve(p + 1, j, m - 1))
                    }
                    minCost
                }
            }.also { dp[i][j][m] = it }
        }

        /**
         * TIME: O(N^3) - N*N*K states, each with N/K split points.
         * SPACE: O(N^2 * K) - 3D dp table storage.
         */
        return solve(0, n - 1, 1)
    }
}
