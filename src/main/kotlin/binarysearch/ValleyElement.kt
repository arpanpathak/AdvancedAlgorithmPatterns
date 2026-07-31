package binarysearch

class ValleyElement {
    fun findValleyElementBinary(nums: IntArray): Int? {
        if (nums.isEmpty()) return null

        var (left, right) = 0 to nums.size

        while (left < right) {
            val mid = left + (right - left) / 2

            // Safely handle boundaries
            val leftNeighbor = if (mid > 0) nums[mid - 1] else Int.MAX_VALUE
            val rightNeighbor = if (mid < nums.size - 1) nums[mid + 1] else Int.MAX_VALUE

            when {
                nums[mid] < leftNeighbor && nums[mid] < rightNeighbor -> return nums[mid] // Found valley
                nums[mid] > rightNeighbor -> left = mid + 1 // Move to the right
                else -> right = mid // Move to the left
            }
        }

        return null // No valley found (unlikely for a valid array)
    }

    fun findValley(arr: IntArray): Int? {
        if (arr.isEmpty()) return null
        if (arr.size == 1) return arr[0]

        var low = 0
        var high = arr.size - 1

        while (low <= high) {
            val mid = low + (high - low) / 2

            val leftVal = if (mid > 0) arr[mid - 1] else Int.MAX_VALUE
            val rightVal = if (mid < arr.size - 1) arr[mid + 1] else Int.MAX_VALUE

            // Move towards decreasing slope
            when {
                arr[mid] <= leftVal && arr[mid] <= rightVal -> return arr[mid]
                leftVal < arr[mid] -> high = mid - 1
                else -> low = mid + 1
            }
        }
        return null
    }
}
