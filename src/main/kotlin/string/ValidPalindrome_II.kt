package string

class ValidPalindrome_II {
    fun validPalindrome(s: String): Boolean {
        var left = 0
        var right = s.lastIndex

        while (left < right) {
            if (s[left] != s[right]) {
                return isPalindrome(s, left, right - 1) || isPalindrome(s, left + 1, right)
            }

            left++
            right--
        }

        return true
    }

    fun isPalindrome(s: String, i: Int, j: Int): Boolean {
        var left = i
        var right = j

        while (left < right) {
            if (s[left++] != s[right--]) {
                return false
            }
        }

        return true
    }
}