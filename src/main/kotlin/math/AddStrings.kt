package math

class AddStrings {
    fun addStrings(num1: String, num2: String): String {
        val sb = StringBuilder()
        var (i, j) = num1.length - 1 to num2.length - 1
        var carry = 0

        // Process digits from right to left
        while (i >= 0 || j >= 0 || carry != 0) {
            // Get the current digits (or 0 if we've run out of digits)
            val digit1 = if (i >= 0) num1[i--] - '0' else 0
            val digit2 = if (j >= 0) num2[j--] - '0' else 0

            // Calculate the sum of digits and the carry
            val sum = digit1 + digit2 + carry
            carry = sum / 10  // New carry for the next iteration
            sb.append(sum % 10)  // Append the current digit (sum % 10)
        }

        // The string is built in reverse order, so we need to reverse it
        return sb.reverse().toString()
    }
}