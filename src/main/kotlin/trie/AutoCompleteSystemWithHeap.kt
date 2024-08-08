package trie

class AutoCompleteSystemWithHeap(sentences: Array<String>, times: IntArray) {
    inner class TrieNode {
        val children = Array<TrieNode?>(26) { null }
        val suggestions = mutableListOf<String>()
    }

    val root = TrieNode()

    init {
        sentences.forEachIndexed { index, word ->
            insert(word, times[index])
        }
    }

    fun insert(word: String, hotness: Int) {
        var currentNode = root

        word.forEach { ch ->
            val index = ch - 'a'
            if (currentNode.children[index] == null) {
                currentNode.children[index] = TrieNode()
            }
            currentNode = currentNode.children[index]!!
            if (currentNode.suggestions.size < 3) {
                currentNode.suggestions.add(word)
            }
        }
    }

    fun search(prefix: String): List<String> {
        var currentNode = root
        val result = mutableListOf<String>()

        prefix.forEach { ch ->
            val index = ch - 'a'
            if (currentNode.children[index] == null) {
                return result
            }
            currentNode = currentNode.children[index]!!
        }

        // If we reach here, currentNode is the node representing the end of the prefix
        return currentNode.suggestions.take(3)
    }

    fun suggestedProducts(searchWord: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val prefixSearchString = StringBuilder()

        searchWord.forEach { ch ->
            prefixSearchString.append(ch)
            result.add(search(prefixSearchString.toString()))
        }

        return result
    }
}