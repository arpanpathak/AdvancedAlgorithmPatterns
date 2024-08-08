package binarysearch

class SearchInRotatedSortedArray {
    fun search(nums: IntArray, target: Int): Int {
        var (start, end) = listOf(0, nums.lastIndex)

        while (start <= end) {
            val mid = start + (end - start) / 2

            if (nums[mid] == target) return mid

            // Determine if Left half is sorted
            if (nums[start] <= nums[mid]) {
                if (nums[start] <= target && target < nums[mid]) {
                    end = mid - 1
                } else {
                    start = mid + 1
                }
            } else {
                // Right half is sorted
                if (nums[mid] < target && target <= nums[end]) {
                    start = mid + 1
                } else {
                    end = mid - 1
                }
            }
        }

        return -1

    }
}