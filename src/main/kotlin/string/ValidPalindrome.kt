package string

class ValidPalindrome {
    fun isPalindrome(s: String): Boolean {
        var left = 0
        var right = s.lastIndex
        val isAlpha = {ch: Char -> Character.isAlphabetic(ch.code) || Character.isDigit(ch.code)}

        while (left < right) {
            when {
                isAlpha(s[left]) && isAlpha(s[right])
                        && s[left].lowercaseChar() != s[right].lowercaseChar() -> return false
                isAlpha(s[left]) && !isAlpha(s[right]) ->  right --
                isAlpha(s[right]) && !isAlpha(s[left]) -> left++
                else -> {
                    left++
                    right--
                }

            }
        }

        return true
    }
}