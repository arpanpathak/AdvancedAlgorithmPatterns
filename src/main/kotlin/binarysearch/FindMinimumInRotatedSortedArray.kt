package binarysearch

class FindMinimumInRotatedSortedArray {
    fun findMin(nums: IntArray): Int {
        var (left, right) = 0 to nums.size - 1
        while (left < right) {
            val mid = left + (right - left) / 2
            when {
                nums[mid] > nums[right] -> left = mid + 1
                else -> right = mid
            }
        }

        return nums[left]
    }
}