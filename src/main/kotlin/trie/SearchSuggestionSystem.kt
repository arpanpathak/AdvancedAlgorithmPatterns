package trie

import java.lang.StringBuilder
import java.util.*


class Trie {
    data class TrieNode(
        var ch: Char='*',
        var isWord: Boolean = false,
        var children: TreeMap<Char, TrieNode> = TreeMap()
    )

    val root = TrieNode()

    fun insert(word: String) {
        var currentNode = root

        word.forEach { ch->  currentNode = currentNode.children.getOrPut(ch){ TrieNode(ch) } }

        currentNode.isWord = true
    }

    fun search(word: String): List<String> {
        val results = mutableListOf<String>()
        var currentNode = root

        word.forEach { ch -> currentNode = currentNode.children[ch] ?: return results }
        // If we reach here, currentNode represents the node ending at 'word'
        // Collect all words from this node downwards
        collectWords(currentNode, word, results)

        return results
    }

    fun collectWords(node: TrieNode?, prefix: String, results: MutableList<String>) {
        if (node == null)
            return
        if (node.isWord)
            results.add(prefix)

        for((ch, childNode) in node.children) {
            collectWords(childNode, prefix + ch, results)
        }
    }
}

class SearchSuggestionSystem {
    fun suggestedProducts(products: Array<String>, searchWord: String): List<List<String>> {
        val trie = Trie()
        val result = mutableListOf<List<String>>()
        val prefixSearchString = StringBuilder()

        products.forEach { trie.insert(it) }

        searchWord.forEach { ch ->
            prefixSearchString.append(ch)
            result.add(trie.search(prefixSearchString.toString()).take(3))
        }

        return  result
    }
}