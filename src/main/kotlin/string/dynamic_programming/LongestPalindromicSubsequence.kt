package string.dynamic_programming

class LongestPalindromicSubsequence {
    fun longestPalindromeSubseq(s: String): Int {
        val dp = Array(s.length) { IntArray(s.length) }

        fun lps(start: Int, length: Int): Int {
            if (length <= 1) return length
            val end = start + length - 1

            return when {
                dp[start][end] != 0 -> dp[start][end] // Value already cached
                s[start] == s[end] -> 2 + lps(start + 1, length - 2)
                else -> maxOf(lps(start + 1, length - 1 ), lps(start, length - 1))
            }.also { dp[start][end] = it}
        }
        return lps(0, s.length)
    }
}