package trie

data class TrieNode(
    val char: Char,
    val children: MutableMap<Char, TrieNode> = mutableMapOf(),
    var isWord: Boolean = false
)

class LongestCommonPrefix {
    // Dummy Node as Root Node
    private val trieRoot = TrieNode('_', mutableMapOf())

    private fun insert(str: String) {
        var currentNode = trieRoot
        str.forEach {
            currentNode = currentNode.children.getOrPut(it) { TrieNode(it) }
        }

        currentNode.isWord = true
    }

    /**
     * Not Required. Just to Visualize how the Trie Looks like
     */
    private fun printTrie(node: TrieNode, prefix: String) {
        if (node !== trieRoot) {  // Avoid printing the dummy root node
            val isWord = if (node.isWord) "[#]" else ""
            println("$prefix${node.char}${isWord}")
        }
        node.children.values.forEach { childNode ->
            printTrie(childNode, "$prefix    ")
        }
    }

    fun longestCommonPrefix(strs: Array<String>): String {
        if (strs.isEmpty()) return ""

        // Build the trie from the array of strings
        strs.forEach { insert(it) }

        // Find the longest common prefix
        val prefix = StringBuilder()
        var currentNode = trieRoot
        while (currentNode.children.size == 1 && !currentNode.isWord) {
            val nextNode = currentNode.children.values.first()
            prefix.append(nextNode.char)
            currentNode = nextNode
        }
        return prefix.toString()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val test =   LongestCommonPrefix()
            val input = arrayOf("flower","flow","flight")
            println("Output :- ${test.longestCommonPrefix(input)}")
        }
    }
}