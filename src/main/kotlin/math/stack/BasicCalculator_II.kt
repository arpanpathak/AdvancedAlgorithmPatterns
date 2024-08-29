package math.stack

class BasicCalculator_II {
    fun calculate(s: String): Int {
        val stack = mutableListOf<Int>()
        var currentNumber = 0
        var operator = '+'

        for (i in s.indices) {
            if (s[i].isDigit()) {
                currentNumber = currentNumber * 10 + ( s[i] - '0')

            }

            if (!s[i].isDigit() && s[i] != ' ' || i == s.lastIndex) {
                when (operator) {
                    '+' -> stack.add(currentNumber)
                    '-' -> stack.add(-currentNumber)
                    '*' -> stack[stack.lastIndex] *= currentNumber
                    '/' -> stack[stack.lastIndex] /= currentNumber
                }
                operator = s[i]
                currentNumber = 0
            }
        }

        return stack.sum()
    }

    // O(1) Space solution
    fun calculate_o1(s: String): Int {
        var currentNumber = 0
        var result = 0
        var lastNumber = 0
        var operator = '+'

        for (i in s.indices) {
            val char = s[i]

            if (char.isDigit()) {
                currentNumber = currentNumber * 10 + char.digitToInt()
            }

            if (!char.isDigit() && char != ' ' || i == s.lastIndex) {
                when (operator) {
                    '+' -> {
                        result += lastNumber
                        lastNumber = currentNumber
                    }
                    '-' -> {
                        result += lastNumber
                        lastNumber = -currentNumber
                    }
                    '*' -> lastNumber *= currentNumber
                    '/' -> lastNumber /= currentNumber
                }
                operator = char
                currentNumber = 0
            }
        }

        result += lastNumber
        return result
    }
}
