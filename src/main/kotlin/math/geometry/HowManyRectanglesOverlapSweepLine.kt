package math.geometry

data class Rectangle(
    val bottomX: Int,
    val bottomY: Int,
    val topX: Int,
    val topY: Int
)

enum class EventType(val value: Int) {
    START(1),  // Sweep line enters a rectangle
    END(-1)    // Sweep line exits a rectangle
}

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

/**
 * Checks for 1D overlap on the Y-axis only.
 */
private fun isYOverlap(r1: Rectangle, r2: Rectangle): Boolean {
    // Non-overlap in Y-axis:
    val isNonOverlappingY = (r1.topY <= r2.bottomY         // R1 is entirely below R2
            || r1.bottomY >= r2.topY)    // R1 is entirely above R2

    return !isNonOverlappingY
}

/**
 * Finds overlapping rectangle pairs using optimized sweep-line algorithm.
 * Uses TreeSet properties for efficient range queries.
 */
fun countOverlappingPairsSweepLine(rectangles: List<Rectangle>): Int {
    if (rectangles.size < 2) return 0

    // Create timeline: each rectangle has START (left edge) and END (right edge) events
    val events = rectangles.flatMap { rect ->
        listOf(
            SweepEvent(rect.bottomX, rect, EventType.START),
            SweepEvent(rect.topX, rect, EventType.END)
        )
    }.sorted()

    var overlapCount = 0

    // Active rectangles sorted by bottomY - we can use subSet for range queries!
    val activeRects = sortedSetOf<Rectangle>(compareBy { it.bottomY })

    for (event in events) {
        when (event.type) {
            EventType.START -> {
                val currentRect = event.rect

                // Use BST range query: get all rectangles with bottomY < currentRect.topY
                // This gives us all rectangles that could potentially overlap from below
                val potentialFromBelow = activeRects.headSet(
                    Rectangle(0, currentRect.topY, 0, 0) // Dummy rectangle for comparison
                )

                // Check each candidate using our beautiful overlap function
                overlapCount += potentialFromBelow.count { activeRect ->
                    // Only count if the active rectangle's topY > currentRect's bottomY
                    // (meaning it could overlap from above)
                    activeRect.topY > currentRect.bottomY &&
                            isYOverlap(currentRect, activeRect)
                }

                activeRects.add(currentRect)
            }
            EventType.END -> {
                activeRects.remove(event.rect)
            }
        }
    }
    return overlapCount
}

fun main() {
    val rects = listOf(
        Rectangle(bottomX = 0, bottomY = 5, topX = 5, topY = 10),
        Rectangle(bottomX = 3, bottomY = 3, topX = 7, topY = 7),
        Rectangle(bottomX = 6, bottomY = 2, topX = 10, topY = 6),
        Rectangle(bottomX = 15, bottomY = 10, topX = 20, topY = 15)
    )

    println("Total overlapping pairs: ${countOverlappingPairsSweepLine(rects)}")
    // Output: 2
}