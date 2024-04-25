package grid.histogram

import java.util.*

class MaximalRectangle {

    private lateinit var dp: Array<Int>

    fun largestRectangleArea(heights: IntArray): Int {
        val stack = Stack<Int>()
        var ( maxArea, i ) = listOf(0, 0)

        while (i <= heights.size) {
            val currentHeight = if (i == heights.size) 0 else heights[i]

            when {
                stack.isEmpty() || heights[stack.last()] <= currentHeight -> stack.add(i++)
                else -> {
                    val height = heights[stack.pop()]
                    val width = if (stack.isEmpty()) i else i - stack.peek() - 1
                    maxArea = maxOf(maxArea, height * width)
                }
            }
        }

        return maxArea
    }

    /**
     * |
     * | |   |
     * | |   | |
     * | | | | | |
     *[4 3 1 3 2 1]
     *[0 1 2 3 4 5 ]
     *   ^
     * stack = []
     * pop = 0 , height = heights[pop]= 4
     * peek_after_pop = 1
     * width =
     */

    fun maximalRectangle(matrix: Array<CharArray>): Int {
        if (matrix.isEmpty()) return 0

        val dp = IntArray(matrix[0].size)
        return matrix.fold(0) { maxArea, row ->
            row.forEachIndexed { index, value ->
                dp[index] = if (value == '1') dp[index] + 1 else 0
            }
            maxOf(maxArea, largestRectangleArea(dp))
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val testCase = MaximalRectangle()
            println(testCase.maximalRectangle(
                arrayOf(
                    charArrayOf('1', '0', '0'),
                    charArrayOf('1', '0', '0'),
                    charArrayOf('1', '0', '1'),
                    charArrayOf('1', '1', '1'),
                    charArrayOf('1', '1', '1')
                )
            ))
        }
    }
}
