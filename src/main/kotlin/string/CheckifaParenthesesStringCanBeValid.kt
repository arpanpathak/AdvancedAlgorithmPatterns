package string

class CheckifaParenthesesStringCanBeValid {
    fun canBeValid(s: String, locked: String): Boolean {
        // Odd number of brackets
        if (s.length % 2 !=0) return false

        var openCount = 0
        for (i in 0 until s.length) {
            if (s[i] == '(' || s[i] == ')' && locked[i] == '0')
                openCount++
            else openCount--

            if (openCount < 0) return false

        }

        // Second pass: Right to left
        var closeCount = 0
        for (i in s.indices.reversed()) {
            // If it's locked as ')' or can be treated as ')'
            if (s[i] == ')' || (s[i] == '(' && locked[i] == '0')) {
                closeCount++
            } else {
                closeCount--
            }

            // If at any point we have more opening parentheses than closing
            if (closeCount < 0) return false
        }

        // If both counts are valid, the string is balanced
        return true
    }
}
