package string.greedy

class BreakAPalindrome {
    fun breakPalindrome(palindrome: String): String {
        if (palindrome.length == 1) return ""

        val arr = palindrome.toCharArray()
        for (i in 0 until palindrome.length / 2) {
            if (arr[i] != 'a') {
                arr[i] = 'a'
                return String(arr)
            }
        }
        // If all characters are 'a', change the last character to 'b'
        arr[arr.lastIndex] = 'b'
        return String(arr)
    }
}