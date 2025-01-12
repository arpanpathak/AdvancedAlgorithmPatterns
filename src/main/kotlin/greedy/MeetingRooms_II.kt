package greedy

class MeetingRooms_II {
    fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
        intervals.sortBy { it[0] }  // Sort by the start time of each interval

        for (i in 0 until intervals.size - 1) {
            if (intervals[i][1] > intervals[i + 1][0]) {
                return false  // If current meeting ends after the next meeting starts, return false
            }
        }
        return true  // No overlaps, return true
    }
}