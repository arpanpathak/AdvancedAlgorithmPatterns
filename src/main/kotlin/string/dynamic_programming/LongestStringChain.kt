package string.dynamic_programming

class LongestStringChain {
    fun longestStrChain(words: Array<String>): Int {
        words.sortBy { it.length }  // Sort words by length
        val dp = mutableMapOf<String, Int>()
        var maxLen = 1

        for (word in words) {
            dp[word] = 1  // Default length of chain ending at word is 1
            for (i in word.indices) {
                val prev = word.removeRange(i, i + 1)  // Remove one character
                if (prev in dp) {
                    dp[word] = maxOf(dp[word]!!, dp[prev]!! + 1)
                }
            }
            maxLen = maxOf(maxLen, dp[word]!!)
        }

        return maxLen
    }
}
