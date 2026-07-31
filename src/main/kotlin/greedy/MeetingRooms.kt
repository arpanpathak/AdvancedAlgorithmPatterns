package greedy

class MeetingRooms {
    fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
        intervals.sortBy { it[0] } // Sorting by the start time

        for(i in 0 until intervals.size - 1) {
            if(intervals[i + 1][0] < intervals[i][1])
                return false
        }

        return true
    }
}
