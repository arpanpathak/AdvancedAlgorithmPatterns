package stack

import oracle.net.aso.b.i

class SmallestSubsequenceOfDistinctCharacters {
    fun smallestSubsequence(s: String): String {
        val lastIndex = IntArray(26) {-1}
        val inStack = BooleanArray(26)
        val stack = ArrayDeque<Char>()

        for (i in s.indices)
            lastIndex[s[i] - 'a'] = i

        for (i in s.indices) {
            val c = s[i]

            if (inStack[c - 'a']) continue

            while (stack.isNotEmpty() && stack.last() > c && lastIndex[stack.last() - 'a'] > i)  {
                inStack[stack.removeLast() - 'a'] = false
            }

            stack.addLast(c)
            inStack[c - 'a'] = true
        }

        return stack.joinToString("")
    }
}
