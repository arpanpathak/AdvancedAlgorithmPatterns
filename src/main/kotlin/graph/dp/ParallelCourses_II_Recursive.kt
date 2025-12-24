package graph.dp

class Solution {
    fun minNumberOfSemesters(n: Int, relations: Array<IntArray>, k: Int): Int {
        // prereqMasks[j]: A bitmask where the set bits represent the courses (0-indexed)
        // that must be completed before course j can be taken.
        val prereqMasks = IntArray(n)
        relations.forEach { (prev, next) ->
            prereqMasks[next - 1] = prereqMasks[next - 1] or (1 shl (prev - 1))
        }

        val totalStates = 1 shl n
        val targetMask = totalStates - 1

        // dp[completedCoursesMask]: Minimum semesters required to finish the *remaining* courses.
        // Initialized to -1 to signify uncomputed states.
        val dp = IntArray(totalStates) { -1 }


        fun findMinSemesters(completedCoursesMask: Int): Int {
            when  {
                completedCoursesMask == targetMask -> return 0
                dp[completedCoursesMask] != -1 -> return dp[completedCoursesMask]
            }

            // 1. Determine which courses are currently available to take.
            var availableCoursesMask = 0
            for (j in 0 until n) {
                val jBit = 1 shl j

                // Course j is not completed yet AND all prerequisites are met.
                val notCompleted = (completedCoursesMask and jBit) == 0
                val prereqsMet = (completedCoursesMask and prereqMasks[j]) == prereqMasks[j]

                if (notCompleted && prereqsMet) {
                    availableCoursesMask = availableCoursesMask or jBit
                }
            }

            // Initialize minimum semesters for this state to an impossible value.
            var minSemesters = n + 1

            // 2. Iterate over all valid subsets of availableCoursesMask to take in this semester.
            var coursesTakenThisSemester = availableCoursesMask

            while (coursesTakenThisSemester > 0) {
                if (coursesTakenThisSemester.countOneBits() <= k) {
                    val nextCompletedMask = completedCoursesMask or coursesTakenThisSemester

                    // Recurrence: 1 (for the current semester) + minimum semesters from the next state.
                    val semesters = 1 + findMinSemesters(nextCompletedMask)
                    minSemesters = minOf(minSemesters, semesters)
                }

                coursesTakenThisSemester = (coursesTakenThisSemester - 1) and availableCoursesMask
            }

            return minSemesters.also { dp[completedCoursesMask] = it }
        }

        return findMinSemesters(0)
    }
}