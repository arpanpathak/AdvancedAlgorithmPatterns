package graph.cycle

class ParallelCourses {
    fun minimumSemesters(n: Int, relations: Array<IntArray>): Int {
        val inDegree = IntArray(n + 1) // One-based index
        val graph = Array(n + 1) { mutableListOf<Int>() }

        relations.forEach { (u, v) ->
            graph[u].add(v)
            inDegree[v]++
        }

        val queue = ArrayDeque<Int>()
        var semesters = 0
        var completed = 0

        // Add courses with no prerequisites (in-degree 0)
        (1..n).filter { inDegree[it] == 0 }.forEach { queue.add(it) }

        while (queue.isNotEmpty()) {
            val size = queue.size
            semesters++
            repeat(size) {
                val course = queue.removeFirst()
                completed++
                graph[course].forEach { next ->
                    if (--inDegree[next] == 0) queue.add(next)
                }
            }
        }

        return if (completed == n) semesters else -1
    }
}
