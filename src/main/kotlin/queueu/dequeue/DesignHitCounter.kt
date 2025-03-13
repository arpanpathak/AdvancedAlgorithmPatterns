package queueu.dequeue

import java.util.*

class HitCounter() {
    private val hits: Deque<Int> = LinkedList()

    fun hit(timestamp: Int) {
        hits.offer(timestamp)
    }

    fun getHits(timestamp: Int): Int {
        while (hits.isNotEmpty() && hits.peekFirst() <= timestamp - 300) {
            hits.pollFirst()
        }
        return hits.size
    }
}
