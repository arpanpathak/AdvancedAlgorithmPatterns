package graph.cycle

class CourseSchedule_II {

    // Data structure for processing
    private val graph = mutableMapOf<Int, MutableList<Int>>()

    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        // Build the graph
        for (course in prerequisites) {
            val (child, preq) = course
            graph.getOrPut(preq) { mutableListOf() }.add(child)
        }

        return topologicalSort(numCourses)
    }

    private enum class Status {
        UNVISITED, EXPLORING, DONE
    }

    private fun topologicalSort(numCourses: Int): IntArray {
        val result = IntArray(numCourses)
        val status = Array(numCourses) { Status.UNVISITED }
        var idx = numCourses - 1

        // DFS function to detect cycles and perform topological sort
        fun hasCycleDfs(course: Int): Boolean {
            when (status[course]) {
                Status.EXPLORING -> return true
                Status.DONE -> return false
                else -> {
                    status[course] = Status.EXPLORING

                    graph[course]?.forEach{ preq -> if (hasCycleDfs(preq)) return true }

                    status[course] = Status.DONE
                    result[idx--] = course
                    return false
                }
            }
        }

        // Check each course for cycles and sort
        for (course in 0 until numCourses) {
            if (status[course] == Status.UNVISITED && hasCycleDfs(course)) {
                return intArrayOf() // Cycle detected, return empty array
            }
        }

        return result
    }
}