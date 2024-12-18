package binarysearch

import kotlin.random.Random

class RandomPickWithWeight (w: IntArray) {
    private val prefixSum = IntArray(w.size){ 0 }
    private val totalSum: Int
    private val w: IntArray = w
    init {
        for (i in w.indices) {
            prefixSum[i] = if(i > 0) prefixSum[i-1] + w[i] else w[i]
        }

        totalSum = prefixSum.last()
    }

    fun pickIndex(): Int {
        val randomPick = Random.nextInt(totalSum)
        var (start, end) = 0 to w.size

        while (start < end) {
            val mid = start + (end - start)/2

            when {
                prefixSum[mid] > randomPick -> end = mid
                else -> start = mid + 1
            }
        }

        return start
    }
}