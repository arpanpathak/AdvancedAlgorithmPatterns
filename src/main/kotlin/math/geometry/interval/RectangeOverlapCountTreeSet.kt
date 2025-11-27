package math.geometry.interval

class RectangeOverlapCountTreeSet {
    data class Rectangle(
        val bottomX: Int,
        val bottomY: Int,
        val topX: Int,
        val topY: Int
    )

    enum class EventType(val value: Int) { START(1), END(-1) }

    data class SweepEvent(
        val x: Int,
        val rect: Rectangle,
        val type: EventType
    ) : Comparable<SweepEvent> {
        override fun compareTo(other: SweepEvent): Int {
            if (this.x != other.x) return this.x.compareTo(other.x)
            return other.type.value.compareTo(this.type.value)
        }
    }

    data class Interval(
        val rect: Rectangle,
        val start: Int,  // bottomY
        val end: Int     // topY
    )

    class IntervalTree {
        private val intervals = sortedSetOf<Interval>(compareBy { it.start })
        private var globalMaxEnd: Int = Int.MIN_VALUE

        fun insert(rect: Rectangle) {
            val interval = Interval(rect, rect.bottomY, rect.topY)
            intervals.add(interval)
            globalMaxEnd = maxOf(globalMaxEnd, rect.topY)
        }

        fun remove(rect: Rectangle) {
            val interval = Interval(rect, rect.bottomY, rect.topY)
            intervals.remove(interval)
            globalMaxEnd = intervals.maxOfOrNull { it.end } ?: Int.MIN_VALUE
        }

        fun queryOverlaps(rect: Rectangle): List<Rectangle> {
            val results = mutableListOf<Rectangle>()

            // Early exit optimization
            if (globalMaxEnd < rect.bottomY) return results

            // Find all intervals that start before this rectangle's topY
            val maxStart = rect.topY
            val dummy = Interval(Rectangle(0, 0, 0, 0), maxStart, maxStart)
            val candidates = intervals.headSet(dummy)

            // Check Y-overlap for each candidate
            for (candidate in candidates) {
                if (candidate.end > rect.bottomY && candidate.start < rect.topY) {
                    results.add(candidate.rect)
                }
            }

            return results
        }
    }

    fun countOverlappingPairs(rectangles: List<Rectangle>): Int {
        if (rectangles.size < 2) return 0

        val events = rectangles.flatMap { rect ->
            listOf(
                SweepEvent(rect.bottomX, rect, EventType.START),
                SweepEvent(rect.topX, rect, EventType.END)
            )
        }.sorted()

        var overlapCount = 0
        val intervalTree = IntervalTree()

        for (event in events) {
            when (event.type) {
                EventType.START -> {
                    overlapCount += intervalTree.queryOverlaps(event.rect).size
                    intervalTree.insert(event.rect)
                }
                EventType.END -> {
                    intervalTree.remove(event.rect)
                }
            }
        }
        return overlapCount
    }

    companion object {
        @JvmStatic
        fun main() {
            val testInstance = RectangeOverlapCountTreeSet()

            val rects = listOf(
                Rectangle(bottomX = 0, bottomY = 5, topX = 5, topY = 10),
                Rectangle(bottomX = 3, bottomY = 3, topX = 7, topY = 7),
                Rectangle(bottomX = 6, bottomY = 2, topX = 10, topY = 6),
                Rectangle(bottomX = 15, bottomY = 10, topX = 20, topY = 15)
            )

            println("Total overlapping pairs: ${testInstance.countOverlappingPairs(rects)}")
        }
    }
}