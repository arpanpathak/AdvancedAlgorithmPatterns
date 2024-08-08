package string.backtracking

import java.lang.StringBuilder

class GenerateParantheses {
    fun generateParenthesis(n: Int): List<String> {
        val result = mutableListOf<String>()
        generate(n, n, StringBuilder(), result)
        return result
    }

    fun generate(open: Int, close: Int, current: StringBuilder, result: MutableList<String>) {
        if (open == 0 && close == 0) {
            result.add(current.toString())
            return
        }

        if (open > 0) {
            current.append("(")
            generate(open-1, close, current, result)
            current.setLength(current.length - 1)
        }

        if (close > open) {
            current.append(")")
            generate(open, close - 1, current, result)
            current.deleteCharAt(current.length - 1) // backtrack
        }
    }
}