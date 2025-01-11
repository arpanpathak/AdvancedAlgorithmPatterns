package string.stack

class RemoveAllAdjacentDuplicatesInString {
    fun removeDuplicates(s: String): String {
        val stack = ArrayDeque<Char>()

        s.forEach { ch ->
            when {
                stack.isNotEmpty() && stack.last() == ch -> stack.removeLast()
                else -> stack.add(ch)
            }
        }

        return stack.joinToString("")
    }
}
