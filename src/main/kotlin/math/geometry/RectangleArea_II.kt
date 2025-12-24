package math.geometry

/**
 * This required co-ordinate compression
 */
class RectangleArea_II {
    fun rectangleArea(rectangles: Array<IntArray>): Int {
        val MOD = 1_000_000_007L

        // 1. Collect all unique X coordinates to define our vertical strips
        val xCoords = rectangles.flatMap { listOf(it[0], it[2]) }.distinct().sorted()

        var totalArea = 0L

        // 2. Iterate through each vertical strip [xCoords[i], xCoords[i+1]]
        for (i in 0 until xCoords.size - 1) {
            val width = (xCoords[i + 1] - xCoords[i]).toLong()
            if (width == 0L) continue

            // 3. Find all rectangles that cover this vertical strip
            val activeYIntervals = rectangles
                .filter { it[0] <= xCoords[i] && it[2] >= xCoords[i + 1] }
                .map { it[1] to it[3] }
                .sortedBy { it.first }

            // 4. Calculate the union of Y intervals (The 1D sub-problem)
            var currentYHeight = 0L
            var lastY = -1

            for ((yStart, yEnd) in activeYIntervals) {
                val actualStart = maxOf(lastY, yStart)
                if (yEnd > actualStart) {
                    currentYHeight += (yEnd - actualStart)
                    lastY = yEnd
                }
            }

            totalArea = (totalArea + (width * currentYHeight)) % MOD
        }

        return totalArea.toInt()
    }
}

