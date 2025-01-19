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
}