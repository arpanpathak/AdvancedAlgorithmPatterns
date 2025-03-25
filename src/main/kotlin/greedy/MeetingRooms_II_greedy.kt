package greedy

import java.util.*

class MeetingRooms_II_greedy {
    fun minMeetingRooms(intervals: Array<IntArray>): Int {
        intervals.sortBy { it[0] }

        val heap = PriorityQueue<Int>()

        for ((start, end) in intervals) {
            // If the current meeting starts after the earliest ending meeting, reuse the room
            if ( heap.isNotEmpty() && start >= heap.peek()) {
                heap.poll()
            }
            heap.offer(end)
        }

        return heap.size
    }
}