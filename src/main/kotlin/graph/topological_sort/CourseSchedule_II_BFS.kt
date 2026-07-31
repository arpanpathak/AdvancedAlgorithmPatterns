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

fun main() {
    val solver = graph.topological_sort.CourseSchedule_II_BFS()

    // Test 1: Simple case
    val result1 = solver.findOrder(2, arrayOf(intArrayOf(1, 0)))
    assert(result1 contentEquals intArrayOf(0, 1)) { "Test 1 failed: $result1" }

    // Test 2: Multiple dependencies
    val result2 = solver.findOrder(4, arrayOf(intArrayOf(1, 0), intArrayOf(2, 0), intArrayOf(3, 1), intArrayOf(3, 2)))
    assert(result2 contentEquals intArrayOf(0, 1, 2, 3) || result2 contentEquals intArrayOf(0, 2, 1, 3)) { "Test 2 failed: $result2" }

    // Test 3: Cycle in the graph (no valid order)
    val result3 = solver.findOrder(2, arrayOf(intArrayOf(0, 1), intArrayOf(1, 0)))
    assert(result3.isEmpty()) { "Test 3 failed: $result3" }

    // Test 4: No prerequisites
    val result4 = solver.findOrder(3, arrayOf())
    assert(result4.toSet() == setOf(0, 1, 2)) { "Test 4 failed: $result4" }

    // Test 5: Self-loop (invalid schedule)
    val result5 = solver.findOrder(1, arrayOf(intArrayOf(0, 0)))
    assert(result5.isEmpty()) { "Test 5 failed: $result5" }

    println("All tests passed.")
}
