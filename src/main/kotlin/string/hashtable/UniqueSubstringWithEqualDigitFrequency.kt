package string.hashtable

class UniqueSubstringWithEqualDigitFrequency {
    fun equalDigitFrequency(s: String): Int {
        val n = s.length
        val uniqueSubstrings = mutableSetOf<String>()

        // Precompute prefix frequency arrays
        val prefixFreq = Array(n + 1) { IntArray(10) }
        for (i in 1..n) {
            for (d in 0..9) {
                prefixFreq[i][d] = prefixFreq[i - 1][d]
            }
            prefixFreq[i][s[i - 1] - '0']++
        }

        // Iterate over all possible substrings
        for (i in 0 until n) {
            for (j in i + 1..n) {
                val freq = IntArray(10)
                for (d in 0..9) {
                    freq[d] = prefixFreq[j][d] - prefixFreq[i][d]
                }

                if (hasEqualFrequency(freq)) {
                    uniqueSubstrings.add(s.substring(i, j))
                }
            }
        }

        return uniqueSubstrings.size
    }

    private fun hasEqualFrequency(freq: IntArray): Boolean {
        var uniqueFreq = -1
        for (count in freq) {
            if (count > 0) {
                if (uniqueFreq == -1) {
                    uniqueFreq = count
                } else if (count != uniqueFreq) {
                    return false
                }
            }
        }
        return true
    }
}