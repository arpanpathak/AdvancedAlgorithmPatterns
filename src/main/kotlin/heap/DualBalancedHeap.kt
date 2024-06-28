package heap

import java.util.*

class DualBalancedHeap<T : Comparable<T>> {
    private val minHeap = PriorityQueue<T>() // Min-heap for the larger half
    private val maxHeap = PriorityQueue<T>(compareByDescending { it }) // Max-heap for the smaller half

    private fun balanceHeaps() {
        if (maxHeap.size > minHeap.size + 1) {
            minHeap.add(maxHeap.poll())
        } else if (minHeap.size > maxHeap.size) {
            maxHeap.add(minHeap.poll())
        }
    }

    fun add(num: T) {
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.add(num)
        } else {
            minHeap.add(num)
        }
        balanceHeaps()
    }

    fun remove(num: T) {
        if (maxHeap.contains(num)) {
            maxHeap.remove(num)
        } else {
            minHeap.remove(num)
        }
        balanceHeaps()
    }

    fun getMedian(): T? {
        return if (maxHeap.size == minHeap.size) {
            // Handling generic types requires a custom approach for averaging or choosing one of the elements.
            maxHeap.peek() // Simplification: you may need to define how to combine two elements for real applications.
        } else {
            maxHeap.peek()
        }
    }

    fun size(): Int {
        return maxHeap.size + minHeap.size
    }
}
