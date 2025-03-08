package math

class DivideTwoIntegers {
    fun divide(dividend: Int, divisor: Int): Int {
        // Handle overflow case
        if (dividend == Int.MIN_VALUE && divisor == -1) return Int.MAX_VALUE

        // Determine sign of the result
        val sign = if ((dividend < 0) xor (divisor < 0)) -1 else 1

        // Convert both numbers to long and take absolute value
        var dividendL = Math.abs(dividend.toLong())
        val divisorL = Math.abs(divisor.toLong())

        var quotient = 0L

        while (dividendL >= divisorL) {
            var tempDivisor = divisorL
            var multiple = 1L

            // Keep shifting divisor left until it's just smaller than the dividend
            while (dividendL >= (tempDivisor shl 1)) {
                tempDivisor = tempDivisor shl 1
                multiple = multiple shl 1
            }

            // Subtract the found largest chunk and accumulate the quotient
            dividendL -= tempDivisor
            quotient += multiple
        }

        // Apply the sign
        val result = sign * quotient

        // Ensure the result is within the 32-bit integer range
        return if (result > Int.MAX_VALUE) Int.MAX_VALUE else result.toInt()
    }
}