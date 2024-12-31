package array.random

import kotlin.random.Random

class RandomPickIndex(nums: IntArray) {
    private val targetIndices = mutableMapOf<Int, MutableList<Int>>()

    init {
        // Fill the map with all indices of each target number
        for (i in nums.indices) {
            val num = nums[i]
            if (!targetIndices.containsKey(num)) {
                targetIndices[num] = mutableListOf()
            }
            targetIndices[num]?.add(i)
        }
    }

    fun pick(target: Int): Int {
        // Randomly pick an index from the stored indices
        val indices = targetIndices[target] ?: return -1  // If target not found, return -1
        return indices[Random.nextInt(indices.size)]
    }
}