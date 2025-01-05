package math

import kotlin.math.abs

class DetectSquares() {
    private val pointCount = mutableMapOf<Pair<Int, Int>, Int>()

    fun add(point: IntArray) {
        val key = point[0] to point[1]
        pointCount[key] = pointCount.getOrDefault(key, 0) + 1
    }

    fun count(point: IntArray): Int {
        val (x, y) = point

        var squareCount = 0

        pointCount.forEach { (p, count) ->
            val (px, py) = p
            if (x != px && y != py
                && abs(x - px) == abs(y - py)) {
                squareCount += count * (pointCount[px to y] ?: 0) * (pointCount[x to py] ?: 0)
            }
        }

        return squareCount
    }
}