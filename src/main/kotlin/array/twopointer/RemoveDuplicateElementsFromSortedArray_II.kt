package array.twopointer

class RemoveDuplicateElementsFromSortedArray_II {
    fun removeDuplicates(nums: IntArray): Int {
        var index = 0
        var count = 1
        for (i in 1..nums.lastIndex) {
            if (nums[i] == nums[i-1]) {
                count++
            } else {
                count = 1
            }

            if ( count<=2) {
                nums[++index] = nums[i]
            }
        }

        return index + 1
    }
}