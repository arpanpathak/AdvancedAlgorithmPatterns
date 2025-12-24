// Time Complexity: O(N⋅L+N⋅C^L)
// Space Complexity: O(NL)
class WorkSquare {
    class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        val wordIndices = mutableListOf<Int>()
    }

    fun wordSquares(words: Array<String>): List<List<String>> {
        val root = TrieNode()
        words.forEachIndexed { index, word ->
            var curr = root
            for (char in word) {
                curr = curr.children.getOrPut(char) { TrieNode() }
                curr.wordIndices.add(index)
            }
        }

        val result = mutableListOf<List<String>>()
        val n = words[0].length

        fun backtrack(currentSquare: MutableList<String>) {
            if (currentSquare.size == n) {
                result.add(ArrayList(currentSquare))
                return
            }

            val prefix = StringBuilder()
            for (i in 0 until currentSquare.size) {
                prefix.append(currentSquare[i][currentSquare.size])
            }

            val prefixString = prefix.toString()
            var node = root
            for (char in prefixString) {
                node = node.children[char] ?: return
            }

            for (candidateIdx in node.wordIndices) {
                currentSquare.add(words[candidateIdx])
                backtrack(currentSquare)
                currentSquare.removeLast()
            }
        }

        for (word in words) {
            backtrack(mutableListOf(word))
        }

        return result
    }
}

/**
 *
 *
 *       root
    /   |   \   \
    a    l    b   w
    |   / \   |   |
    r  e   a  a   a
    |  |   |  |   |
    e  a   d  l   l
    |  |   |  |   |
    a  d   y  l   l
 *
 *   backtrack( [ball] )
 *
 * Input: words = ["area","lead","wall","lady","ball"]
    "b a l l",
    "a r e a",
    "l e a d","
    "l a d y"
=======================
   ["w a l l",
    "a r e a",
    "l e a d",
    "l a d y"
   ]
]
 */

/**
 * Input: words = ["wall", "ball", "area", "abat", "lead", "lean", "lady", "land"]
 * * root
 * /  |  \
 * b   a   l
 * |   |  / \
 * a   r e   a
 * / \  | |  / \
 * s   l e a d   n
 * |   | | | |   |
 * t   l a b y   d
 * * backtrack( [ball] )
 * * Input: words = ["ball", "area", "lead", "lady", "lean", "land"]
 * [
 * "b a l l",
 * "a r e a",
 * "l e a d",
 * "l a d y"
 * ]
 * =======================
 * [
 * "b a l l",
 * "a r e a",
 * "l e a n",
 * "l a n d"
 * ]
 * =======================
 * [
 * "w a l l",
 * "a r e a",
 * "l e a d",
 * "l a d y"
 * ]
 */