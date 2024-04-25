package array

class RemoveElement {
    fun removeElement(nums: IntArray, `val`: Int): Int {
        var i = 0

        for (j in nums.indices) {
            if (nums[j] != `val`) {
                nums[i++] = nums[j]
            }
        }

        return i
    }
}