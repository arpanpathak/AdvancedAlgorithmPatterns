package string

class CountWordsWithAGivenPrefix {
    fun prefixCount(words: Array<String>, prefix: String): Int {
        var count = 0

        for (word in words) {
            if (word.startsWith(prefix)) {
                count++
            }
        }

        return count
    }
}
