package math.geometry

import kotlin.math.abs

class RectangleArea {
    fun computeArea(ax1: Int, ay1: Int, ax2: Int, ay2: Int, bx1: Int, by1: Int, bx2: Int, by2: Int): Int {
        val h1 = abs(ax1 - ax2)
        val w1 = abs(ay1 - ay2)

        val h2 = abs(bx1 - bx2)
        val w2 = abs(by1 - by2)

        // Calculate height and width of intersection
        val h = minOf(ax2, bx2) - maxOf(ax1, bx1)
        val w = minOf(ay2, by2) - maxOf(ay1, by1)

        var area = h1 * w1 + h2 * w2

        // If no overlap, return the current area
        if (minOf(ax2, bx2) <= maxOf(ax1, bx1) || minOf(ay2, by2) <= maxOf(ay1, by1)) {
            return area
        }

        // Subtract overlapping area
        area -= h * w

        return area

    }
}