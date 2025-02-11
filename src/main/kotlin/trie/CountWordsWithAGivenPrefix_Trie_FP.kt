package trie

class CountWordsWithAGivenPrefix_Trie_FP {
    data class TrieNode(val children: MutableMap<Char, TrieNode> = mutableMapOf(), var prefixCount: Int = 0)

    private val root = TrieNode()

    fun prefixCount(words: Array<String>, prefix: String): Int {
        words.forEach { word ->
            word.fold(root) { node, ch ->
                node.children.getOrPut(ch) { TrieNode() }.apply { prefixCount++ }
            }
        }
        return prefixSearch(prefix)?.prefixCount ?: 0
    }

    private fun prefixSearch(prefix: String): TrieNode? = prefix.fold(root) { node, ch -> node.children[ch] ?: return null }
}
