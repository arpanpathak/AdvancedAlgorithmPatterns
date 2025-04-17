package bitset

class `Number of Steps to ReduceaANumberInBinaryRepresentationtoOne` {
    fun numSteps(s: String): Int {
        var steps = 0
        var carry = 0

        for (i in s.length - 1 downTo 1) {
            val digit = (s[i] - '0') + carry
            if (digit % 2 == 1) {
                steps += 2  // Add 1 (makes it even) + divide by 2
                carry = 1   // Propagate carry
            } else {
                steps += 1  // Just divide by 2
            }
        }

        return steps + carry  // Handle remaining carry at the first digit
    }
}