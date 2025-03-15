package bitset

class ReverseBits {
    fun reverseBits(n: Int): Int {
        var num = n
        var result = 0

        for (i in 0 until 32) {
            val bit = num and 1   // Extract the last bit
            result = (result shl 1) or bit  // Shift left and add the bit
            num = num ushr 1       // Unsigned right shift n
        }

        return result
    }
}
