package trie

class WordBreak_I {
    // TrieNode class as a data class
    data class TrieNode(val children: MutableMap<Char, TrieNode> = mutableMapOf(), var endOfWord: Boolean = false)

    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val root = TrieNode()

        // Build the Trie from the word dictionary
        wordDict.forEach { word ->
            var node = root
            word.forEach { char ->
                node = node.children.getOrPut(char) { TrieNode() }
            }
            node.endOfWord = true
        }

        val dp = mutableMapOf<Int, Boolean>().apply  { this[s.length] = true} // Base case: empty suffix can always be segmented

        // Recursive function to check if string s[i..] can be segmented
        fun canSegment(i: Int): Boolean {
            dp[i]?.let {  return it }// Already marked as segmentable
            var node = root

            for (j in i until s.length) {
                node = node.children[s[j]] ?: break // No match found for this character

                when {
                    node.endOfWord && canSegment(j + 1) -> return true.also { dp[i] = it }
                }
            }
            return false.also { dp[i] = it }
        }

        return canSegment(0) // Start from the first character of the string
    }
}
