package string

class StringCompression_II {
    fun compressedString(word: String): String {
        val compressed = StringBuilder()

        var i = 0

        while (i < word.length) {
            val ch = word[i]
            var count = 0

            while (i < word.length && word[i] == ch && count < 9 ) {
                count++
                i++
            }

            compressed.append(count).append(ch)
        }

        return compressed.toString()
    }
}