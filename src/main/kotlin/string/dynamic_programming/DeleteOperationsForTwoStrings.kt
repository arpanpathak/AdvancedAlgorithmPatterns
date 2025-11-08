package string.dynamic_programming

class DeleteOperationsForTwoStrings {
    fun minDistance(word1: String, word2: String): Int {
        // dp[i][j]: Length of the Longest Common Subsequence (LCS)
        val dp = Array(word1.length + 1) { IntArray(word2.length + 1) }

        for (i in 1..word1.length) {
            for (j in 1..word2.length) {
                dp[i][j] = when {
                    // Match: extend LCS from the previous match
                    word1[i - 1] == word2[j - 1] -> dp[i - 1][j - 1] + 1

                    // Mismatch: take the max LCS from dropping a character from either string
                    else -> maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }

        // Return (word1.length - LCS) + (word2.length - LCS)
        return word1.length + word2.length - 2 * dp[word1.length][word2.length]
    }
}
