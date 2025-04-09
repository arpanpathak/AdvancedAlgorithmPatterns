package heap

import java.util.*

class LongestHappyString {
    fun longestDiverseString(a: Int, b: Int, c: Int): String {
        // Use a priority queue to always get the character with highest remaining count
        val pq = PriorityQueue<Pair<Char, Int>> { p1, p2 -> p2.second - p1.second }

        // Add non-zero counts to the queue
        if (a > 0) pq.offer('a' to a)
        if (b > 0) pq.offer('b' to b)
        if (c > 0) pq.offer('c' to c)

        // Build the result
        return buildString {
            var lastChar = ' '
            var lastCount = 0

            while (pq.isNotEmpty()) {
                // Get the character with highest remaining count
                val (char, count) = pq.poll()

                // If we already have two consecutive of the same character,
                // we need to use the second highest count character
                if (lastChar == char && lastCount == 2) {
                    if (pq.isEmpty()) break

                    val (nextChar, nextCount) = pq.poll()
                    append(nextChar)

                    // Put the character back with reduced count
                    if (nextCount > 1) {
                        pq.offer(nextChar to nextCount - 1)
                    }

                    // Put the original highest character back
                    pq.offer(char to count)

                    // Reset last tracking for the new character
                    lastChar = nextChar
                    lastCount = 1
                } else {
                    // Add the highest count character
                    append(char)

                    // Update last tracking
                    if (lastChar == char) {
                        lastCount++
                    } else {
                        lastChar = char
                        lastCount = 1
                    }

                    // Put the character back with reduced count
                    if (count > 1) {
                        pq.offer(char to count - 1)
                    }
                }
            }
        }
    }
}
