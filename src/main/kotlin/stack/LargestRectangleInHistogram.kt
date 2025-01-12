package stack

class LargestRectangleInHistogram {
    fun largestRectangleArea(heights: IntArray): Int {
        val stack = mutableListOf<Int>()
        var maxArea = 0
        val heightsList = heights.toMutableList()  // Convert IntArray to MutableList
        heightsList.add(0)  // Add a zero at the end to pop all bars at the end

        for (i in heightsList.indices) {
            // While the current bar is shorter than the one at the top of the stack
            while (stack.isNotEmpty() && heightsList[stack.last()] > heightsList[i]) {
                val h = heightsList[stack.removeLast()]  // Pop the top bar using removeLast()
                val w = if (stack.isEmpty()) i else i - stack.last() - 1  // Calculate width
                maxArea = maxOf(maxArea, h * w)  // Update max area
            }
            stack.add(i)  // Push the current index onto the stack
        }

        return maxArea
    }
}
