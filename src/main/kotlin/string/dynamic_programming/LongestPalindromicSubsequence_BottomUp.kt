package string.dynamic_programming

class LongestPalindromicSubsequence_BottomUp {
    fun longestPalindromeSubseq(s: String): Int {
        val n = s.length
        val dp = Array(n) { IntArray(n) }

        // Bottom-up fill the dp table for substrings of increasing length
        for (length in 1..n) {  // Iterate over all substring lengths
            for (start in 0..n - length) {  // Iterate over all possible start indices
                val end = start + length - 1  // Calculate the end index of the substring

                dp[start][end] = when {
                    length == 1 -> 1  // Base case: single character is a palindrome of length 1
                    s[start] == s[end] -> 2 + dp[start + 1][end - 1]  // Palindrome formed by adding the two matching characters
                    else -> maxOf(dp[start + 1][end], dp[start][end - 1])  // Max of ignoring one end
                }
            }
        }

        // The result is stored in dp[0][n-1] (the entire string)
        return dp[0][n - 1]
    }
}