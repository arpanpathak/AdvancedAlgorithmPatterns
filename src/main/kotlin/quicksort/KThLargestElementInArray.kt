package quicksort

import kotlin.random.Random

class KThLargestElementInArray {
    fun partition(nums: IntArray, left: Int, right: Int): Int {
        // Randomly select pivot index and swap with the last element
        val pivotIndex = Random.nextInt(left, right + 1)
        nums[pivotIndex] = nums[right].also { nums[right] = nums[pivotIndex] }
        val pivot = nums[right]

        // Partition around the pivot
        var i = left
        for (j in left until right) {
            if (nums[j] <= pivot) {
                nums[i] = nums[j].also { nums[j] = nums[i] }
                i++
            }
        }
        nums[i] = nums[right].also { nums[right] = nums[i] }
        return i
    }

    fun findKthLargest(nums: IntArray, k: Int): Int {
        var left = 0
        var right = nums.size - 1

        while (left <= right) {
            val pivotIndex = partition(nums, left, right)
            when {
                pivotIndex == nums.size - k -> return nums[pivotIndex]
                pivotIndex < nums.size - k -> left = pivotIndex + 1
                else -> right = pivotIndex - 1
            }
        }

        return -1 // If k is out of range of the array, handle accordingly
    }

    fun uniqueOccurrences(arr: IntArray): Boolean {
        val map = mutableMapOf<Int, Int>()

        for (num in arr) {
            map[num] = map.getOrPut(num){1} + 1
        }

        return map.keys.toSet().size == map.keys.size
    }

}