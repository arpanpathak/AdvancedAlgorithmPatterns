package math

class PowerOfTwo {
    fun isPowerOfTwo(n: Int): Boolean {
        // Powers of two must be positive AND have only one bit set to 1.
        // n > 0 handles the positivity.
        // (n and (n - 1)) == 0 ensures only one bit is set.
        return n > 0 && (n and (n - 1)) == 0
    }
}
