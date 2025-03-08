package binarysearch

class SingleElementInASortedArray {
    fun singleNonDuplicate(nums: IntArray): Int {
        var low = 0
        var high = nums.size - 1

        while (low < high) {
            var mid = low + (high - low) / 2
            // Ensure mid is even for checking pair with the next element
            if (mid % 2 == 1) {
                mid--
            }

            // Check if the pair is valid
            if (nums[mid] == nums[mid + 1]) {
                low = mid + 2 // Move to the right half
            } else {
                high = mid // Move to the left half
            }
        }

        return nums[low] // low == high will give the unique element
    }
}
