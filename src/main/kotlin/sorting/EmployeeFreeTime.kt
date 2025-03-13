package sorting

class Interval(var start: Int, var end: Int)

class EmployeeFreeTime {
    fun employeeFreeTime(schedule: ArrayList<ArrayList<Interval>>): ArrayList<Interval> {
        val allIntervals = schedule.flatten().sortedBy { it.start }
        val freeTime = arrayListOf<Interval>()

        var prevEnd = allIntervals[0].end

        // Same as merging overlapping intervals
        for (i in 1 until allIntervals.size) {
            val (start, end) = allIntervals[i].start to allIntervals[i].end
            // No overlap, so it's a free time
            if (start > prevEnd) freeTime.add(Interval(prevEnd, start))
            prevEnd = maxOf(prevEnd, end)
        }
        return freeTime
    }
}
