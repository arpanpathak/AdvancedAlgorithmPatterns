package array.twopointer

class RotateArray {
    fun rotate(nums: IntArray, k: Int): Unit {
        val n = nums.size
        val steps = k % n // In case k is greater than n

        reverse(nums, 0, n - 1)
        reverse(nums, 0, steps - 1)
        reverse(nums, steps, n - 1)
    }

    private fun reverse(nums: IntArray, start: Int, end: Int): Unit {
        var s = start
        var e = end
        while (s < e) {
            nums[s] = nums[e].also { nums[e] = nums[s] }
            s++
            e--
        }
    }
}