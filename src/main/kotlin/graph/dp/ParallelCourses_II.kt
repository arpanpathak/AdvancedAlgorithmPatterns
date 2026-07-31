package graph.dp

class ParallelCourses_II {
    fun minNumberOfSemesters(n: Int, relations: Array<IntArray>, k: Int): Int {
        // prereq[i] = bitmask where each bit represents a prerequisite course for course i
        // Example: if prereq[3] = 5 (binary 101), then course 3 requires courses 0 and 2
        val prereq = IntArray(n)

        // Build prerequisite bitmasks from the relations array
        for ((prev, next) in relations) {
            // Convert to 0-based indexing and set the prerequisite bit
            prereq[next - 1] = prereq[next - 1] or (1 shl (prev - 1))
        }

        // dp[state] = minimum number of semesters needed to complete the courses represented by 'state'
        // Initialize with 'n' (maximum possible) since we're looking for minimum
        val dp = IntArray(1 shl n) { n }
        dp[0] = 0  // Base case: 0 semesters needed to complete 0 courses

        // Iterate through all possible states (combinations of completed courses)
        for (state in 0 until (1 shl n)) {
            // Skip unreachable states
            if (dp[state] == n) continue

            // Find all courses that are available to take in the next semester:
            // 1. Course not already taken ((state and (1 shl i)) == 0)
            // 2. All prerequisites satisfied ((prereq[i] and state) == prereq[i])
            var available = 0
            for (i in 0 until n) {
                if ((state and (1 shl i)) == 0 && (prereq[i] and state) == prereq[i]) {
                    available = available or (1 shl i)  // Mark course as available
                }
            }

            // Try all possible subsets of available courses we can take in one semester
            // This clever trick iterates through all subsets of 'available'
            var subset = available
            while (subset > 0) {
                // Only consider subsets with at most k courses
                if (subset.countOneBits() <= k) {
                    // New state after taking this subset of courses
                    val newState = state or subset
                    // Update minimum semesters needed to reach newState
                    dp[newState] = minOf(dp[newState], dp[state] + 1)
                }
                // Get next subset: (subset - 1) & available gives the next smaller subset
                subset = (subset - 1) and available
            }
        }

        // Return minimum semesters needed to complete all courses (state where all bits are 1)
        return dp[(1 shl n) - 1]
    }
}
