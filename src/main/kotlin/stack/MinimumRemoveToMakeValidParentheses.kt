package stack

import javax.swing.text.html.HTML.Tag.S

class MinimumRemoveToMakeValidParentheses {
    fun minRemoveToMakeValid(s: String): String {
        val stack = mutableListOf<Int>()
        val toRemove = mutableSetOf<Int>()

        for (i in s.indices) {
            when {
                s[i] == '(' -> stack.add(i)
                s[i] == ')' && stack.isNotEmpty() -> stack.removeLast()
                s[i] == ')' && stack.isEmpty() -> toRemove.add(i)
                else -> continue
            }
        }

        while (stack.isNotEmpty()) {
            toRemove.add(stack.removeLast())
        }

        return s.filterIndexed { index, _ -> index !in toRemove }
    }
}