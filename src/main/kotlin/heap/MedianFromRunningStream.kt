package heap

import java.util.*
import kotlin.Comparator

class MedianFromRunningStream {
    private val minHeap = PriorityQueue<Int>() // Min-heap for the larger half
    private val maxHeap = PriorityQueue<Int>(compareBy() { -it }) // Max-heap for the smaller half


    fun addNum(num: Int) {
        minHeap.offer(num)
        maxHeap.offer(minHeap.poll())

        if (minHeap.size < maxHeap.size) {
            minHeap.offer(maxHeap.poll())
        }
    }

    fun findMedian(): Double {
        return if (minHeap.size > maxHeap.size) {
            minHeap.peek().toDouble()
        } else {
            (minHeap.peek() + maxHeap.peek()) / 2.0
        }
    }
}