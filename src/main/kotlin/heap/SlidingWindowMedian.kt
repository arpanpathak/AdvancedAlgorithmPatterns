package heap

import java.util.*

class SlidingWindowMedian {
    private val minHeap = PriorityQueue<Double>() // Min-heap for the larger half
    private val maxHeap = PriorityQueue<Double> ( compareBy{-it} ) // Max-heap for the smaller half

    private fun balanceHeaps() {
        if (maxHeap.size > minHeap.size + 1) {
            minHeap.add(maxHeap.poll())
        } else if (minHeap.size > maxHeap.size) {
            maxHeap.add(minHeap.poll())
        }
    }

    private fun add(num: Int) {
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.add(num.toDouble())
        } else {
            minHeap.add(num.toDouble())
        }
        balanceHeaps()
    }

    private fun remove(num: Int) {
        if (num <= maxHeap.peek()) {
            maxHeap.remove(num.toDouble())
        } else {
            minHeap.remove(num.toDouble())
        }
        balanceHeaps()
    }

    private fun getMedian(): Double {
        return if (maxHeap.size == minHeap.size) {
            (maxHeap.peek().toDouble() + minHeap.peek().toDouble()) / 2.0
        } else {
            maxHeap.peek().toDouble()
        }
    }

    fun medianSlidingWindow(nums: IntArray, k: Int): DoubleArray {
        val result = DoubleArray(nums.size - k + 1)

        for (i in nums.indices) {
            add(nums[i])

            if (i >= k) {
                remove(nums[i - k])
            }

            if (i >= k - 1) {
                result[i - k + 1] = getMedian()
            }
        }

        return result
    }
}
