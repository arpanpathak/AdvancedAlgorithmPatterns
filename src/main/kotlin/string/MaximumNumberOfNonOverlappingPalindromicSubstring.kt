package string

class MaximumNumberOfNonOverlappingPalindromicSubstring {
    fun maxPalindromes(s: String, k: Int): Int {
        var count = 0
        val n = s.length
        var lastEnd = -1

        for (i in 0 until n) {
            // 1. Odd-length expansion: 'i' is the single character center
            val oddEnd = expandFromCenter(s, i, i, k, lastEnd)

            // 2. Even-length expansion: center is the gap between 'i' and 'i + 1'
            val evenEnd = expandFromCenter(s, i, i + 1, k, lastEnd)

            // Find the earliest valid ending position from this center
            val endPos = when {
                oddEnd != -1 && evenEnd != -1 -> minOf(oddEnd, evenEnd)
                oddEnd != -1 -> oddEnd
                else -> evenEnd
            }

            // If a valid palindrome was found, lock it in and update our boundary
            if (endPos != -1) {
                count++
                lastEnd = endPos
            }
        }
        return count
    }

    // Expands outward. Returns the earliest ending index 'r' where length >= k
    // AND the left index 'l' has completely cleared 'lastEnd'.
    private fun expandFromCenter(s: String, left: Int, right: Int, k: Int, lastEnd: Int): Int {
        var l = left
        var r = right
        val n = s.length

        // If the left boundary hits or goes behind lastEnd, expanding further
        // outward will only make 'l' even smaller, so we stop immediately.
        while (l >= 0 && l > lastEnd && r < n && s[l] == s[r] ) {
            val length = r - l + 1
            if (length >= k) {
                return r // Take the minimal valid length to minimize the ending boundary
            }
            l--
            r++
        }
        return -1
    }
}