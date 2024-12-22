package stack

class MinimumAddtoMakeParenthesesValid {
    fun minAddToMakeValid(s: String): Int {
        var minCount = 0
        val stack = mutableListOf<Char>()

        s.forEach { ch ->
            when (ch) {
                '(' -> stack.add(ch) // Push opening parenthesis
                ')' -> if (stack.isNotEmpty() && stack.last() == '(') {
                    stack.removeLast() // Pop matching opening parenthesis
                } else {
                    minCount++ // Unmatched closing parenthesis
                }
            }
        }

        // Remaining unmatched opening parentheses
        minCount += stack.size

        return minCount
    }
}

// ((()))
