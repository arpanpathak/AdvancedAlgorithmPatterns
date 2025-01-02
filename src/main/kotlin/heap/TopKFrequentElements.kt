package heap

import java.util.*

class TopKFrequentElements {
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val freqMap  = mutableMapOf<Int, Int>()

        nums.forEach { freqMap[it] = freqMap.getOrPut(it){0} + 1}

        // Min-Heap to keep k most frequent elements
        val minHeap = PriorityQueue<Int> { a, b -> freqMap[a]!! - freqMap[b]!! }

        for (num in freqMap.keys) {
            minHeap.offer(num)
            if (minHeap.size > k) {
                minHeap.poll()
            }
        }

        return minHeap.toIntArray()
    }
}