package sliding_window

class LongestSubArraysOfOneAfterDeletingOneElement {
    fun longestSubarray(nums: IntArray): Int {
        var (zeroCount, longestWindow, windowStart) = Triple(0, 0, 0)

        for (i in 0 until nums.size) {
            zeroCount += (1 - nums[i])

            // Shrink zero counts if it's > 1 coz if there is only one zero then that can be deleted
            // to extend two window of consecutive ones
            while (zeroCount > 1) {
                zeroCount-= (1-nums[windowStart++])
            }

            longestWindow = maxOf(longestWindow, i - windowStart)
        }


        return longestWindow
    }
}