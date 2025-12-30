package quicksort

import kotlin.random.Random

fun topKFrequentDualPivot(nums: IntArray, k: Int): IntArray {
    val frequencyMap = nums.asIterable().groupingBy { it }.eachCount()
    val uniqueElements = frequencyMap.keys.toIntArray()

    // Descending order means top k elements are at indices 0..k-1
    quickSelectDual(uniqueElements, k - 1, compareByDescending { frequencyMap[it] })

    return uniqueElements.copyOfRange(0, k)
}

private fun quickSelectDual(arr: IntArray, targetIndex: Int, comparator: Comparator<Int>) {
    var left = 0
    var right = arr.size - 1

    while (left < right) {
        val (pivot1, pivot2) = partitionDual(arr, left, right, comparator)

        when {
            targetIndex < pivot1 -> right = pivot1 - 1
            targetIndex > pivot2 -> left = pivot2 + 1
            else -> return
        }
    }
}

private fun partitionDual(arr: IntArray, left: Int, right: Int, comparator: Comparator<Int>): Pair<Int, Int> {
    // Randomized pivot selection
    val r1 = Random.nextInt(left, right + 1)
    var r2 = Random.nextInt(left, right + 1)
    while (r1 == r2 && left != right) r2 = Random.nextInt(left, right + 1)

    arr.swap(r1, left)
    arr.swap(r2, right)

    if (comparator.compare(arr[left], arr[right]) > 0) {
        arr.swap(left, right)
    }

    val leftPivot = arr[left]
    val rightPivot = arr[right]

    var smallerBoundary = left + 1
    var largerBoundary = right - 1
    var current = left + 1



    while (current <= largerBoundary) {
        when {
            comparator.compare(arr[current], leftPivot) < 0 -> {
                arr.swap(current++, smallerBoundary++)
            }
            comparator.compare(arr[current], rightPivot) > 0 -> {
                arr.swap(current, largerBoundary--)
            }
            else -> current++
        }
    }

    // Final pivot placement
    arr.swap(left, smallerBoundary - 1)
    arr.swap(right, largerBoundary + 1)

    return Pair(smallerBoundary - 1, largerBoundary + 1)
}

private fun IntArray.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
}