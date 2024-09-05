package math.stack

class BasicCalculator_I {
    fun calculate(s: String): Int {
        val stack = mutableListOf<Int>()
        var result = 0
        var sign = 1
        var i = 0

        while (i < s.length) {
            when (val c = s[i]) {
                in '0'..'9' -> {
                    var num = 0
                    // Parse the full number
                    while (i < s.length && s[i] in '0'..'9') {
                        num = num * 10 + (s[i] - '0')
                        i++
                    }
                    result += sign * num
                    i-- // Adjust the index because of the inner loop
                }
                '+' -> sign = 1
                '-' -> sign = -1
                '(' -> {
                    // Push the current result and sign to the stack
                    stack.add(result)
                    stack.add(sign)
                    // Reset result and sign
                    result = 0
                    sign = 1
                }
                ')' -> {
                    // Pop the sign and previous result from the stack
                    result = stack.removeLast() * result + stack.removeLast()
                }
            }
            i++
        }

        return result
    }
}