package string

class LengthOfLastWord {
    fun lengthOfLastWord(s: String): Int {
        var i = s.length - 1
        var len = 0

        while (i >= 0 && s[i] == ' ') i--     // skip trailing spaces
        while (i >= 0 && s[i] != ' ') {
            len++
            i--
        }

        return len
    }
}