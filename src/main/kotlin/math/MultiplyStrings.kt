package math

class MultiplyStrings {
    fun multiply(num1: String, num2: String): String {
        if (num1 == "0" || num2 == "0") return "0"

        val result = IntArray(num1.length + num2.length)

        for (i in num1.indices.reversed()) {
            for (j in num2.indices.reversed()) {
                val product = (num1[i] - '0') * (num2[j] - '0') // Multiply Digits
                val sum  = product + result[i + j + 1] // Add to the current position
                result[i + j + 1] = sum % 10 // Convert current position to make it single digit
                result[i + j ] += sum / 10 // Add carry of sum to the next position
            }
        }

        return result.joinToString("").trimStart('0').ifEmpty { "0" }
    }
}