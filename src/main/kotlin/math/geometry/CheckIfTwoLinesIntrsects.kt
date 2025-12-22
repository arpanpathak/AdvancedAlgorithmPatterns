package math.geometry

data class Coordinate2D(val x: Long, val y: Long) {
    operator fun minus(other: Coordinate2D) = Vector(this.x - other.x, this.y - other.y)
}

data class Vector(val dx: Long, val dy: Long) {
    infix fun cross(other: Vector): Long = this.dx * other.dy - this.dy * other.dx
}

data class LineSegment(val start: Coordinate2D, val end: Coordinate2D) {
    infix fun intersects(other: LineSegment): Boolean {
        val a = this.start
        val b = this.end
        val c = other.start
        val d = other.end

        // Orientation of points of segment CD relative to segment AB
        val abc = getOrientation(a, b, c)
        val abd = getOrientation(a, b, d)

        // Orientation of points of segment AB relative to segment CD
        val cda = getOrientation(c, d, a)
        val cdb = getOrientation(c, d, b)

        /**
         * 1. GENERAL CASE: "Straddling"
         * Two segments intersect if the endpoints of one segment lie on opposite
         * sides of the other segment's line.
         * * Logic: (abc != abd) means C and D are on opposite sides of line AB.
         * (cda != cdb) means A and B are on opposite sides of line CD.
         * If both are true, the segments MUST cross.
         */
        if (abc != abd && cda != cdb) return true

        /**
         * 2. SPECIAL COLLINEAR CASES:
         * If any orientation is 0, the three points are collinear (on the same infinite line).
         * Being on the same line doesn't guarantee an intersection of the segments.
         * * We must verify if the "floating" point (e.g., C) lies within the 1D
         * projection of the other segment (e.g., AB). We check this using a
         * Bounding Box: the point is on the segment if its X and Y coordinates
         * are between the min and max coordinates of the segment's endpoints.
         *
         * Cases covered:
         * - Overlapping: Segments share a common sub-segment.
         * - Touching: One endpoint lies exactly on the other segment.
         * - Containment: One segment is completely inside the other.
         */
        return when {
            abc == 0 && onSegment(c, a, b) -> true // Is C on segment AB?
            abd == 0 && onSegment(d, a, b) -> true // Is D on segment AB?
            cda == 0 && onSegment(a, c, d) -> true // Is A on segment CD?
            cdb == 0 && onSegment(b, c, d) -> true // Is B on segment CD?
            else -> false
        }
    }

    companion object {
        fun getOrientation(p: Coordinate2D, q: Coordinate2D, r: Coordinate2D): Int {
            val pq = q - p
            val qr = r - q
            val cross = pq cross qr
            return when {
                cross > 0 -> -1 // CCW (Left Turn)
                cross < 0 -> 1  // CW (Right Turn)
                else -> 0       // Collinear
            }
        }

        private fun onSegment(p: Coordinate2D, start: Coordinate2D, end: Coordinate2D) =
            // Bounding box check: Is p between start and end?
            p.x <= maxOf(start.x, end.x) && p.x >= minOf(start.x, end.x) &&
                    p.y <= maxOf(start.y, end.y) && p.y >= minOf(start.y, end.y)

    }
}
