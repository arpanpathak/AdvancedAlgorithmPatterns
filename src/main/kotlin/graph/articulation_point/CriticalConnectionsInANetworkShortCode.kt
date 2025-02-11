package graph.articulation_point

class CriticalConnectionsInANetworkShortCode {
    fun criticalConnections(n: Int, connections: List<List<Int>>): List<List<Int>> {
        val graph = Array(n) { mutableListOf<Int>() }.apply {
            connections.forEach { (u, v) -> this[u].add(v); this[v].add(u) }
        }
        val result = mutableListOf<List<Int>>()
        val labels = IntArray(n) { -1 }
        val low = IntArray(n)
        var time = 0

        fun dfs(node: Int, parent: Int) {
            labels[node] = time.also { low[node] = it; time++ }
            graph[node].forEach { neighbour ->
                if (neighbour != parent) {
                    if (labels[neighbour] == -1) {
                        dfs(neighbour, node)
                        if (low[neighbour] > labels[node]) result.add(listOf(node, neighbour))
                    }
                    low[node] = minOf(low[node], low[neighbour])
                }
            }
        }

        dfs(0, -1)
        return result
    }
}