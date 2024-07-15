package graph.articulation_point

class FindArticulationPoints {
    private lateinit var G: Array<MutableList<Int>>
    private var time = 0 // Time counter for DFS
    private lateinit var label: IntArray
    private lateinit var low: IntArray
    private lateinit var visited: BooleanArray

    fun findCutVertices(n: Int, connections: List<List<Int>>): Set<Int> {
        G = Array(n) { mutableListOf() }
        time = 0
        label = IntArray(n)
        low = IntArray(n)
        visited = BooleanArray(n)
        val cutVertices = mutableSetOf<Int>()

        for (edge in connections) {
            val i = edge[0]
            val j = edge[1]
            G[i].add(j)
            G[j].add(i)
        }

        dfs(0, -1, cutVertices);

        return cutVertices
    }

    private fun dfs(node: Int, parent: Int, cutVertices: MutableSet<Int>) {
        visited[node] = true
        label[node] = time++
        low[node] = label[node]

        var childrenCount = 0
        for (neighbour in G[node]) {
            if (neighbour == parent) continue

            if (!visited[neighbour]) {
                dfs(neighbour, node, cutVertices)
                childrenCount++

                // If node is not root and has a child with low link >= label[node], it's a cut vertex
                if (parent == -1 && childrenCount > 1 || low[neighbour] >= label[node] && parent != -1) {
                    cutVertices.add(node)
                }
            }

            low[node] = Math.min(low[node], label[neighbour]) // Update low link for back edges
        }
    }
}