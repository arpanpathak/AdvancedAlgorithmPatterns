package graph.greedy

import java.util.*

class NetworkDelayTime {
    data class State(val time: Int, val node: Int)

    fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {
        val adj = times.groupBy({ it[0] }, { it[1] to it[2] })

        val dists = IntArray(n + 1) { Int.MAX_VALUE }.apply { this[k] = 0}

        val pq = PriorityQueue<State>(compareBy { it.time })
        pq.add(State(0, k))

        var maxDelay = 0
        var visitedCount = 0

        while (pq.isNotEmpty()) {
            val (time, u) = pq.poll()

            // If we've already found a better path to u, skip it
            if (time > dists[u]) continue

            // New shortest path finalized for node u
            visitedCount++
            maxDelay = maxOf(maxDelay, time)

            adj[u]?.forEach { (v, weight) ->
                val newTime = dists[u] + weight
                if (newTime < dists[v]) {
                    dists[v] = newTime
                    pq.add(State(newTime, v))
                }
            }
        }

        return if (visitedCount == n) maxDelay else -1
    }
}
