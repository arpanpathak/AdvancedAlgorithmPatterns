package string.hashtable

class PermutationsInString {
    fun checkInclusion(s1: String, s2: String): Boolean {
        val targetFreq = IntArray(26)
        val windowFreq = IntArray(26)

        for(c in s1) targetFreq[c - 'a']++

        for (i in s2.indices) {
            windowFreq[s2[i] - 'a']++
            if (i >= s1.length) {
                windowFreq[s2[i - s1.length] - 'a']--
            }
            if (targetFreq.contentEquals(windowFreq))
                return true
        }

        return false
    }
}
