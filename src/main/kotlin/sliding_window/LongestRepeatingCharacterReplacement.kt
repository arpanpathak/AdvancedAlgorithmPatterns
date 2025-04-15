package sliding_window

class LongestRepeatingCharacterReplacement {
    fun characterReplacement(s: String, k: Int): Int {
        val count = IntArray(26)
        var maxLength = 0
        var left = 0
        var maxCount = 0

        for (right in s.indices) {
            val char = s[right]
            count[char - 'A']++
            maxCount = maxOf(maxCount, count[char - 'A'])

            if (right - left + 1 - maxCount > k) {
                count[s[left] - 'A']--
                left++
            }

            maxLength = maxOf(maxLength, right - left + 1)
        }

        return maxLength
    }
}
