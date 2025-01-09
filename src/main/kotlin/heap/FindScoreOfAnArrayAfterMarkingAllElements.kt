package heap

import java.util.*

class FindScoreOfAnArrayAfterMarkingAllElements {
    fun findScore(nums: IntArray): Long {
        val n = nums.size
        val marked = BooleanArray(n) { false }

        val minHeap = PriorityQueue<Pair<Int, Int>> { a, b ->
            if (a.first == b.first) a.second - b.second else a.first - b.first
        }

        nums.forEachIndexed { index, value -> minHeap.add(Pair(value, index)) }

        var score = 0L

        while (minHeap.isNotEmpty()) {
            val (value, index) = minHeap.poll()

            if (marked[index]) continue

            score += value

            marked[index] = true
            if (index > 0) marked[index - 1] = true
            if (index < n - 1) marked[index + 1] = true
        }

        return score
    }
}