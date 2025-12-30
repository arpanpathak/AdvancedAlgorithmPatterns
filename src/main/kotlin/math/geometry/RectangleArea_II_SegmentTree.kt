package math.geometry

class RectangleArea_II_SegmentTree {
    data class Event(val x: Int, val type: Int, val y1: Int, val y2: Int)

    fun rectangleArea(rectangles: Array<IntArray>): Int {
        val mod = 1_000_000_007L
        val yCoords = rectangles.flatMap { listOf(it[1], it[3]) }.distinct().sorted().toIntArray()

        // Handle edge case of no rectangles
        if (yCoords.isEmpty()) return 0

        val events = rectangles.flatMap {
            listOf(Event(it[0], 1, it[1], it[3]), Event(it[2], -1, it[1], it[3]))
        }.sortedWith(compareBy({ it.x }, { it.type })) // Sort by x, then type

        val m = yCoords.size
        val count = IntArray(4 * m)
        val length = LongArray(4 * m)

        fun update(node: Int, start: Int, end: Int, qL: Int, qR: Int, delta: Int) {
            when {
                qL > end || qR < start -> return
                qL <= start && qR >= end -> count[node] += delta
                else -> {
                    val mid = (start + end) / 2
                    update(node * 2, start, mid, qL, qR, delta)
                    update(node * 2 + 1, mid + 1, end, qL, qR, delta)
                }
            }

            length[node] = when {
                count[node] > 0 -> (yCoords[end + 1] - yCoords[start]).toLong()
                start != end -> length[node * 2] + length[node * 2 + 1]
                else -> 0L
            }
        }

        var totalArea = 0L
        var prevX = events.first().x.toLong()

        for (event in events) {
            val width = event.x.toLong() - prevX
            // AREA = (WIDTH * HEIGHT) % MOD
            val currentStripArea = (width * length[1]) % mod
            totalArea = (totalArea + currentStripArea) % mod

            val qL = yCoords.binarySearch(event.y1)
            val qR = yCoords.binarySearch(event.y2) - 1

            if (qL <= qR) {
                update(1, 0, m - 2, qL, qR, event.type)
            }
            prevX = event.x.toLong()
        }

        return totalArea.toInt()
    }
}