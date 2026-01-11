package graph

class ParallelCourses_II_FunctionalProgramming {
    private fun Int.isNotDone(course: Int) = (this and (1 shl course)) == 0
    private fun Int.hasPrereqsMet(prereqsMask: Int) = (this and prereqsMask) == prereqsMask
    private fun Int.addCourses(mask: Int) = this or mask

    fun minNumberOfSemesters(n: Int, relations: Array<IntArray>, k: Int): Int {
        val allCoursesMask = (1 shl n) - 1

        // Build a pre-requisites bitmask array where prereqs[i] = bitmask prereqs
        val prereqs = relations.fold(IntArray(n)) { acc, (prev, next) ->
            acc.apply { this[next - 1] = this[next - 1] or (1 shl (prev - 1)) }
        }

        val cache = mutableMapOf<Int, Int>()

        fun getValidSubsets(mask: Int): List<Int> = buildList {
            var subset = mask
            while (subset > 0) {
                subset.takeIf { it.countOneBits() <= k }?.let { add(it) }
                subset = (subset - 1) and mask
            }
        }

        fun dfs(completedMask: Int): Int = cache.getOrPut(completedMask) {
            when (completedMask) {
                allCoursesMask -> 0
                else -> {
                    // 1. Generate bitmasks of available courses
                    val availableMask = (0 until n)
                        .asSequence()
                        .filter { course ->
                            completedMask.isNotDone(course) and completedMask.hasPrereqsMet(prereqs[course])
                        }
                        .fold(0) { acc, course -> acc or (1 shl course) }

                    // 2. Generate all valid subsets from availableMask where length of subset <= k. Take all of these courses in next semester and discover whichones yields the minimal time
                    getValidSubsets(availableMask).minOf { subset ->
                        1 + dfs(completedMask.addCourses(subset))
                    }
                }
            }
        }

        return dfs(0)
    }
}
