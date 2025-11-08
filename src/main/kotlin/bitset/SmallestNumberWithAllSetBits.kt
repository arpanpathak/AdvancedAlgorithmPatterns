package bitset

class SmallestNumberWithAllSetBits {

    fun smallestNumber(n: Int): Int {
        if (n <= 1) return 1

        // Find the bit position of the most significant set bit
        var msb = 0
        while (1 shl msb <= n) {
            msb++
        }

        // The number with all bits set up to msb position is (1 << msb) - 1
        return (1 shl msb) - 1
    }

}
