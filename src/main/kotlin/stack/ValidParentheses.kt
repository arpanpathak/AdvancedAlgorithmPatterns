package stack

class ValidParentheses {
    fun isValid(s: String): Boolean {
        val stack = mutableListOf<Char>()
        val openingBraces = listOf('(', '[', '{')
        for (ch in s) {
            if (ch in openingBraces) {
                stack.add(ch)
                continue
            }

            when {
                stack.isEmpty() -> return false
                ch == ')' -> if(stack.last() == '(') stack.removeLast() else return false
                ch == '}' -> if(stack.last() == '{') stack.removeLast() else return false
                ch == ']' -> if(stack.last() == '[') stack.removeLast() else return false
            }
        }

        return stack.isEmpty()
    }
}