package stack

class NumberOfVisiblePeopleInAQueue {
    fun canSeePersonsCount(heights: IntArray): IntArray {
        val n = heights.size
        val answer = IntArray(n)
        val stack = ArrayDeque<Int>()

        for (i in n - 1 downTo 0) {
            // Pop elements from the stack that are shorter than the current person
            while (stack.isNotEmpty() && heights[i] > stack.last()) {
                stack.removeLast()
                answer[i]++
            }
            // If the stack is not empty, the current person can see one more person (the taller one)
            if (stack.isNotEmpty()) {
                answer[i]++
            }
            // Push the current person's height onto the stack
            stack.addLast(heights[i])
        }

        return answer
    }
}