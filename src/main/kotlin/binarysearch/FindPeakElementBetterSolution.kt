package binarysearch

class FindPeakElementBetterSolution {
    fun findPeakElement(nums: IntArray): Int? {
        if (nums.isEmpty()) return null

        var (left, right) = 0 to nums.size - 1

        while (left < right) {
            val mid = left + (right - left) / 2

            // Safely handle boundaries
            val leftNeighbor = if (mid > 0) nums[mid - 1] else Int.MIN_VALUE
            val rightNeighbor = if (mid < nums.size - 1) nums[mid + 1] else Int.MIN_VALUE

            when {
                nums[mid] > leftNeighbor && nums[mid] > rightNeighbor -> return mid // Found peak
                nums[mid] < rightNeighbor -> left = mid + 1 // Move to the right
                else -> right = mid // Move to the left
            }
        }

        return left // Single peak element when the range narrows down
    }
}
