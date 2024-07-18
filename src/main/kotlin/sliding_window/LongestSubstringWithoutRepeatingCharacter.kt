package sliding_window

class LongestSubstringWithoutRepeatingCharacter {
    fun lengthOfLongestSubstring(s: String): Int {
        val lastIndex = IntArray(256) { -1 }
        var max = 0
        var windowStart = 0

        for (i in s.indices) {
            windowStart = maxOf(windowStart, lastIndex[s[i].code] + 1)
            max = maxOf(max, i - windowStart + 1)
            lastIndex[s[i].code] = i
        }

        return max
    }
}