package stream


class MovingAverage(private val size: Int) {

    private val window = ArrayDeque<Int>()
    private var sum = 0
    fun next(`val`: Int): Double {
        window.add(`val`)
        sum += `val`
        if (window.size > size) {
            sum -= window.removeFirst()
        }

        return sum.toDouble() / window.size
    }

}

/**
 * Your MovingAverage object will be instantiated and called as such:
 * var obj = MovingAverage(size)
 * var param_1 = obj.next(`val`)
 */