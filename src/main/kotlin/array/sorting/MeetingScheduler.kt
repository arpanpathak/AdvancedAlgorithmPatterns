package array.sorting

class MeetingScheduler {
    fun minAvailableDuration(slots1: Array<IntArray>, slots2: Array<IntArray>, duration: Int): List<Int> {

        slots1.sortBy { it[0] }
        slots2.sortBy { it[0] }

        var firstIdx = 0
        var secondIdx = 0

        while (firstIdx < slots1.size && secondIdx < slots2.size) {
            val maxStart = maxOf(slots1[firstIdx][0], slots2[secondIdx][0] )
            val minEnd =  minOf(slots1[firstIdx][1], slots2[secondIdx][1] )

            val window = minEnd - maxStart
            // Founda time slot which can accomodate the meeting...
            if (window >= duration)  {
                return listOf(maxStart, maxStart + duration)
            }

            when {
                slots1[firstIdx][1] < slots2[secondIdx][1] -> firstIdx++
                else -> secondIdx++
            }
        }

        return emptyList()
    }
}