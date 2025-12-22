package math.geometry.interval

class FindingNumberOfVisibleMountains {
    data class Mountain(val start: Int, val end: Int)

    fun visibleMountains(peaks: Array<IntArray>): Int {
        // 1. Convert 2D peaks to 1D ground intervals [start, end].
        // WHY (x-y, x+y): Because the mountain is a 45-45-90 triangle,
        // the horizontal distance from the peak center (x) to the base
        // corners is exactly equal to the height (y).
        // Left corner = center - height; Right corner = center + height.
        val counts = peaks
            .map { (x, y) -> Mountain(x - y, x + y) }
            .groupingBy { it }
            .eachCount()

        // 2. Sorting Strategy:
        // - PRIMARY SORT (start ASC): We move left-to-right so we only
        //   have to worry about the right edge (end) to check for coverage.
        // - SECONDARY SORT (end DESC): If two mountains start at the same
        //   point, the TALLER mountain MUST come first. This ensures
        //   the larger one 'shadows' the smaller ones immediately.
        val sortedIntervals = counts.keys
            .sortedWith(compareBy<Mountain> { it.start }.thenByDescending { it.end })

        var maxReach = Int.MIN_VALUE
        var visibleCount = 0

        for (mtn in sortedIntervals) {
            val (start, end) = mtn

            // 3. The Coverage Logic:
            // Since we sorted by start ASC, we know any previous mountain
            // started before or at the same position as the current one.
            // If the current mountain's 'end' is <= 'maxReach', it is
            // physically trapped inside the footprint of a previous mountain.
            if (end > maxReach) {
                // 4. The Duplicate Exception:
                // Per problem rules, if two mountains are identical, they
                // perfectly overlap and hide each other's peaks.
                if (counts[mtn] == 1) {
                    visibleCount++
                }
                // Update 'maxReach' to the furthest right edge seen so far.
                // This becomes the new "shadow" boundary.
                maxReach = end
            }
        }

        return visibleCount
    }
}