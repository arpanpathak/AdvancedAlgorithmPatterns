package trie

import java.util.*

class AutoCompleteTrie {
    data class TrieNode(
        var ch: Char='*',
        var hotness: Int = 0,
        var isSentence: Boolean = false,
        var children: TreeMap<Char, TrieNode> = TreeMap()
    )
    data class SearchResultItem(var data: String, val hotness: Int)

    val root = TrieNode()

    fun append(word: String, hotness: Int = 1) {
        var currentNode = root

        word.forEach { ch->
            currentNode = currentNode.children.getOrPut(ch){ TrieNode(ch) }
        }

        currentNode.isSentence = true
        currentNode.hotness += hotness
    }

    fun rank(word: String, limit: Int = 3): List<String> {
        val results = mutableListOf<SearchResultItem>()
        var currentNode = root

        word.forEach { ch -> currentNode = currentNode.children[ch] ?: return results.map{ it.data } }
        // If we reach here, currentNode represents the node ending at 'word'
        // Collect all words from this node downwards
        collectWords(currentNode, word, results)


        return results
            .sortedByDescending { it.hotness } // Sort by hotness descending
            .take(3) // Take the top 3 results
            .map { it.data } // Extract only the data (sentences) from SearchResultItem
    }

    fun collectWords(node: TrieNode?, prefix: String, results: MutableList<SearchResultItem>) {
        if (node == null)
            return
        if (node.isSentence)
            results.add(SearchResultItem(data=prefix, hotness = node.hotness))

        for((ch, childNode) in node.children) {
            collectWords(childNode, prefix + ch, results)
        }
    }
}

class AutocompleteSystem(sentences: Array<String>, times: IntArray) {
    var prefix = StringBuilder()
    var trie = AutoCompleteTrie()

    init {
        sentences.forEachIndexed{ index, word-> trie.append(word, times[index] ) }
    }

    fun input(c: Char): List<String> {

        // If it's a # then time to end the character stream and insert all the buffered characters
        // Also since the sentence is types again, increase it's hotness in the Trie data structure
        if (c == '#') {
            val sentence = prefix.toString()
            trie.append(sentence)

            prefix = StringBuilder()
            return emptyList()
        }

        prefix.append(c)
        return trie.rank(prefix.toString())
    }
}
