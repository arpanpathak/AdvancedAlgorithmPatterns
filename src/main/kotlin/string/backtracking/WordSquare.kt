package string.backtracking

class WordSquare {
    fun wordSquares(words: Array<String>): List<List<String>> {
        if (words.isEmpty()) return emptyList()

        val l = words[0].length
        val prefixMap = mutableMapOf<String, MutableList<String>>()

        for (word in words) {
            for (i in 1 until l) {
                val prefix = word.substring(0, i)
                prefixMap.computeIfAbsent(prefix) { mutableListOf() }.add(word)
            }
        }

        val results = mutableListOf<List<String>>()

        fun backtrack(step: Int, currentSquare: MutableList<String>) {
            if (step == l) {
                results.add(ArrayList(currentSquare))
                return
            }

            val prefixBuilder = StringBuilder()
            for (word in currentSquare) {
                prefixBuilder.append(word[step])
            }
            val prefix = prefixBuilder.toString()

            val candidates = if (prefix.isEmpty()) {
                words.toList()
            } else {
                prefixMap[prefix] ?: emptyList()
            }

            for (candidate in candidates) {
                currentSquare.add(candidate)
                backtrack(step + 1, currentSquare)
                currentSquare.removeAt(currentSquare.size - 1)
            }
        }

        backtrack(0, mutableListOf())
        return results
    }
}