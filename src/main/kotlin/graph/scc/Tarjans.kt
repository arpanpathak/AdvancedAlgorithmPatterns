package graph.scc

class GraphTarjans<T> {
    private val graph = mutableMapOf<T, MutableList<T>>()

    fun addEdge(from: T, to: T) {
        graph.getOrPut(from) { mutableListOf() }.add(to)
    }

    fun findSCC(): List<List<T>> {
        val sccs = mutableListOf<List<T>>()
        // ids: Discovery time for each node (dfn)
        val ids = mutableMapOf<T, Int>()
        // lowLinks: Lowest ID/dfn reachable from the node (low)
        val lowLinks = mutableMapOf<T, Int>()
        val stack = ArrayDeque<T>()
        // inStack: Tracks nodes currently on the recursion stack
        val inStack = mutableSetOf<T>()
        var id = 0

        fun tarjanDfs(node: T) {
            ids[node] = id
            lowLinks[node] = id
            id++
            stack.addLast(node)
            inStack.add(node)

            graph[node]?.forEach { neighbor ->
                when {
                    // Tree edge: Unvisited neighbor
                    neighbor !in ids -> {
                        tarjanDfs(neighbor)
                        // Update lowLink with the lowest reachable ID from the neighbor's subtree
                        lowLinks[node] = minOf(lowLinks[node]!!, lowLinks[neighbor]!!)
                    }
                    // Back edge: Forms a cycle with a node currently on the stack
                    neighbor in inStack -> {
                        // Update lowLink with the neighbor's discovery ID (ids)
                        lowLinks[node] = minOf(lowLinks[node]!!, ids[neighbor]!!)
                    }
                }
            }

            // Core condition: Root of a Strongly Connected Component found
            if (lowLinks[node] == ids[node]) {
                val scc = mutableListOf<T>()
                var componentNode: T

                // Pop all nodes from the stack until the root node is popped.
                do {
                    componentNode = stack.removeLast()
                    inStack.remove(componentNode)
                    scc.add(componentNode)
                } while (componentNode != node)

                sccs.add(scc)
            }
        }

        graph.keys.forEach { node ->
            if (node !in ids) {
                tarjanDfs(node)
            }
        }

        return sccs
    }
}
