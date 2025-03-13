package stack

class OneThreeTwoPattern {
    fun find132pattern(nums: IntArray): Boolean {
        val stack = ArrayDeque<Int>()
        var thirdElement = Int.MIN_VALUE

        // Traverse from the rightmost element to the left
        for (i in nums.size - 1 downTo 0) {
            if (nums[i] < thirdElement) return true

            // Pop elements from stack that are less than nums[i]
            while (stack.isNotEmpty() && nums[i] > stack.last()) {
                thirdElement = stack.removeLast()
            }

            stack.add(nums[i])
        }

        return false
    }
}