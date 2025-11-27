package graph.components

data class Graph<T> (val adjacencyList: Map<T, List<T>>)

fun <T> Graph<T>.findConnectedComponentsDfs(): List<Set<T>> {
    val visited = mutableSetOf<T>()
    val components = mutableListOf<Set<T>>()

    fun dfs(node: T, component: MutableSet<T>) {
        visited.add(node).also { it -> component.add(node) }
        adjacencyList[node]?.forEach {
            if (!visited.contains(it))
                dfs(it, component)
        }
    }

    for (node in adjacencyList.keys) {
        if (!visited.contains(node))
            components.add(buildSet{ dfs(node, this)})
    }

    return components
}

fun <T> Graph<T>.findConnectedComponents(): List<Set<T>> {
    val visited = mutableSetOf<T>()
    val components = mutableListOf<Set<T>>()

    fun bfs(start: T, component: MutableSet<T>) {
        val queue = ArrayDeque<T>().apply { add(start) }
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            if (visited.add(node)) {
                component.add(node)
                adjacencyList[node]?.forEach { neighbor ->
                    if (neighbor !in visited) queue.add(neighbor)
                }
            }
        }
    }

    for (node in adjacencyList.keys) {
        if (node !in visited) {
            components.add(buildSet { bfs(node, this) })
        }
    }

    return components
}
