package trie

class DesignAddAndSearchWordDataStructure {
    class WordDictionary {
        private data class TrieNode(
            val children: MutableMap<Char, TrieNode> = mutableMapOf(),
            var isEnd: Boolean = false
        )

        private val root = TrieNode()

        fun addWord(word: String) {
            var current = root
            for (ch in word) {
                current = current.children.getOrPut(ch) { TrieNode() }
            }
            current.isEnd = true
        }

        fun search(word: String): Boolean {
            fun dfs(index: Int, node: TrieNode): Boolean {
                when {
                    index == word.length -> return node.isEnd
                    word[index] == '.' -> {
                        return node.children.values.any { child ->
                            dfs(index + 1, child)
                        }
                    }
                    else -> {
                        val nextNode = node.children[word[index]] ?: return false
                        return dfs(index + 1, nextNode)
                    }
                }
            }
            return dfs(0, root)
        }
    }
}