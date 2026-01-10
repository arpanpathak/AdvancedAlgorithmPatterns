package trie

typealias ProductId = Long

object Creativity {

    class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        var isWord = false
        var leafNodeIds = mutableListOf<ProductId>()
    }

    abstract class Trie {
        private val root = TrieNode()

        fun insert(word: String) {
            // fold starts at root and returns the last node reached/created
            word.fold(root) { curr, char ->
                curr.children.getOrPut(char) { TrieNode() }
            }.isWord = true
        }

        fun search(word: String): Boolean =
            word.fold(root as TrieNode?) { curr, char ->
                curr?.children?.get(char)
            }?.isWord ?: false
    }
}
