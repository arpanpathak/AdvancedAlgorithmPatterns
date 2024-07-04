package array.greedy

import kotlin.math.abs

class MaximumDistanceInArray {
    fun maxDistance(arrays: List<List<Int>>): Int {
        var (max, min, maxDistance) = Triple( arrays[0].first(), arrays[0].last(), 0)

        for (arr in arrays) {
            val (currentMin, currentMax) = Pair(arr.first(), arr.last())

            maxDistance = maxOf(maxDistance, abs(max - currentMin ), abs(currentMax - min))

            min = minOf(min, currentMin)
            max = maxOf(max, currentMax)
        }

        return maxDistance
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val test = MaximumDistanceInArray()

            test.maxDistance(listOf(
                listOf(1,2,3),
                listOf(4,5),
                listOf(1,2,3)
            ))
        }
    }
}