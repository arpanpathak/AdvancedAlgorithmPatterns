package math

import java.util.*

class SlidingPuzzle {
    fun slidingPuzzle(board: Array<IntArray>): Int {
        val target = "123450"
        val start = board.flatMap { it.asIterable() }.joinToString("")
        if (start == target) return 0

        val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)

        val visited = mutableSetOf<String>()
        val queue: Queue<Pair<String, Int>> = LinkedList()
        queue.offer(start to 0)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val (current, moves) = queue.poll()
            if (current == target) return moves

            val zeroPos = current.indexOf('0')
            val row = zeroPos / 3
            val col = zeroPos % 3

            for ((dr, dc) in dirs) {
                val newRow = row + dr
                val newCol = col + dc
                if (newRow in 0..1 && newCol in 0..2) {
                    val newPos = newRow * 3 + newCol
                    val chars = current.toCharArray()
                    chars[zeroPos] = chars[newPos]
                    chars[newPos] = '0'
                    val nextState = String(chars)
                    if (!visited.contains(nextState)) {
                        visited.add(nextState)
                        queue.offer(nextState to moves + 1)
                    }
                }
            }
        }
        return -1
    }
}