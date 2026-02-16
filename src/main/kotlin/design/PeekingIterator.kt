package design

// Kotlin Iterator reference:
// https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterator/

class PeekingIterator(iterator: Iterator<Int>) : Iterator<Int> {
    // 1. Capture the iterator from the constructor into a class property
    private val innerIterator = iterator

    // 2. Buffer to hold the next value
    private var nextValue: Int? = null

    init {
        // 3. Prime the buffer immediately
        if (innerIterator.hasNext()) {
            nextValue = innerIterator.next()
        }
    }

    fun peek(): Int {
        // Return the buffered value without advancing
        return nextValue!!
    }

    override fun next(): Int {
        val current = nextValue

        // Advance the inner iterator to refill the buffer
        if (innerIterator.hasNext()) {
            nextValue = innerIterator.next()
        } else {
            nextValue = null
        }

        return current!!
    }

    override fun hasNext(): Boolean {
        return nextValue != null
    }
}
/**
 * Your PeekingIterator object will be instantiated and called as such:
 * var obj = PeekingIterator(arr)
 * var param_1 = obj.next()
 * var param_2 = obj.peek()
 * var param_3 = obj.hasNext()
 */