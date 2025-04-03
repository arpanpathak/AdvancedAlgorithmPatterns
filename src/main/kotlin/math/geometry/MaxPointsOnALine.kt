package math.geometry

class MaxPointsOnALine {
    fun maxPoints(points: Array<IntArray>): Int {
        if (points.size <= 2) return points.size

        var maxPoints = 1
        for (i in points.indices) {
            var samePoints = 1 // include the current point (points[i])
            val slopeMap = mutableMapOf<Double, Int>()

            for (j in i + 1 until points.size) {
                val dx = points[j][0] - points[i][0]
                val dy = points[j][1] - points[i][1]

                if (dx == 0 && dy == 0) {
                    samePoints++
                    maxPoints = maxOf(maxPoints, samePoints)
                    continue
                }

                val slope = when {
                    dx == 0 -> Double.POSITIVE_INFINITY // vertical line
                    dy == 0 -> 0.0 // horizontal line
                    else -> dy.toDouble() / dx
                }

                slopeMap[slope] = slopeMap.getOrDefault(slope, 0) + 1
                maxPoints = maxOf(maxPoints, slopeMap[slope]!! + samePoints)
            }
        }

        return maxPoints
    }
}
