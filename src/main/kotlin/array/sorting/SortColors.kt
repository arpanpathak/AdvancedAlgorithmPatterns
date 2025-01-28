package array.sorting

class SortColors {
    fun sortColors(nums: IntArray): Unit {
        fun swap(i: Int, j: Int) {
            nums[i] = nums[j].also { nums[j] = nums[i] }
        }

        var (low, high) = 0 to nums.size - 1
        var i = 0

        while (i <= high) {
            when (nums[i]) {
                0 -> swap(i++, low++)
                1 -> i++
                2 -> swap(i, high--)
            }
        }
    }
}
