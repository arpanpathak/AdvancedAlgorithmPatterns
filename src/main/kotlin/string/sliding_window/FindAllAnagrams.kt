package string.sliding_window

class FindAllAnagrams {
    fun findAnagrams(s: String, p: String): List<Int> {
        val result = mutableListOf<Int>()
        if (s.length < p.length) return result

        val pCount = IntArray(26)
        val sCount = IntArray(26)

        // Initialize frequency counts for p and the first window of s
        for (i in p.indices) {
            pCount[p[i] - 'a']++
            sCount[s[i] - 'a']++
        }

        // Compare the initial window and check every subsequent window
        for (i in p.length..s.length) {
            if (pCount.contentEquals(sCount)) {
                result.add(i - p.length)
            }
            if (i < s.length) {
                sCount[s[i] - 'a']++          // Add new character to the window
                sCount[s[i - p.length] - 'a']-- // Remove the old character from the window
            }
        }

        return result
    }
}