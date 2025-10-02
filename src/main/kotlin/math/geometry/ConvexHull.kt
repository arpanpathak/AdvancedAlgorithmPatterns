package math.geometry

import kotlin.collections.ArrayDeque

data class Point(val x: Int, val y: Int)

class ConvexHull {
    fun outerTrees(trees: Array<IntArray>): Array<IntArray> {
        if (trees.size <= 3) return trees

        val points = trees.map { Point(it[0], it[1]) }.sortedWith(compareBy({ it.x }, { it.y }))

        val lower = buildHalfHull(points)
        val upper = buildHalfHull(points.reversed())

        return (lower + upper).toSet().map { intArrayOf(it.x, it.y) }.toTypedArray()
    }

    private fun buildHalfHull(points: List<Point>): List<Point> {
        val hull = ArrayDeque<Point>()
        for (point in points) {
            while (hull.size >= 2 && cross(hull[hull.size - 2], hull.last(), point) < 0) {
                hull.removeLast()
            }
            hull.addLast(point)
        }
        return hull.toList()
    }

    private fun cross(a: Point, b: Point, c: Point): Int =
        (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
}