package array.greedy

internal class Solution {
    fun findMinArrowShots(points: Array<IntArray>): Int {
        if (points.isEmpty()) return 0

        points.sortBy { it[1] }

        // Atleast one arrow is requird,..
        var arrows = 1
        var firstEnd = points[0][1]
        for (point in points) {
            // Check if there is no overlap with last item
            if (firstEnd < point[0]) {
                arrows++
                firstEnd = point[1]
            }
        }
        return arrows
    }
}