package heap

import java.util.*

class IPO {
    data class Project(val capital: Int, val profit: Int)

    fun findMaximizedCapital(k: Int, w: Int, profits: IntArray, capital: IntArray): Int {
        val projects = capital.indices
            .map { Project(capital[it], profits[it]) }
            .sortedBy { it.capital }

        val maxHeap = PriorityQueue<Int>(compareByDescending { it })

        var currentCapital = w
        var i = 0

        repeat(k) {
            while (i < projects.size && projects[i].capital <= currentCapital)
                maxHeap.offer(projects[i++].profit)

            maxHeap.poll()?.let { currentCapital += it } ?: return currentCapital
        }

        return currentCapital
    }
}