package graph.topological_sort

class CourseSchedule_II {
    private var graph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private lateinit var status: Array<NodeStatus>
    private lateinit var result: IntArray
    private var index: Int = 0

    private enum class NodeStatus { UNVISITED, EXPLORING, DONE }

    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        // Initialize graph, status, and result array
        status = Array(numCourses) { NodeStatus.UNVISITED }
        result = IntArray(numCourses)
        index = numCourses - 1

        // Build the graph
        prerequisites.forEach { (course, prereq) ->
            graph.getOrPut(prereq) { mutableListOf() }.add(course)
        }

        // Attempt to find a topological order
        if (hasCycle(numCourses)) {
            return intArrayOf()
        }

        return result
    }

    private fun hasCycle(numCourses: Int): Boolean {
        // Nested DFS function for checking cycles and building the result
        fun dfs(node: Int): Boolean {
            when (status[node]) {
                // If an already visited node re-appears then it means we looped back to an exploring node
                NodeStatus.EXPLORING -> return true
                // If an already done node re-appears it doesn't mean cycle rather it means
                // we fully explored the node previously and found no cycle in the path.
                NodeStatus.DONE -> return false
                else -> {
                    status[node] = NodeStatus.EXPLORING
                    graph[node]?.forEach { neighbor ->
                        if (dfs(neighbor)) return true
                    }

                    status[node] = NodeStatus.DONE
                    result[index--] = node
                    return false
                }
            }
        }

        for (i in 0 until numCourses) {
            if (status[i] == NodeStatus.UNVISITED) {
                if (dfs(i)) {
                    return true
                }
            }
        }
        return false
    }
}