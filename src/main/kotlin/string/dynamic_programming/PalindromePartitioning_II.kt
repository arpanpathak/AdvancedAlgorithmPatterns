package string.dynamic_programming

class PalindromePartitioning_II {
    fun minCut(s: String): Int {
        val length = s.length
        val isPalindrome = Array(length) { BooleanArray(length) { false } }
        val minCuts = IntArray(length) { 0 }

        for (end in 0 until length) {
            var currentMinCuts = end // worst case: cut after every character

            for (start in 0..end) {
                // Check if substring from start to end is palindrome
                if (s[start] == s[end] && (end - start <= 2 || isPalindrome[start + 1][end - 1])) {
                    isPalindrome[start][end] = true

                    currentMinCuts = when {
                        start == 0 -> 0 // entire prefix is palindrome
                        else -> minOf(currentMinCuts, minCuts[start - 1] + 1) // cut before this palindrome
                    }
                }
            }
            minCuts[end] = currentMinCuts
        }

        return minCuts[length - 1]
    }
}