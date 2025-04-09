package array.dp

import kotlin.math.abs

class PartitionArrayIntoTwoArrayToMinimuzeSumDifference {
    fun minimumDifference(nums: IntArray): Int {
        val n = nums.size
        val totalSum = nums.sum()
        val halfSize = n / 2

        val leftSubsets = generateSubsetsBySize(nums, 0, halfSize)
        val rightSubsets = generateSubsetsBySize(nums, halfSize, n)

        var minDiff = Int.MAX_VALUE

        for (k in 0..halfSize) {
            val leftSums = leftSubsets[k] ?: continue
            val rightSums = rightSubsets[halfSize - k] ?: continue

            rightSums.sort()

            for (leftSum in leftSums) {
                val target = (totalSum - 2 * leftSum) / 2
                val closestRightSums = findClosestValues(rightSums, target)

                closestRightSums.forEach { rightSum ->
                    val currentDiff = abs(totalSum - 2 * (leftSum + rightSum))
                    minDiff = minOf(minDiff, currentDiff)
                    if (minDiff == 0) return 0  // Early exit if perfect match found
                }
            }
        }

        return minDiff
    }

    private fun generateSubsetsBySize(nums: IntArray, start: Int, end: Int): Array<MutableList<Int>?> {
        val size = end - start
        val subsets = Array<MutableList<Int>?>(size + 1) { mutableListOf() }

        (0 until (1 shl size)).forEach { mask ->
            var sum = 0
            var count = 0
            for (i in 0 until size) {
                if (mask and (1 shl i) != 0) {
                    sum += nums[start + i]
                    count++
                }
            }
            subsets[count]?.add(sum)
        }

        return subsets
    }

    private fun findClosestValues(sortedList: List<Int>, target: Int): List<Int> {
        val index = sortedList.binarySearch(target)
        if (index >= 0) return listOf(sortedList[index])  // Exact match

        val insertPos = -index - 1
        return buildList {
            if (insertPos < sortedList.size) add(sortedList[insertPos])
            if (insertPos > 0) add(sortedList[insertPos - 1])
        }.distinct()  // Remove duplicates if adjacent values are equal
    }
}