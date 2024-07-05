package array

class MoveZeroes {
    fun moveZeroes(nums: IntArray): Unit {
        var nonZeroPointer = 0

        // Move all non-zero elements to the front
        for (i in nums.indices) {
            if (nums[i] != 0) {
                nums[nonZeroPointer++] = nums[i]
            }
        }

        // Fill the rest of the array with zeros
        while (nonZeroPointer < nums.size) {
            nums[nonZeroPointer++] = 0
        }
    }
}