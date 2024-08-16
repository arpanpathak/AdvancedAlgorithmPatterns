package stack

class EvaluateReversePolishNotation {
    private val SYMBOLS = setOf("+", "-", "*", "/")

    fun evalRPN(tokens: Array<String>): Int {
        val stack = mutableListOf<Int>()
        for (token in tokens) {
            if (token in SYMBOLS) {
                val op2 = stack.removeLast()
                val op1 = stack.removeLast()
                stack.add(operate(token[0], op1, op2))
            } else
                stack.add(token.toInt())
        }
        return stack.removeLast()
    }

    fun operate(symbol: Char, a: Int, b: Int): Int {
        return when(symbol) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            else -> 0
        }
    }
}