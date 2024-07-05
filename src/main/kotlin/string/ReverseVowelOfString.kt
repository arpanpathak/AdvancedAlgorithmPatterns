package string

class ReverseVowelOfString {
    fun reverseVowels(s: String): String {
        val vowels = setOf('a', 'e', 'i', 'o', 'u')
        val result = StringBuilder(s)

        var (start, end) = Pair(0, s.lastIndex)

        while (start < end) {
            if (s[start] in vowels && s[end] in vowels) {
                swap (result, start++, end--)
            } else if (s[start].lowercaseChar() !in vowels)  {
                start++
            } else {
                end--
            }
        }

        return result.toString()
    }

    fun swap(s: StringBuilder, i: Int, j: Int) {
        s[j] = s[i].also { s[i] = s[j]}
    }
}