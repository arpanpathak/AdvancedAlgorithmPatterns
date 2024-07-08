package array.greedy

class NonOverlappingIntervals {
    fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
        intervals.sortWith(compareBy { it[0] })

        var count = 0
        var prevEnd = intervals[0][1]

            // Start from the second interval
            for (i in 1 until intervals.size) {
                val interval = intervals[i]
                if (interval[0] < prevEnd) {
                    count++
                    prevEnd = minOf(prevEnd, interval[1])
                } else {
                    prevEnd = interval[1]
                }
            }

        return count
    }
}