package queueu.dequeue

import java.util.LinkedList

class NumberOfRecentCalls {
    val deque = LinkedList<Int>()
    val TIME_WINDOW_MS = 3000
    fun ping(t: Int): Int {
        while (deque.isNotEmpty() && ( t - deque.first()) > TIME_WINDOW_MS)
            deque.removeFirst()

        deque.addLast(t)

        return deque.size
    }
}