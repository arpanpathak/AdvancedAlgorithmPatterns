package array.dp

/**
 * Zero Sum game
 */
fun stoneGame(piles: IntArray): Boolean {
    // Cache stores (i to j) -> Max relative score difference for that range
    val cache = mutableMapOf<Pair<Int, Int>, Int>()

    /**
     * Returns (Current Player's Score - Opponent's Score) for the range [i, j].
     * This "Relative Score" approach avoids needing to track whose turn it is.
     */
    fun pick(i: Int, j: Int): Int = cache.getOrPut(i to j) {
        when (i) {
            // Base case: Only one pile left, the current player takes it all.
            j -> piles[i]

            /**
             * Recursive step:
             * We subtract the opponent's result because the recursive call
             * solve(next_range) returns the advantage for the NEXT player.
             * * Current Player Score = (Taken Pile) - (Opponent's Net Advantage)
             */
            else -> maxOf(
                piles[i] - pick(i + 1, j), // Take left, subtract opponent's net gain
                piles[j] - pick(i, j - 1)  // Take right, subtract opponent's net gain
            )
        }
    }

    // If Alice (first player) has a relative score > 0, she wins.
    return pick(0, piles.lastIndex) > 0
}