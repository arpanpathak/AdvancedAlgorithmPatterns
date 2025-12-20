package stack

class MinimumOperationstoConvertAllElementstoZero {
    fun minOperations(nums: IntArray): Int {
        // 's' is the Monotonic Stack, storing the heights of currently active segments.
        // It maintains a strictly increasing sequence of positive numbers from bottom to top.
        val s = ArrayDeque<Int>()
        var result = 0

        // Iterate through each number in the array.
        for (a in nums) {

            // 1. POP HIGHER SEGMENTS (Segments End)
            // If the current value 'a' is smaller than the last segment height on the stack,
            // the segments corresponding to the popped values are effectively zeroed out here.
            while (s.isNotEmpty() && s.last() > a) {
                s.removeLast()
            }

            // 2. IGNORE ZEROES
            // Zeroes mark breaks but do not start new operations or segments.
            if (a == 0) continue

            // 3. START NEW SEGMENT (Operation Required)
            // This condition is true when 'a' starts a segment that is nested on top of
            // the current segment minimum (s.last()), or if the stack is empty (ground level).
            if (s.isEmpty() || s.last() < a) {
                // A new operation is required to clear this new, higher segment.
                result++
                // Push 'a' onto the stack to mark the height of the new active segment.
                s.add(a)
            }

            // If a == s.last(), the current segment continues, and no action is needed.
        }

        return result
    }
}
