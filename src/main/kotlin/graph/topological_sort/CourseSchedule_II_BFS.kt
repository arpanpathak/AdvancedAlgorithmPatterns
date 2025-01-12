package graph.topological_sort

class CourseSchedule_II_BFS {
    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        val graph = Array<MutableList<Int>>(numCourses) { mutableListOf() }
        val inDegree = IntArray(numCourses)
        val result = mutableListOf<Int>()

        // Build the graph and calculate in-degrees
        prerequisites.forEach { (course, preReq) ->
            graph[preReq].add(course)
            inDegree[course]++
        }

        // Initialize queue with courses having no prerequisites
        val queue = ArrayDeque<Int>().apply { inDegree.indices.filter { inDegree[it] == 0 }.forEach { add(it) } }

        // Perform BFS (Kahn's Algorithm)
        while (queue.isNotEmpty()) {
            queue.removeFirst().also {
                result.add(it)
                graph[it].forEach { neighbor ->
                    if (--inDegree[neighbor] == 0) queue.add(neighbor)
                }
            }
        }

        return if (result.size == numCourses) result.toIntArray() else intArrayOf()
    }
}
