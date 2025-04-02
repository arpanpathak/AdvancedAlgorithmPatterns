package graph

class ReorderRoutesToMakeAllPathsLeadToCityZero {
    fun minReorder(n: Int, connections: Array<IntArray>): Int {
        val graph = Array(n) { mutableListOf<IntArray>() }

        // Build adjacency list as an undirected graph
        for (edge in connections) {
            graph[edge[0]].add(edge)  // Forward edge
            graph[edge[1]].add(edge)  // Backward edge
        }

        var changes = 0
        val visited = BooleanArray(n)

        fun dfs(city: Int) {
            visited[city] = true
            for (edge in graph[city]) {
                val (from, to) = edge
                val neighbor = if (from == city) to else from
                if (!visited[neighbor]) {
                    if (from == city) changes++ // Forward edge needs reversal
                    dfs(neighbor)
                }
            }
        }

        dfs(0) // Start DFS from city 0
        return changes
    }
}