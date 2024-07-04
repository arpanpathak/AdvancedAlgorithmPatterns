package quicksort

import kotlin.random.Random

class KClosestPointsToOrigin {
    fun kClosest(points: Array<IntArray>, K: Int): Array<IntArray> {
        var (start, end) = Pair( 0, points.size - 1)

        // Perform quicksort to partition the points until the k-th point is in its final position
        while (start < end) {
            val pivotIndex = quickSort(points, start, end)
            when {
                pivotIndex < K - 1 -> start = pivotIndex + 1
                pivotIndex > K - 1 -> end = pivotIndex - 1
                else -> break
            }
        }

        // Return the first K points
        return points.copyOfRange(0, K)
    }

    // Randomized Quicksort function
    private fun quickSort(points: Array<IntArray>, start: Int, end: Int): Int {
        var partitionIndex = start
        val randomIndex = Random.nextInt(start, end + 1)
        swap(points, end, randomIndex)

        val pivotDistance = calculateDistance(points[end])

        for (i in start until end) {
            val currentDistance = calculateDistance(points[i])
            if (currentDistance <= pivotDistance) {
                swap(points, i, partitionIndex++)
            }
        }

        swap(points, partitionIndex, end)
        return partitionIndex
    }

    // Helper function to swap elements in the array using `also`
    private fun swap(points: Array<IntArray>, i: Int, j: Int) {
        points[j] = points[i].also { points[i] = points[j] }
    }

    // Helper function to calculate the squared Euclidean distance
    private fun calculateDistance(point: IntArray): Int {
        return point[0] * point[0] + point[1] * point[1]
    }
}
