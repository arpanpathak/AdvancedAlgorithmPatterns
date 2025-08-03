package string.pattern_matching

import oracle.net.aso.m

class FindTheIndexOfTheFirstOccurrenceInString_RabinKarp {
    fun strStr(haystack: String, needle: String): Int {
        if (needle.isEmpty()) return 0
        if (haystack.length < needle.length) return -1

        val base = 26
        val mod = 1_000_000_007
        val m = needle.length
        var targetHash = 0L
        var windowHash = 0L
        var power = 1L

        // Precompute needle hash and initial window hash
        for (i in 0 until m) {
            targetHash = (targetHash * base + charValue(needle[i])) % mod
            windowHash = (windowHash * base + charValue(haystack[i])) % mod
            if (i < m - 1) power = (power * base) % mod
        }

        // Early check for match at index 0
        if (windowHash == targetHash && matches(haystack, needle, 0)) {
            return 0
        }

        // Slide the window and update hash
        for (i in m until haystack.length) {
            // Remove leftmost character and add new character
            windowHash = (windowHash - charValue(haystack[i - m]) * power % mod + mod) % mod
            windowHash = (windowHash * base + charValue(haystack[i])) % mod

            // Check for match
            val startIndex = i - m + 1
            if (windowHash == targetHash && matches(haystack, needle, startIndex)) {
                return startIndex
            }
        }

        return -1
    }

    private fun charValue(c: Char): Int = c - 'a'

    private fun matches(text: String, pattern: String, start: Int): Boolean {
        for (i in pattern.indices) {
            if (text[start + i] != pattern[i]) return false
        }
        return true
    }
}
