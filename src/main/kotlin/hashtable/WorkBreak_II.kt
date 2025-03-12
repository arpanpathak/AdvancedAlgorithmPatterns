package hashtable

class WorkBreak_II {
    fun wordBreak(s: String, wordDict: List<String?>?): List<String> {
        val set = wordDict?.toSet() ?: emptySet()
        val result = mutableListOf<String>()

        fun permute(i: Int, current: StringBuilder) {
            if (i == s.length) {
                result.add(current.toString().trim())
                return
            }
            for (j in i until s.length) {
                val prefix = s.substring(i, j + 1)
                if (set.contains(prefix)) {
                    val originalLength = current.length
                    current.append(prefix).append(" ")
                    permute(j + 1, current)
                    current.setLength(originalLength) // Backtrack
                }
            }
        }

        permute(0, StringBuilder())  // Call the permute function starting at index 0
        return result
    }
}
