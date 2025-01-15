package stack

class NextGreaterElement_I {
    fun nextGreaterElement(nums1: IntArray, nums2: IntArray): IntArray {
        val stack = ArrayDeque<Int>() // Stack to keep track of elements in nums2
        val nextGreaterMap = mutableMapOf<Int, Int>() // Map to store the next greater element for each element in nums2

        // Traverse through nums2 from left to right
        for (num in nums2) {
            // While stack is not empty and the top of the stack is less than the current num, pop and store the result
            while (stack.isNotEmpty() && stack.last() < num) {
                val top = stack.removeLast()
                nextGreaterMap[top] = num
            }
            // Push the current num onto the stack
            stack.addLast(num)
        }

        // For any remaining elements in the stack, there is no next greater element, so assign -1
        while (stack.isNotEmpty()) {
            nextGreaterMap[stack.removeLast()] = -1
        }

        // Generate the result array for nums1
        return nums1.map { nextGreaterMap[it] ?: -1 }.toIntArray()
    }
}