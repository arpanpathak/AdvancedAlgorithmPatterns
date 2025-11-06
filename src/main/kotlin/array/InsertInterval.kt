package array

class InsertInterval {
    fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
        val result = mutableListOf<IntArray>()
        var i = 0
        val n = intervals.size

        // Add all intervals that end before newInterval starts (no overlap)
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i])
            i++
        }

        // Sub-Merge all overlapping intervals with newInterval
        val mergedInterval = newInterval.copyOf()
        while (i < n && intervals[i][0] <= mergedInterval[1]) {
            mergedInterval[0] = minOf(mergedInterval[0], intervals[i][0])
            mergedInterval[1] = maxOf(mergedInterval[1], intervals[i][1])
            i++
        }
        result.add(mergedInterval)

        // Add all remaining intervals that start after newInterval ends
        while (i < n) {
            result.add(intervals[i])
            i++
        }

        return result.toTypedArray()
    }
}