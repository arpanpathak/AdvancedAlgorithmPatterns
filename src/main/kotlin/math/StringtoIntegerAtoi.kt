package math

class StringtoIntegerAtoi {
    fun myAtoi(s: String): Int {
        var sign = 1
        var number = 0
        var i = 0

        // Skip leading spaces
        while (i < s.length && s[i] == ' ') {
            i++
        }

        // Check for sign
        if (i < s.length && s[i] in "+-") {
            sign = if (s[i] == '-') -1 else 1
            i++ // Move past the sign character
        }

        // Convert the digits
        while (i < s.length && s[i].isDigit()) {
            val digit = s[i] - '0'

            // Check for overflow before updating the number
            if (number > Int.MAX_VALUE / 10 || (number == Int.MAX_VALUE / 10 && digit > Int.MAX_VALUE % 10)) {
                return if (sign == 1) Int.MAX_VALUE else Int.MIN_VALUE
            }

            number = number * 10 + digit
            i++
        }

        return sign * number
    }
}