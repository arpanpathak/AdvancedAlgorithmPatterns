package string

class MergeStringAlternatively {
    fun mergeAlternately(word1: String, word2: String): String {
        val mergedString = StringBuilder()

        for (i in 0 until maxOf(word1.length, word2.length)) {
            if (i < word1.length)
                mergedString.append(word1[i])
            if (i < word2.length)
                mergedString.append(word2[i])
        }

        return mergedString.toString()
    }
}