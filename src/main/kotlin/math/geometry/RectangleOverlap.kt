package math.geometry

class RectangleOverlap {
    /**
     * Determines if two rectangles overlap using destructuring for clarity.
     * rec[0] is x-left, rec[1] is y-bottom, rec[2] is x-right, rec[3] is y-top.
     */
    fun isRectangleOverlap(rec1: IntArray, rec2: IntArray): Boolean {
        // Destructure rec1 into named variables:
        val (aX1, aY1, aX2, aY2) = rec1
        // Destructure rec2 into named variables:
        val (bX1, bY1, bX2, bY2) = rec2

        // X-axis Overlap Check: A is NOT left of B AND B is NOT left of A
        val xOverlap = aX1 < bX2 && bX1 < aX2

        // Y-axis Overlap Check: A is NOT below B AND B is NOT below A
        val yOverlap = aY1 < bY2 && bY1 < aY2

        // Overlap only occurs if both axes overlap
        return xOverlap && yOverlap
    }

    fun isRectangleOverlapEasy(rec1: IntArray, rec2: IntArray): Boolean {
        val (ax1, ay1, ax2, ay2) = rec1
        val (bx1, by1, bx2, by2) = rec2

        // Returns TRUE if they are DISJOINT (separated) on X OR Y.
        val isDisjoint = (minOf(ax2, bx2) <= maxOf(ax1, bx1)) ||
                (minOf(ay2, by2) <= maxOf(ay1, by1))

        // Overlap is the opposite of disjoint.
        return !isDisjoint
    }

    fun isRectangleOverlapClean(rec1: IntArray, rec2: IntArray): Boolean {
        val (Ax1, Ay1, Ax2, Ay2) = rec1
        val (Bx1, By1, Bx2, By2) = rec2

        return !(Bx1 >= Ax2 ||  // B is right of A
                Bx2 <= Ax1 ||  // B is left of A
                By1 >= Ay2 ||  // B is above A
                By2 <= Ay1)    // B is below A
    }
}
