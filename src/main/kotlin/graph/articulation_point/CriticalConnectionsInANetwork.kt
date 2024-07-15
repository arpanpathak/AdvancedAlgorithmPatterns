package graph.articulation_point

class CriticalConnectionsInANetwork {
    private lateinit var G: Array<MutableList<Int>>
    private lateinit var result: MutableList<List<Int>>
    private var depth = 0
    private lateinit var label: IntArray
    private lateinit var low: IntArray
    private lateinit var visited: BooleanArray

    fun criticalConnections(n: Int, connections: List<List<Int>>): List<List<Int>> {
        G = Array(n) { mutableListOf() }
        result = mutableListOf()
        depth = 0
        label = IntArray(n)
        low = IntArray(n)
        visited = BooleanArray(n)

        for (i in 0 until n) {
            G[i] = mutableListOf()
        }

        for (edge in connections) {
            val i = edge[0]
            val j = edge[1]
            G[i].add(j)
            G[j].add(i)
        }

        dfs(0, -1)

        return result
    }

    private fun dfs(node: Int, parent: Int) {
        visited[node] = true
        label[node] = depth
        low[node] = depth
        depth++

        for (neighbour in G[node]) {
            if (neighbour == parent) continue

            if (!visited[neighbour]) {
                dfs(neighbour, node)

                if (label[node] < low[neighbour]) {
                    result.add(listOf(node, neighbour))
                }
            }

            low[node] = minOf(low[node], low[neighbour])
        }
    }
}