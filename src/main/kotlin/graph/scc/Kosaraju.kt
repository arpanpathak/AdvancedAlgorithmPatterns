import java.util.ArrayDeque

class Graph<T> {
    private val adj = mutableMapOf<T, MutableList<T>>()
    private val revAdj = mutableMapOf<T, MutableList<T>>()

    fun addEdge(u: T, v: T) {
        adj.getOrPut(u) { mutableListOf() }.add(v)
        revAdj.getOrPut(v) { mutableListOf() }.add(u)
    }

    fun getSCCs(): List<List<T>> {
        val visited = mutableSetOf<T>()
        val stack = ArrayDeque<T>()

        adj.keys.forEach { vertex ->
            if (vertex !in visited) fillOrder(vertex, visited, stack)
        }

        visited.clear()
        return buildList {
            while (stack.isNotEmpty()) {
                val vertex = stack.removeLast()
                if (vertex !in visited) {
                    add(buildList { dfsOnReversed(vertex, visited, this) })
                }
            }
        }
    }

    private fun fillOrder(vertex: T, visited: MutableSet<T>, stack: ArrayDeque<T>) {
        visited.add(vertex)
        adj[vertex]?.forEach { neighbor ->
            if (neighbor !in visited) fillOrder(neighbor, visited, stack)
        }
        stack.addLast(vertex)
    }

    private fun dfsOnReversed(vertex: T, visited: MutableSet<T>, component: MutableList<T>) {
        visited.add(vertex)
        component.add(vertex)
        revAdj[vertex]?.forEach { neighbor ->
            if (neighbor !in visited) dfsOnReversed(neighbor, visited, component)
        }
    }
}

fun main() {
    val graph = Graph<Int>().apply {
        addEdge(0, 2)
        addEdge(2, 1)
        addEdge(1, 0)
        addEdge(2, 3)
        addEdge(3, 4)
    }

    graph.getSCCs().forEach { println(it) }
}
