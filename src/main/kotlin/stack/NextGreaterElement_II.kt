package stack

class NextGreaterElement_II {
    fun nextGreaterElements(nums: IntArray): IntArray {
        val n = nums.size
        val result = IntArray(n) { -1 }  // Initialize all elements as -1
        val stack = mutableListOf<Int>()  // Stack to store indices of elements

        // Traverse the array twice (for circular behavior)
        for (i in 0 until 2 * n) {
            val currentIndex = i % n  // Use modulo to simulate circular behavior
            while (stack.isNotEmpty() && nums[stack.last()] < nums[currentIndex]) {
                val index = stack.removeLast()
                result[index] = nums[currentIndex]
            }

            // Only add indices from the first traversal (i < n)
            if (i < n) {
                stack.add(currentIndex)
            }
        }

        return result
    }
}
