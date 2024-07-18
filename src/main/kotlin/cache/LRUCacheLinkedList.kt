package cache

import java.util.*
import kotlin.collections.HashMap

class LRUCacheLinkedList(private val capacity: Int) {
    private val cache = mutableMapOf<Int, Int>()
    private val order: LinkedList<Int> = LinkedList()
    private val size: Int = capacity

    fun get(key: Int): Int {
        return if (cache.containsKey(key)) {
            // Move the accessed key to the end of the list (most recently used)
            order.remove(key)
            order.addLast(key)
            cache[key]!!
        } else {
            -1
        }
    }

    fun put(key: Int, value: Int) {
        if (cache.size >= size) {
            // Evict the least recently used element
            val oldest = order.removeFirst()
            cache.remove(oldest)
        }

        // Insert the new element
        cache[key] = value
        order.addLast(key)
    }
}
