package math

class Sqrt {
    fun mySqrt(x: Int): Int {
        if ( x < 2) return x
        var (left, right) = 1 to x
        while (left <= right) {
            val mid = left + (right - left)/2
            val square = mid.toLong() * mid
            when {
                square < x -> left = mid + 1
                square > x -> right = mid - 1
                else -> return mid
            }
        }

        return right
    }
}