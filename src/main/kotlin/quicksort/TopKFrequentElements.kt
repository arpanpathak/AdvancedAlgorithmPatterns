package quicksort

import oracle.net.aso.k
import kotlin.random.Random

class TopKFrequentElements {
    private val map = HashMap<Int, Int>()

    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        nums.forEach { map[it] = map.getOrPut(it) { 0 } + 1 }

        val uniqueNums = map.keys.toIntArray()
        var start = 0
        var end = uniqueNums.size - 1

        while (start < end) {
            val partitionIndex = partition(uniqueNums, start, end)
            when {
                partitionIndex < k - 1 -> start = partitionIndex + 1
                partitionIndex > k - 1 -> end = partitionIndex - 1
                else -> break
            }
        }

        return uniqueNums.copyOfRange(0, k)
    }

    // Randomized Quick Partition...
    private fun partition(nums: IntArray, start: Int, end: Int): Int {
        val randomIndex = Random.nextInt(start, end + 1)
        swap(nums, randomIndex, end)  // Swap pivot with the end
        val pivot = map[nums[end]] ?: 0

        var partitionIndex = start
        for (i in start until end) {
            if (map[nums[i]]!! >= pivot) {
                swap(nums, i, partitionIndex++)
            }
        }

        swap(nums, partitionIndex, end)  // Swap back the pivot to the correct position
        return partitionIndex
    }

    private fun swap(nums: IntArray, i: Int, j: Int) {
       nums[i] = nums[j].also { nums[i] = it }
    }
}