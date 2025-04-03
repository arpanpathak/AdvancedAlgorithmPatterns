package string

class LongestPalidnromicSubstring {
    fun longestPalindrome(s: String): String {
        if (s.isEmpty()) return ""

        var start = 0
        var maxLength = 1

        // Helper function to expand around the center
        fun expandAroundCenter(left: Int, right: Int): Int {
            var l = left
            var r = right
            while (l >= 0 && r < s.length && s[l] == s[r]) {
                l--
                r++
            }
            // Return the length of the palindrome
            return r - l - 1
        }

        for (i in 0 until s.length) {
            // Check for odd length palindrome (center is s[i])
            val len1 = expandAroundCenter(i, i)
            // Check for even length palindrome (center is between s[i] and s[i + 1])
            val len2 = expandAroundCenter(i, i + 1)

            val len = maxOf(len1, len2)

            if (len > maxLength) {
                maxLength = len
                start = i - (len - 1) / 2  // Calculate the start of the palindrome
            }
        }

        return s.substring(start, start + maxLength)
    }
}
