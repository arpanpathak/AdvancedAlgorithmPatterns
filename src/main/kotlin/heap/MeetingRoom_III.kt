package heap

import java.util.*

class MeetingRoom_III {
    data class Room(val endTime: Long, val index: Int)

    fun mostBooked(n: Int, meetings: Array<IntArray>): Int {
        // Sort meetings by start time
        meetings.sortWith(compareBy { it[0] })

        val roomUsage = IntArray(n)
        val busyRooms = PriorityQueue<Room>(compareBy({ it.endTime }, { it.index }))
        val availableRooms = PriorityQueue<Int>()

        // Initialize available rooms
        for (i in 0 until n) availableRooms.add(i)

        for ((start, end) in meetings.map { it[0].toLong() to it[1].toLong() }) {
            val duration = end - start

            // Free up any rooms that are now available
            while (busyRooms.isNotEmpty() && busyRooms.peek().endTime <= start) {
                availableRooms.add(busyRooms.poll().index)
            }

            if (availableRooms.isNotEmpty()) {
                // If a room is available, use it
                val room = availableRooms.poll()
                busyRooms.add(Room(start + duration, room))
                roomUsage[room]++
            } else {
                // If all rooms are busy, use the one that will become available first
                val earliestRoom = busyRooms.poll()
                // The new end time is the earliest available time plus the duration
                busyRooms.add(Room(earliestRoom.endTime + duration, earliestRoom.index))
                roomUsage[earliestRoom.index]++
            }
        }

        // Find the room with maximum usage (if tied, return the smallest index)
        var maxUsage = -1
        var result = -1

        for (i in 0 until n) {
            if (roomUsage[i] > maxUsage) {
                maxUsage = roomUsage[i]
                result = i
            }
        }

        return result
    }
}
