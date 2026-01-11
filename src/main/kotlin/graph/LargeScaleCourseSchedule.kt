package graph

import java.util.PriorityQueue

class LargeScaleCourseScheduler {

    /**
     * Handles large n by prioritizing the "Critical Path" (longest chain).
     * Uses idiomatic (course, prereq) destructuring and in-degree management.
     */
    fun minNumberOfSemesters(n: Int, relations: Array<IntArray>, k: Int): Int {
        val adj = mutableMapOf<Int, MutableList<Int>>()
        val inDegree = IntArray(n + 1)

        // 1. Build adjacency list and in-degree count
        relations.forEach { (prereq, course) ->
            adj.getOrPut(prereq) { mutableListOf() }.add(course)
            inDegree[course]++
        }

        // 2. Calculate the "Depth" (Longest path to leaf) for each node
        // This is our heuristic for priority.
        val depths = calculateDepths(n, adj)

        // 3. Max-Heap prioritized by depth to ensure we unlock long chains early
        val available = PriorityQueue<Int>(compareByDescending { depths[it] }).apply {
            addAll((1..n).filter { inDegree[it] == 0 })
        }

        var semesters = 0
        while (available.isNotEmpty()) {
            semesters++

            // Extract up to k courses for this semester
            val currentSemesterCourses = mutableListOf<Int>()
            val numToTake = minOf(k, available.size)

            repeat(numToTake) {
                currentSemesterCourses.add(available.poll())
            }

            // 4. Update in-degrees and add newly unlocked courses to the queue
            for (course in currentSemesterCourses) {
                adj[course]?.forEach { neighbor ->
                    inDegree[neighbor]--
                    if (inDegree[neighbor] == 0) {
                        available.add(neighbor)
                    }
                }
            }
        }

        return semesters
    }

    private fun calculateDepths(n: Int, adj: Map<Int, List<Int>>): IntArray {
        val depth = IntArray(n + 1)
        val memo = mutableMapOf<Int, Int>()

        fun dfs(u: Int): Int = memo.getOrPut(u) {
            val children = adj[u] ?: return@getOrPut 1
            1 + (children.maxOfOrNull { dfs(it) } ?: 0)
        }

        for (i in 1..n) {
            depth[i] = dfs(i)
        }
        return depth
    }
}