package stack

class RemoveKDigits {
    fun removeKdigits(num: String, k: Int): String {
        if ( k>= num.length) return "0"

        val stack = ArrayDeque<Char>()
        var remaining = k

        for (digit in num) {
            while (remaining > 0 && stack.isNotEmpty() && stack.last() > digit) {
                --remaining
                stack.removeLast()
            }

            stack.addLast(digit)
        }

        return stack.joinToString ("")
            .dropLast(remaining)
            .dropWhile { it == '0' }
            .ifEmpty { "0" }

    }
}
