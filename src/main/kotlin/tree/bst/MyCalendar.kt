import java.util.*

class MyCalendar() {
    private val calender = TreeMap<Int, Int>()

    fun book(startTime: Int, endTime: Int): Boolean {
        val prev = calender.floorEntry(startTime)
        val next = calender.ceilingEntry(startTime)

        val hasOverlapWithPrev = prev?.let { startTime < it.value } ?: false
        val hasOverlapWithNext = next?.let { it.key < endTime }  ?: false

        return when {
            hasOverlapWithPrev || hasOverlapWithNext -> false
            else -> {
                calender[startTime] = endTime
                true
            }
        }
    }

}

/**
 * Your MyCalendar object will be instantiated and called as such:
 * var obj = MyCalendar()
 * var param_1 = obj.book(startTime,endTime)
 */