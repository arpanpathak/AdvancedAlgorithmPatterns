package array.greedy

class MergeOverlappingIntervals {
    fun merge(intervals: Array<IntArray>): Array<IntArray> {
        // Sort the internals by start
        intervals.sortWith(compareBy { it[0] })
        var index = 0
        for (i in 1..intervals.lastIndex) {
            if (intervals[i][0] > intervals[index][1]) {
                intervals[++index] = intervals[i]
            } else {
                intervals[index][1] = maxOf(intervals[index][1], intervals[i][1])
            }
        }

        return intervals.copyOfRange(0, index + 1)
    }
}