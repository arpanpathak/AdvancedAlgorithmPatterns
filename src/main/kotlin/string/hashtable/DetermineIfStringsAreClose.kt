package string.hashtable

class DetermineIfStringsAreClose {
    fun closeStrings(word1: String, word2: String): Boolean {
        if (word1.length != word2.length)
            return false

        val freq1 = mutableMapOf<Char, Int>().apply {
            word1.forEach { ch -> this[ch] = this.getOrDefault(ch, 0) + 1}
        }
        val freq2 = mutableMapOf<Char, Int>().apply {
            word2.forEach { ch -> this[ch] = this.getOrDefault(ch, 0) + 1}
        }

        return when {
            freq1.keys != freq2.keys -> false
            else -> freq1.values.sorted() == freq2.values.sorted()
        }
    }
}