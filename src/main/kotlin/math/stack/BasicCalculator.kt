package math.stack

import java.util.*

class BasicCalculator {
    private val operators = setOf('(' , '+', '-')

    // Function to get precedence of operators
    private fun precedence(op: Char): Int {
        return when (op) {
            '+', '-' -> 1
            else -> 0
        }
    }

    // Function to convert infix to postfix with unary operators handled
    private fun infixToPostfix(s: String): String {
        val stack = Stack<Char>()
        val postfix = StringBuilder()
        var i = 0

        while (i < s.length) {
            when {
                // Handle multi-digit numbers
                s[i].isDigit() -> {
                    while (i < s.length && s[i].isDigit()) {
                        postfix.append(s[i])
                        i++
                    }
                    postfix.append(' ') // Add a space to separate numbers
                }
                s[i] == '(' -> stack.push(s[i])
                s[i] == ')' -> {
                    while (stack.isNotEmpty() && stack.peek() != '(') {
                        postfix.append(stack.pop())
                        postfix.append(' ')
                    }
                    stack.pop() // Pop '('
                }
                s[i] == '+' || s[i] == '-' -> {
                    // Handle unary operators
                    if (i == 0 || s[i - 1] in operators) {
                        // Unary operator, treat as binary with previous number as zero
                        if (s[i] == '-') {
                            postfix.append('0')
                            postfix.append(s[i])
                            postfix.append(' ')
                        } else {
                            postfix.append(s[i])
                            postfix.append(' ')
                        }
                    } else {
                        while (stack.isNotEmpty() && stack.peek() != '(' && precedence(stack.peek()) >= precedence(s[i])) {
                            postfix.append(stack.pop())
                            postfix.append(' ')
                        }
                        stack.push(s[i])
                    }
                }
            }
            i++
        }

        while (stack.isNotEmpty()) {
            postfix.append(stack.pop())
            postfix.append(' ')
        }

        return postfix.toString().trim()
    }

    // Function to evaluate postfix expression
    private fun evaluatePostfix(postfix: String): Int {
        val stack = Stack<Int>()
        var i = 0

        while (i < postfix.length) {
            when {
                postfix[i].isDigit() -> {
                    var num = 0
                    while (i < postfix.length && postfix[i].isDigit()) {
                        num = num * 10 + postfix[i].digitToInt()
                        i++
                    }
                    stack.push(num)
                }
                postfix[i] == '+' -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a + b)
                }
                postfix[i] == '-' -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a - b)
                }
            }
            i++
        }

        return stack.pop()
    }

    fun calculate(s: String): Int {
        val postfix = infixToPostfix(s.replace(" ", "")) // Convert infix expression to postfix
        return evaluatePostfix(postfix) // Evaluate the postfix expression
    }
}
