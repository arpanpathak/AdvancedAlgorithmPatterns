package tree

class MinimumTimeToCollectAllApplesInATree {
    fun minTime(n: Int, edges: Array<IntArray>, hasApple: List<Boolean>): Int {
        // Build the graph as a list of lists (adjacency list representation)
        val graph = Array(n) { mutableListOf<Int>() }
        edges.forEach { (u, v) ->
            graph[u].add(v)
            graph[v].add(u)
        }

        // DFS function to calculate the total time
        fun dfs(node: Int, parent: Int): Int {
            var totalTime = 0
            // Use forEach to iterate over each neighbor
            graph[node].forEach { neighbor ->
                if (neighbor != parent) {
                    val time = dfs(neighbor, node)
                    // If the time is greater than 0 or the neighbor has an apple, add the time and return time (2)
                    if (time > 0 || hasApple[neighbor]) {
                        totalTime += time + 2
                    }
                }
            }
            return totalTime
        }

        // Start DFS from node 0 with no parent (-1)
        return dfs(0, -1)
    }
}