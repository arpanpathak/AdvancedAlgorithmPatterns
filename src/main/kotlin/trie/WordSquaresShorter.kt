package trie

class WordSquaresShorter {
    class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        val wordIndices = mutableListOf<Int>()
    }

    fun wordSquares(words: Array<String>): List<List<String>> {
        val root = TrieNode()
        words.forEachIndexed { index, word ->
            var curr = root
            word.forEach { char -> curr = curr.children.getOrPut(char) { TrieNode() }.apply { wordIndices += index } }
        }

        val results = mutableListOf<List<String>>()
        val n = words[0].length

        fun backtrack(square: MutableList<String>) {
            if (square.size == n) return results.add(ArrayList(square)).let {}

            val colIndex = square.size
            var possibleNextWordNode: TrieNode? = root

            for (rowIndex in 0 until colIndex) {
                possibleNextWordNode = possibleNextWordNode?.children?.get(square[rowIndex][colIndex])
            }

            possibleNextWordNode?.wordIndices?.forEach {
                square += words[it]
                backtrack(square)
                square.removeAt(square.lastIndex)
            }
        }

        words.forEach { backtrack(mutableListOf(it)) }
        return results
    }
}