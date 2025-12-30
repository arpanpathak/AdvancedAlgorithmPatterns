package quicksort

import kotlin.random.Random

fun topKFrequent(nums: IntArray, k: Int): IntArray {
    val freq = nums.asIterable().groupingBy { it }.eachCount()
    val unique = freq.keys.toIntArray()
    val targetIndex = unique.size - k

    quickSelect(unique, targetIndex, compareBy { freq[it] })

    return unique.copyOfRange(targetIndex, unique.size)
}

// Another way of doing this... .
// "I fear not the man who has practiced 10,000 kicks once, but I fear the man who has practiced one kick 10,000 times."
//fun topKFrequent(nums: IntArray, k: Int): IntArray {
//    val freq = nums.asIterable().groupingBy { it }.eachCount()
//    val unique = freq.keys.toIntArray()
//
//    // With descending order, we look for index k-1
//    quickSelect(unique, k - 1, compareByDescending { freq[it] })
//
//    return unique.copyOfRange(0, k)
//}

private fun quickSelect(arr: IntArray, k: Int, comparator: Comparator<Int>) {
    var left = 0
    var right = arr.size - 1

    while (left < right) {
        val pivotIndex = partition(arr, left, right, comparator)
        when {
            pivotIndex == k -> return
            pivotIndex < k -> left = pivotIndex + 1
            else -> right = pivotIndex - 1
        }
    }
}

private fun partition(arr: IntArray, left: Int, right: Int, comparator: Comparator<Int>): Int {
    val pivotIdx = Random.nextInt(left, right + 1)
    val pivotValue = arr[pivotIdx]

    arr[pivotIdx] = arr[right].also { arr[right] = arr[pivotIdx] }

    var partitionIndex = left
    for (i in left until right) {
        if (comparator.compare(arr[i], pivotValue) < 0) {
            arr[i] = arr[partitionIndex].also { arr[partitionIndex] = arr[i] }
            partitionIndex++
        }
    }

    arr[partitionIndex] = arr[right].also { arr[right] = arr[partitionIndex] }
    return partitionIndex
}
