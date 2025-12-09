package graph.euler.circuit.path

class ValidArrangementOfPairs {
    fun validArrangement(pairs: Array<IntArray>): Array<IntArray> {
        val graph = mutableMapOf<Int, ArrayDeque<Int>>()
        val degree = mutableMapOf<Int, Int>().withDefault { 0 }

        // Build graph and track degree difference (out - in)
        pairs.forEach { (u, v) ->
            graph.getOrPut(u) { ArrayDeque() }.add(v)
            degree[u] = degree.getValue(u) + 1
            degree[v] = degree.getValue(v) - 1
        }

        // Find start node (outDegree > inDegree)
        val start = degree.keys.firstOrNull { degree.getValue(it) == 1 } ?: pairs[0][0]

        val path = mutableListOf<IntArray>()
        val stack = ArrayDeque<Int>().apply { addLast(start) }
        val itinerary = mutableListOf<Int>()

        // Hierholzer's algorithm
        while (stack.isNotEmpty()) {
            val current = stack.last()
            if (graph.containsKey(current) && graph[current]!!.isNotEmpty()) {
                val next = graph[current]!!.removeFirst()
                stack.addLast(next)
            } else {
                itinerary.add(stack.removeLast())
            }
        }

        // Build result pairs
        for (i in itinerary.size - 2 downTo 0) {
            path.add(intArrayOf(itinerary[i + 1], itinerary[i]))
        }

        return path.toTypedArray()
    }
}