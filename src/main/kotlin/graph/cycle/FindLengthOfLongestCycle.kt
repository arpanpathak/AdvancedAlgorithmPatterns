package graph.cycle

enum class Color { UNVISITED, VISITING, VISITED }

fun longestCycleLength(edges: IntArray): Int {

    val nodeStates = edges.indices.associateWith { Color.UNVISITED }.toMutableMap()
    val distances = mutableMapOf<Int, Int>()
    var maxCycle = -1

    fun dfs(node: Int, currentDist: Int) {
        when (nodeStates[node]) {
            Color.VISITED -> return
            Color.VISITING -> {
                val cycleLength = currentDist - distances[node]!!
                maxCycle = maxOf(maxCycle, cycleLength)
                return
            }
            Color.UNVISITED -> {
                nodeStates[node] = Color.VISITING
                distances[node] = currentDist

                edges[node]
                    .takeUnless { it == -1 }
                    ?.let { dfs(it, currentDist + 1) }

                nodeStates[node] = Color.VISITED
            }

            else -> {}
        }
    }

    nodeStates.keys
        .filter { nodeStates[it] == Color.UNVISITED }
        .forEach { dfs(it, 0) }

    // Another approach
//    edges.indices.forEach { node ->
//        if (nodeStates[node] == Color.UNVISITED) {
//            dfs(node, 0)
//        }
//    }

    return maxCycle
}




// Using adjacency list (list of lists)
fun longestCycleLength(graph: List<List<Int>>): Int {
    val colors = Array(graph.size) { Color.UNVISITED }
    val entryTime = IntArray(graph.size) { -1 }
    var maxCycle = -1
    var time = 0

    fun dfs(node: Int) {
        when (colors[node]) {
            Color.VISITED -> return
            Color.VISITING -> {
                // Cycle found! Calculate its length
                val cycleLength = time - entryTime[node]
                maxCycle = maxOf(maxCycle, cycleLength)
                return
            }
            Color.UNVISITED -> {
                colors[node] = Color.VISITING
                entryTime[node] = time++

                // Visit ALL neighbors (not just one!)
                graph[node].forEach { neighbor ->
                    dfs(neighbor)
                }

                colors[node] = Color.VISITED
            }
        }
    }

    graph.indices.forEach { i ->
        colors[i].takeIf { it == Color.UNVISITED }
            ?.let { dfs(i) }
    }

    return maxCycle
}

fun findAllCycles(graph: List<List<Int>>): List<List<Int>> {
    val colors = Array(graph.size) { Color.UNVISITED }
    val parent = IntArray(graph.size) { -1 }
    val cycles = mutableListOf<List<Int>>()

    fun dfs(node: Int) {
        when (colors[node]) {
            Color.VISITED -> return
            Color.VISITING -> {
                // Reconstruct cycle from node back to itself
                val cycle = buildList {
                    var current = node
                    do {
                        add(current)
                        current = parent[current]
                    } while (current != node)
                    reverse()
                }
                cycles.add(cycle)
                return
            }
            Color.UNVISITED -> {
                colors[node] = Color.VISITING

                graph[node].forEach { neighbor ->
                    parent[neighbor] = node
                    dfs(neighbor)
                }

                colors[node] = Color.VISITED
            }
        }
    }

    graph.indices.forEach { i ->
        if (colors[i] == Color.UNVISITED) dfs(i)
    }

    return cycles.distinctBy { it.toSet() }
}