package sliding_window

class SlidingWindowMaximum {
    fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
        if (nums.isEmpty()) return intArrayOf()

        val result = mutableListOf<Int>()
        val deque = ArrayDeque<Int>()

        nums.forEachIndexed { i, num ->
            // Remove elements outside the current window
            if (deque.isNotEmpty() && deque.first() <= i - k) deque.removeFirst()

            // Remove smaller elements from the back of the deque. Goal is to keep max at front of queue
            while (deque.isNotEmpty() && nums[deque.last()] <= num) {
                deque.removeLast()
            }

            // Add the current element's index to the deque
            deque.addLast(i)

            // Once we have processed the first k elements, add the max (front of deque) to the result
            if (i >= k - 1) result.add(nums[deque.first()])
        }

        return result.toIntArray()
    }
}
