package graph.flow_network

object BipartileMatching {
    data class Edge<T>(val from: T, val to: T, val capacity: Int)

    class ResidualEdge<T>(val to: T, var residualCapacity: Int)

    class ResidualGraph<T>(edges: List<Edge<T>>) {
        // Optimization: Map of Maps for O(1) edge lookup
        private val adj = mutableMapOf<T, MutableMap<T, ResidualEdge<T>>>()
        private val parent = mutableMapOf<T, T>()

        init {
            for ((u, v, cap) in edges) {
                adj.getOrPut(u) { mutableMapOf() }[v] = ResidualEdge(v, cap)
                // Add reverse edge with 0 capacity if it doesn't exist
                adj.getOrPut(v) { mutableMapOf() }.getOrPut(u) { ResidualEdge(u, 0) }
            }
        }

        fun findAugmentingPath(source: T, sink: T): Int {
            parent.clear()
            val pathCapacity = mutableMapOf<T, Int>()
            val queue = ArrayDeque<T>().apply { add(source) }
            pathCapacity[source] = Int.MAX_VALUE

            while (queue.isNotEmpty()) {
                val u = queue.removeFirst()

                // O(1) iteration over neighbors
                adj[u]?.values?.forEach { edge ->
                    if (edge.residualCapacity > 0 && edge.to !in parent && edge.to != source) {
                        parent[edge.to] = u
                        pathCapacity[edge.to] = minOf(pathCapacity[u]!!, edge.residualCapacity)
                        if (edge.to == sink) return pathCapacity[sink]!!
                        queue.add(edge.to)
                    }
                }
            }
            return 0
        }

        fun updateResidual(flowAmount: Int, source: T, sink: T) {
            var v = sink
            while (v != source) {
                val u = parent[v]!!

                // Fixed: O(1) lookups instead of .find()
                adj[u]!![v]!!.residualCapacity -= flowAmount
                adj[v]!![u]!!.residualCapacity += flowAmount

                v = u
            }
        }
    }

    fun solveJobMatching(jobWorkerPairs: List<Pair<String, String>>): Int {
        val source = "GLOBAL_SOURCE"
        val sink = "GLOBAL_SINK"
        val edges = mutableListOf<Edge<String>>()

        val jobs = jobWorkerPairs.map { it.first }.toSet()
        val workers = jobWorkerPairs.map { it.second }.toSet()

        // 1. Source -> Every Job (Capacity 1)
        jobs.forEach { edges.add(Edge(source, it, 1)) }

        // 2. Every Job -> Compatible Worker (Capacity 1)
        jobWorkerPairs.forEach { (job, worker) ->
            edges.add(Edge(job, worker, 1))
        }

        // 3. Every Worker -> Sink (Capacity 1)
        workers.forEach { edges.add(Edge(it, sink, 1)) }

        // 4. Run Edmonds-Karp
        val graph = ResidualGraph(edges)
        var maxMatching = 0

        while (true) {
            val flow = graph.findAugmentingPath(source, sink)
            if (flow == 0) break
            graph.updateResidual(flow, source, sink)
            maxMatching += flow
        }

        return maxMatching
    }
}