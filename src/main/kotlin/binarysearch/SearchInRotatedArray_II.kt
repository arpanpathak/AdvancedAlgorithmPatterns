package binarysearch

class SearchInRotatedArray_II {
    fun search(nums: IntArray, target: Int): Boolean {
        var left = 0
        var right = nums.lastIndex

        while (left <= right) {
            val mid = left + (right - left) / 2

            when {
                nums[mid] == target -> return true

                // Handle duplicates: skip the duplicates
                nums[left] == nums[mid] && nums[mid] == nums[right] -> {
                    left++
                    right--
                }

                // Left portion is sorted
                nums[left] <= nums[mid] -> {
                    if (nums[left] <= target && target < nums[mid]) {
                        right = mid - 1 // Target in left portion
                    } else {
                        left = mid + 1 // Target in right portion
                    }
                }

                // Right portion is sorted
                else -> {
                    if (nums[mid] < target && target <= nums[right]) {
                        left = mid + 1 // Target in right portion
                    } else {
                        right = mid - 1 // Target in left portion
                    }
                }
            }
        }
        return false
    }
}
