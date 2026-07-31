package array

import java.util.*

data class Point(val row: Int, val col: Int)

class DiagonalTraverse_II {
    fun findDiagonalOrder(nums: List<List<Int>>): List<Int> {
        val result = mutableListOf<Int>()
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        val visited = mutableSetOf<Pair<Int, Int>>()

        // Start with the first element
        queue.offer(0 to 0)

        while (queue.isNotEmpty()) {
            val (i, j) = queue.poll()
            result.add(nums[i][j])

            // Move to the next row (i+1, j)
            if (i + 1 < nums.size && j < nums[i + 1].size) {
                queue.offer(i + 1 to j)
                visited.add(i + 1 to j)
            }

            // Move to the next column (i, j+1)
            if (j + 1 < nums[i].size) {
                queue.offer(i to j + 1)
                visited.add(i to j + 1)
            }
        }

        return result
    }
}
