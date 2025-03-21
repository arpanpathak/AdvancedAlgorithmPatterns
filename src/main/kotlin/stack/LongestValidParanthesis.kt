package stack

class LongestValidParanthesis {
    fun longestValidParentheses(s: String): Int {
        val stack = ArrayDeque<Int>()
        stack.addLast(-1)  // Initialize stack with -1 to handle edge cases
        var maxLen = 0

        for (i in s.indices) {
            if (s[i] == '(') {
                stack.addLast(i)  // Push the index of '('
            } else {
                stack.removeLast()  // Pop the last '(' index
                if (stack.isEmpty()) {
                    stack.addLast(i)  // Push current index as new base
                } else {
                    maxLen = maxOf(maxLen, i - stack.last())
                }
            }
        }

        return maxLen
    }
}
