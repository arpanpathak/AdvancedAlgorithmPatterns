package string.sliding_window

class MaximumNumberofVowelsinSubstringofGivenLength {
    fun maxVowels(s: String, k: Int): Int {
        var (vowelWindowCount, maxCount) = Pair(0,0)
        val vowels = setOf('a', 'e', 'i', 'o', 'u')

        for (i in 0 until s.length) {

            if (s[i] in vowels ) {
                vowelWindowCount++
            }

            if ( i >= k -1) {
                maxCount = maxOf(maxCount, vowelWindowCount)

                if (s[i - k + 1] in vowels)
                    vowelWindowCount--
            }
        }

        return maxCount
    }
}