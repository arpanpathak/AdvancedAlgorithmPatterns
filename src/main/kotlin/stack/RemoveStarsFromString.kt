package stack

class RemoveStarsFromString {
    fun removeStars(s: String): String {
        val stack = mutableListOf<Char>()

        for (ch in s) {
            if ( ch == '*' && stack.isNotEmpty())
                stack.removeLast()
            else if(ch != '*')
                stack.add(ch)
        }

        return stack.joinToString(separator = "")
    }
}