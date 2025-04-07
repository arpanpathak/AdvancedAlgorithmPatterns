package array.cycle

class FindTheDuplicateNumber {
    fun findDuplicate(nums: IntArray): Int {
        var slow = nums[0]
        var fast = nums[0]

        // Phase 1: Detect intersection point
        while (true) {
            slow = nums[slow]
            fast = nums[nums[fast]]
            if (slow == fast) break
        }

        // Phase 2: Find the entry to the cycle (duplicate number)
        slow = nums[0]
        while (slow != fast) {
            slow = nums[slow]
            fast = nums[fast]
        }

        return slow
    }
}
