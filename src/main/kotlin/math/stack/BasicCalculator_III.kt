package math.stack

class BasicCalculator_III {
    fun calculate1(s: String): Int {
        var index = 0

        fun evaluate(): Int {
            val stack = ArrayDeque<Int>()
            var num = 0
            var op = '+'

            while (index < s.length) {
                val c = s[index++]

                when {
                    c.isDigit() -> num = num * 10 + (c - '0')
                    c == '(' -> num = evaluate()
                    c == ')' -> break
                    c != ' ' -> {
                        when (op) {
                            '+' -> stack.addLast(num)
                            '-' -> stack.addLast(-num)
                            '*' -> stack.addLast(stack.removeLast() * num)
                            '/' -> stack.addLast(stack.removeLast() / num)
                        }
                        op = c
                        num = 0
                    }
                }
            }

            when (op) {
                '+' -> stack.addLast(num)
                '-' -> stack.addLast(-num)
                '*' -> stack.addLast(stack.removeLast() * num)
                '/' -> stack.addLast(stack.removeLast() / num)
            }

            return stack.sum()
        }

        return evaluate()
    }

    fun calculate(s: String): Int {
        var index = 0

        fun evaluate(): Int {
            var total = 0
            var current = 0
            var number = 0
            var operator = '+'

            while (index < s.length) {
                val char = s[index++]

                when {
                    char.isDigit() -> number = number * 10 + (char - '0')
                    char == '(' -> number = evaluate()
                    char == ')' -> break
                    char != ' ' -> {
                        current = when (operator) {
                            '+' -> number
                            '-' -> -number
                            '*' -> current * number
                            '/' -> current / number
                            else -> current
                        }

                        if (char in "+-") {
                            total += current
                            current = 0
                        }

                        operator = char
                        number = 0
                    }
                }
            }

            return total + when (operator) {
                '+' -> current + number
                '-' -> current - number
                '*' -> current * number
                '/' -> current / number
                else -> 0
            }
        }

        return evaluate()
    }
}