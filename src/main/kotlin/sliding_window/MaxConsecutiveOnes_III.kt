package sliding_window

class MaxConsecutiveOnes_III {
    fun longestOnes(nums: IntArray, k: Int): Int {
        var left = 0
        var K = k
        var right = 0

        while (right < nums.size) {
            if (nums[right++] == 0)
                K --

            if (K < 0) {
                K+= 1 - nums[left++]
            }
        }

        return right - left
    }
}