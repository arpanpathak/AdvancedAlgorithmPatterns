package math.geometry

import kotlin.math.atan2

/**
 * This is the classic Convex Hull problem.
 * We are solving it using Graham's Scan Algorithm.
 *
 * The approach involves:
 *
 * 1. **Sort the points by polar angle**:
 *    We first sort the points by polar angle (angle wrt x-axis) relative to
 *    the lowest point (with smallest y-coordinate). If there's a tie,
 *    we use the leftmost point as a reference.
 *
 * 2. **Construct the Convex Hull**:
 *    We initialize an empty set `convex_hull = {}` to hold the result.
 *    Then, we iterate over all points in sorted order.
 *    For each point, we maintain the invariant that all points in the
 *    convex hull form a counterclockwise path.
 *
 *    To ensure that, for each point:
 *      - Add the point to the convex hull.
 *      - While the last three points in the convex hull do not make a left turn
 *        (i.e., counterclockwise orientation), we remove the second last point.
 *
 * 3. **Orientation Test**:
 *    We use the orientation test to determine the direction formed by three
 *    points `p`, `q`, `r`. The test returns:
 *      - `0` if the points are collinear,
 *      - `1` if they form a clockwise turn,
 *      - `-1` if they form a counterclockwise turn (left turn).
 *
 * 4. **Final Convex Hull**:
 *    Once the iteration is complete, the set `convex_hull` will contain the
 *    points on the boundary of the convex hull in counterclockwise order.
 *
 *
 * Algorithm (Pseudo-code):
 *
 * INPUT:
 *   - Set of `n` points, P = {p1, p2, ..., pn} where each point has coordinates (x, y)
 *
 * OUTPUT:
 *   - Convex hull set, CH = {p1, p2, ..., pk} where k <= n and all points form the convex boundary
 *
 * Algorithm:
 *
 * 1. Find the reference point `p0` such that:
 *      p0 = point with the smallest y-coordinate (break ties with the smallest x-coordinate)
 *
 * 2. Sort all points by polar angle in counterclockwise order with respect to `p0`
 *
 * 3. Initialize an empty stack `S`
 *    S = {p0, p1, p2}  // Start with the reference point and two other points
 *
 * 4. For each point `pi` in the sorted list (i from 3 to n):
 *
 *    4.1 While the last three points in the stack do not form a counterclockwise turn:
 *         Remove the second-last point from the stack.
 *
 *    4.2 Add the current point `pi` to the stack `S`.
 *
 * 5. Return the stack `S` as the convex hull.
 *
 * Mathematical Helper:
 * - Orientation(p, q, r):
 *    val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)
 *    if val > 0: return 1   // Clockwise
 *    if val < 0: return -1  // Counterclockwise
 *    return 0               // Collinear
 *
 * Complexity:
 * - Sorting the points takes O(n log n).
 * - Building the hull takes O(n).
 * - Total time complexity: O(n log n).
 */


class ErectTheFence_ConvexHull {

    data class Point(val x: Int, val y: Int)

    fun outerTrees(points: Array<IntArray>): Array<IntArray> {
        // Convert input to Point objects
        val pointList = points.map { Point(it[0], it[1]) }.toList()

        // Find the point with the lowest y-coordinate
        val lowest = pointList.minByOrNull { it.y } ?: return arrayOf()

        // Sort points based on polar angle with respect to the lowest point
        val sortedPoints = pointList.sortedWith(compareBy({ atan2((it.y - lowest.y).toDouble(), (it.x - lowest.x).toDouble()) }, { it.x }))

        // Initialize the hull with the first two points
        val hull = mutableListOf<Point>()
        for (p in sortedPoints) {
            while (hull.size >= 2 && crossProduct(hull[hull.size - 2], hull[hull.size - 1], p) < 0) {
                hull.removeAt(hull.size - 1) // Remove the last point if it's a right turn
            }
            hull.add(p)
        }

        // Remove duplicates, which can happen with collinear points
        val uniqueHull = hull.distinct()

        // Convert Point objects back to IntArray
        return uniqueHull.map { intArrayOf(it.x, it.y) }.toTypedArray()
    }

    // Helper function to calculate the cross product of three points
    fun crossProduct(a: Point, b: Point, c: Point): Int {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
    }

}
