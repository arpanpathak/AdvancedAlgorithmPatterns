package math.stack

class BasicCalculator_II_ShortCode {
    fun calculate(s: String): Int {
        var (currentNumber, result, lastNumber) = listOf(0, 0, 0)
        var operator = '+'

        s.forEachIndexed { i, char ->
            when {
                char.isDigit() -> currentNumber = currentNumber * 10 + (char - '0')
                !char.isDigit() && char != ' ' || i == s.lastIndex -> {
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
        }

        return result + lastNumber
    }
}
