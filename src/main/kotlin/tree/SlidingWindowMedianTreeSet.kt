package tree

import java.util.*
import kotlin.collections.ArrayDeque

class SlidingWindowMedianTreeSet {
    // Use TreeSet with index-based comparison to handle duplicates
    private val lower = TreeSet<Int> { a, b ->
        if (nums[a] != nums[b]) nums[a].compareTo(nums[b])
        else a.compareTo(b)
    }

    private val upper = TreeSet<Int> { a, b ->
        if (nums[a] != nums[b]) nums[a].compareTo(nums[b])
        else a.compareTo(b)
    }

    private lateinit var nums: IntArray
    private val removalQueue = ArrayDeque<Int>()

    fun medianSlidingWindow(nums: IntArray, k: Int): DoubleArray {
        this.nums = nums
        val result = DoubleArray(nums.size - k + 1)

        nums.indices.forEach { i ->
            addIndex(i)

            if (i >= k - 1) {
                result[i - k + 1] = calculateMedian(k)
                removeIndex(i - k + 1)
            }
        }

        return result
    }

    private fun addIndex(idx: Int) {
        if (lower.isEmpty() || nums[idx] <= nums[lower.last()]) {
            lower.add(idx)
        } else {
            upper.add(idx)
        }
        balance()
        removalQueue.addLast(idx)
    }

    private fun removeIndex(idx: Int) {
        if (lower.contains(idx)) {
            lower.remove(idx)
        } else {
            upper.remove(idx)
        }
        balance()
    }

    private fun balance() {
        // My goal is to keep both half equal. If odd numbers of elements then keep n / 2 + 1 elements in lower half.
        // i.e one extra element.
        if (lower.size > upper.size + 1) {
            lower.pollLast()?.let { upper.add(it) }
        }
        if (upper.size > lower.size) {
            upper.pollFirst()?.let { lower.add(it) }
        }
    }

    private fun calculateMedian(k: Int): Double {
        return when (k % 2 ) {
            1 -> nums[lower.last()].toDouble()
            else -> (nums[lower.last()].toDouble() + nums[upper.first()].toDouble()) / 2.0
        }
    }
}
