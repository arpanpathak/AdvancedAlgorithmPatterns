package array.Combinatorics

import kotlin.math.abs

class ClosestSubsequenceSum {
    fun minAbsDifference(nums: IntArray, goal: Int): Int {
        var possibleSums = mutableSetOf(0)
        for (num in nums) {
            val newSums = mutableSetOf<Int>()
            for (sum in possibleSums) {
                newSums.add(sum + num)
            }
            possibleSums.addAll(newSums)
        }

        var minDiff = Int.MAX_VALUE
        for (sum in possibleSums) {
            minDiff = minOf(minDiff, abs(sum - goal))
        }

        return minDiff
    }
}