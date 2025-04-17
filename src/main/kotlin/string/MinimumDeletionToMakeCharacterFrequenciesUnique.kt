package string

class MinimumDeletionToMakeCharacterFrequenciesUnique {
    fun minDeletions(s: String): Int {
        val freq = IntArray(26)
        for (ch in s) freq[ch - 'a']++

        val seen = mutableSetOf<Int>()
        var deletions = 0

        for (f in freq) {
            var currentFreq = f
            while (currentFreq > 0 && !seen.add(currentFreq)) {
                currentFreq--
                deletions++
            }
        }

        return deletions
    }
}