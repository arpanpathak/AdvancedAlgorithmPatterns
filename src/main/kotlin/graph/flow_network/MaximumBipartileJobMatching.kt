package graph

class BipartiteMatching(private val numJobs: Int, private val numWorkers: Int) {
    // Adjacency list: Job index -> List of compatible Worker indices
    private val adj = Array(numJobs) { mutableListOf<Int>() }

    fun addEdge(job: Int, worker: Int) {
        adj[job].add(worker)
    }

    fun maxMatching(): Int {
        // match[worker] = job assigned to them, -1 if free
        val match = IntArray(numWorkers) { -1 }
        var result = 0

        for (job in 0 until numJobs) {
            // For each job, try to find an available worker using a fresh visited array
            val visited = BooleanArray(numWorkers)
            if (canMatch(job, visited, match)) {
                result++
            }
        }
        return result
    }

    private fun canMatch(job: Int, visited: BooleanArray, match: IntArray): Boolean {
        // Use destructuring if workers were pairs, but here we iterate neighbors
        for (worker in adj[job]) {
            if (!visited[worker]) {
                visited[worker] = true

                // If worker is free OR the existing job assigned to worker can be moved
                if (match[worker] < 0 || canMatch(match[worker], visited, match)) {
                    match[worker] = job
                    return true
                }
            }
        }
        return false
    }
}