package array.hashtable

class LongestConsecutiveSequence {
    fun longestConsecutive(nums: IntArray): Int {
        val set = nums.toSet()
        var maxLength = 0
        for (num in nums) {
            // Check if num - 1 is NOT in the set.
            // This condition ensures that num is the start of a consecutive sequence.
            if ( num - 1 in set )
                continue

            var currentNum = num
            var currentLength = 1

            while (currentNum + 1 in set) {
                currentNum =currentNum + 1
                currentLength++
            }

            maxLength = maxOf(maxLength, currentLength)

        }

        return maxLength
    }
}