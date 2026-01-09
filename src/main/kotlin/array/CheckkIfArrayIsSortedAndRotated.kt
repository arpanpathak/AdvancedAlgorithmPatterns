package array

class CheckkIfArrayIsSortedAndRotated {
    fun check(nums: IntArray): Boolean {
        var count = 0
        val n = nums.size

        for (i in 0 until n) {
            // Compare current to next, using modulo to wrap around to index 0
            if (nums[i] > nums[(i + 1) % n]) {
                count++
            }

            // if we see more than one drop, it's impossible
            if (count > 1) return false
        }

        return true
    }
}