package string.dynamic_programming

class ValidPalindrome_III_SpaceOptimized {
    fun isValidPalindrome(s: String, k: Int): Boolean {
        val n = s.length
        val dp = IntArray(n) { 1 } // Initialize with 1 because each character is a palindrome of length 1

        for (i in n - 1 downTo 0) {
            var prev = 0 // Stores the value of dp[i+1][j-1] from the 2D DP approach
            for (j in i + 1 until n) {
                val temp = dp[j] // Store the current dp[j] before updating it
                if (s[i] == s[j]) {
                    dp[j] = 2 + prev // If characters match, add 2 to the LPS length of the inner substring
                } else {
                    dp[j] = maxOf(dp[j], dp[j - 1]) // Otherwise, take the maximum of excluding s[i] or s[j]
                }
                prev = temp // Update prev for the next iteration
            }
        }

        val lpsLength = dp[n - 1] // The length of the LPS for the entire string
        return (n - lpsLength) <= k
    }
}
