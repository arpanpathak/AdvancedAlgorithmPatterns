package math

class pow {
    fun myPow(x: Double, n: Int): Double {
        fun pow(x: Double, n: Long): Double {
            if (n == 0L) return 1.0

            val half = pow(x, n/2)
            return when(n % 2) {
                0L -> half * half
                else -> x * half * half
            }
        }
        return if (n >=0) pow(x, n.toLong()) else 1 / pow(x, n.toLong())
    }
}