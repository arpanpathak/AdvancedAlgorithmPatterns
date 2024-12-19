package binarysearch

class FindFirstAndLastPosition {

    fun searchRange(nums: IntArray, target: Int): IntArray {
        val result = intArrayOf(-1, -1)

        // Find the first occurrence
        result[0] = binarySearch(nums, target, true)

        // If the first occurrence is not found, return [-1, -1]
        if (result[0] == -1) return result

        // Find the last occurrence
        result[1] = binarySearch(nums, target, false)

        return result
    }

    private fun binarySearch(nums: IntArray, target: Int, findFirst: Boolean): Int {
        var left = 0
        var right = nums.lastIndex
        var result = -1

        while (left <= right) {
            val mid = left + (right - left) / 2
            when {
                nums[mid] == target -> {
                    result = mid
                    // Adjust the search range based on whether we are looking for the first or last occurrence
                    if (findFirst) {
                        right = mid - 1  // Move left to find the first occurrence
                    } else {
                        left = mid + 1   // Move right to find the last occurrence
                    }
                }
                nums[mid] < target -> left = mid + 1
                else -> right = mid - 1
            }
        }

        return result
    }
}