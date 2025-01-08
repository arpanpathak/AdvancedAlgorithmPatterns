package string.dynamic_programming

class ValidPalindrome_III {
    fun isValidPalindrome(s: String, k: Int): Boolean {
        val dp = Array(s.length) { IntArray(s.length) { 0 } }

        fun lps(start: Int, length: Int): Int {
            val end = start + length - 1
            return when {
                length in 0..1 -> length
                dp[start][end] != 0 -> dp[start][end]
                s[start] == s[end] -> lps(start + 1, length - 2) + 2
                else -> maxOf(lps(start + 1, length - 1), lps(start, length - 1))
            }.also {
                dp[start][end] = it
            }
        }

        // Calculate the length of the longest palindromic subsequence
        val lpsLength = lps(0, s.length)

        // Check if the number of deletions required is less than or equal to k
        return (s.length - lpsLength) <= k
    }
}
