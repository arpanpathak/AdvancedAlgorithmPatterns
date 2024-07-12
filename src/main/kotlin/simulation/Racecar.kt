package simulation

import java.util.*

class Racecar {

    // Stack overflow too slow
    fun racecar(target: Int): Int {
        val dp = mutableMapOf<Pair<Int,Int>, Int>()
        fun dfs(pos: Int, speed: Int): Int {
            val key = Pair(pos, speed)
            return when {
                pos == target ->  0
                pos < - 10000 || pos > 10000 -> Int.MAX_VALUE
                key in dp -> dp[key]!!
                else -> {
                    val reverseSpeed = if (speed > 0) -1 else 1
                    1 + minOf(dfs(pos + speed, speed * 2), dfs(pos, reverseSpeed))
                }
            }.also { dp[key] = it }
        }

        return dfs(0, 1)
    }

    data class State(val position: Int, val speed: Int, val distance: Int = 0)

    fun racecar_dijkstra(target: Int): Int {
        val pq = PriorityQueue<State>(compareBy { it.distance })
        val visited = mutableSetOf<State>()

        pq.add(State(0, 1))
        var steps = 0

        while (pq.isNotEmpty()) {
            val curr = pq.poll()

            if (curr.position == target )
                return curr.distance

            // Accelerate forward
            val nextState = State(curr.position + curr.speed, curr.speed * 2, curr.distance + 1)

            if (nextState.position in -2*target..2*target && !visited.contains(nextState)) {
                visited.add(nextState)
                pq.add(nextState)
            }

            // Go Reverse gear
            val reverseSpeed = if (curr.speed > 0) -1 else 1
            val nextReverseState = State(curr.position, reverseSpeed, curr.distance + 1)

            if (!visited.contains(nextReverseState)) {
                visited.add(nextReverseState)
                pq.add(nextReverseState)
            }
        }

        return -1
    }


    fun racecar_bfs(target: Int): Int {
        val queue: Queue<State> = LinkedList()
        val visited = mutableSetOf<State>()

        queue.add(State(0, 1)) // Start at position 0 with speed 1
        visited.add(State(0, 1))

        var steps = 0

        while (queue.isNotEmpty()) {
            val size = queue.size

            for (i in 0 until size) {
                val current = queue.poll()

                // Check if we reached the target
                if (current.position == target) return steps

                // Accelerate
                val nextPosition = current.position + current.speed
                val nextSpeed = current.speed * 2

                val accelerateState = State(nextPosition, nextSpeed)
                if (nextPosition in -2 * target..2 * target && !visited.contains(accelerateState)) {
                    visited.add(accelerateState)
                    queue.add(accelerateState)
                }

                // Reverse
                val reverseSpeed = if (current.speed > 0) -1 else 1
                val reverseState = State(current.position, reverseSpeed)
                if (!visited.contains(reverseState)) {
                    visited.add(reverseState)
                    queue.add(reverseState)
                }
            }
            steps++
        }

        return -1 // Should not reach here
    }
}
