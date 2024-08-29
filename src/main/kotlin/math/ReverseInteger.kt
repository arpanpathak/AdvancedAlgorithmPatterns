package math

import kotlin.math.abs

class ReverseInteger {
    fun reverse(x: Int): Int {
        var reversed: Long = 0
        val sign = if(x<0) -1 else 1
        var num: Long = abs(x.toLong())

        while(num != 0L ) {
            // Check for overflow before actually adding the digit
            if (reversed > (Int.MAX_VALUE - num%10) / 10) {
                return 0
            }
            reversed = (reversed*10L + num % 10)
            num = num/10
        }

        return (sign * reversed.toInt())
    }
}