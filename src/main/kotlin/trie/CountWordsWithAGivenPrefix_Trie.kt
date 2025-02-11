package trie

class CountWordsWithAGivenPrefix_Trie {
    data class TrieNode(
        val ch: Char = '_',
        val children: MutableMap<Char, TrieNode> = mutableMapOf(),
        var prefixCount: Int = 0
    )

    var root = TrieNode()

    // Method to count the words with the given prefix
    fun prefixCount(words: Array<String>, prefix: String): Int {
        // Build the trie using the words
        buildTrie(words, root)

        // Traverse the trie to find the node corresponding to the given prefix
        return findPrefixNode(prefix)?.prefixCount ?: 0
    }

    // Method to build the trie
    private fun buildTrie(words: Array<String>, root: TrieNode) {
        for (word in words) {
            var current = root
            for (ch in word) {
                // Move to the child node or create a new one
                current = current.children.getOrPut(ch) { TrieNode(ch) }
                current.prefixCount++  // Increment prefix count for every node along the way
            }
        }
    }

    // Method to find the node corresponding to the last character of the prefix
    private fun findPrefixNode(prefix: String): TrieNode? {
        var current = root
        for (ch in prefix) {
            current = current.children[ch] ?: return null  // If prefix doesn't exist, return null
        }
        return current
    }
}
