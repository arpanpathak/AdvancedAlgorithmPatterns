package simulation

class TextJustification {
    fun fullJustify(words: Array<String>, maxWidth: Int): List<String> {
        val result = mutableListOf<String>()
        var currentLine = mutableListOf<String>()
        var currentLength = 0

        for (word in words) {
            if (currentLength + word.length + currentLine.size > maxWidth) {
                result.add(justifyLine(currentLine, currentLength, maxWidth))
                currentLine = mutableListOf()
                currentLength = 0
            }
            currentLine.add(word)
            currentLength += word.length
        }

        // Add the last line (left-justified)
        result.add(justifyLastLine(currentLine, maxWidth))

        return result
    }

    private fun justifyLine(words: List<String>, currentLength: Int, maxWidth: Int): String {
        if (words.size == 1) {
            return buildString(words[0], maxWidth)
        }

        val totalSpaces = maxWidth - currentLength
        val spaceSlots = words.size - 1
        val evenSpaces = totalSpaces / spaceSlots
        val extraSpaces = totalSpaces % spaceSlots

        val sb = StringBuilder()
        for (i in words.indices) {
            sb.append(words[i])
            if (i < spaceSlots) {
                for (j in 0 until evenSpaces) {
                    sb.append(' ')
                }
                if (i < extraSpaces) {
                    sb.append(' ')
                }
            }
        }

        return sb.toString()
    }

    private fun justifyLastLine(words: List<String>, maxWidth: Int): String {
        val sb = StringBuilder()
        for (i in words.indices) {
            sb.append(words[i])
            if (i < words.size - 1) {
                sb.append(' ')
            }
        }

        while (sb.length < maxWidth) {
            sb.append(' ')
        }

        return sb.toString()
    }

    private fun buildString(word: String, maxWidth: Int): String {
        val sb = StringBuilder(word)
        while (sb.length < maxWidth) {
            sb.append(' ')
        }
        return sb.toString()
    }

}
