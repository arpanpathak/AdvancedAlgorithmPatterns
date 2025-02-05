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

    // Another intuitive solution using while loop
    fun longestOnes1(nums: IntArray, k: Int): Int {
        var left = 0
        var remainingK = k
        var maxLen = 0

        for (right in nums.indices) {
            if (nums[right] == 0) remainingK--

            // If the window becomes invalid, move `left` forward
            while (remainingK < 0) {
                if (nums[left++] == 0) remainingK++
            }

            // Update the maximum window size
            maxLen = maxOf(maxLen, right - left + 1)
        }

        return maxLen
    }
}