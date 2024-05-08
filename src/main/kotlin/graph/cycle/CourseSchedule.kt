package graph.cycle

class CourseSchedule {
    private var graph: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private lateinit var status: Array<NodeStatus>
    private enum class NodeStatus { UNVISITED, EXPLORING, DONE }

    private fun hasCycle(numCourses: Int): Boolean {
        // Define dfs function inside hasCycle to limit its scope
        fun dfs(node: Int): Boolean = when (status[node]) {
            NodeStatus.EXPLORING -> true
            NodeStatus.DONE -> false
            NodeStatus.UNVISITED -> {
                status[node] = NodeStatus.EXPLORING
                graph[node]?.forEach {
                    if (dfs(it)) return true
                }
                status[node] = NodeStatus.DONE
                false
            }
        }

        for (i in 0 until numCourses) {
            if (status[i] == NodeStatus.UNVISITED && dfs(i)) {
                return true
            }
        }
        return false
    }

    fun canFinish(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
        // Reset graph for each invocation to handle multiple calls
        graph = mutableMapOf()
        prerequisites.forEach { (course, prereq) ->
            graph.getOrPut(prereq) { mutableListOf() }.add(course)
        }

        status = Array(numCourses) { NodeStatus.UNVISITED }

        return !hasCycle(numCourses)
    }
}