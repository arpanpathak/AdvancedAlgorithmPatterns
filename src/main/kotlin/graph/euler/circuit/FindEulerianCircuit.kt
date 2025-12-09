package graph.euler.circuit

class EulerianCircuit(private val graph: Map<Int, List<Int>>) {

    fun findEulerianCircuit(): List<Int>? {
        if (!hasEulerianCircuit()) return null

        val circuit = mutableListOf<Int>()
        val remainingEdges = graph.mapValues { it.value.toMutableList() }.toMutableMap()

        val startVertex = graph.keys.firstOrNull { graph[it]?.isNotEmpty() == true } ?: return emptyList()

        dfsHierholzer(startVertex, remainingEdges, circuit)

        // Final check: if any edges remain, the graph was not Eulerian
        val allEdgesUsed = remainingEdges.values.all { it.isEmpty() }

        return if (allEdgesUsed) circuit.reversed() else null
    }

    private fun dfsHierholzer(
        u: Int,
        remainingEdges: MutableMap<Int, MutableList<Int>>,
        circuit: MutableList<Int>
    ) {
        val neighbors = remainingEdges[u]

        while (neighbors?.isNotEmpty() == true) {

            // Greedily take the next vertex (v)
            val v = neighbors.removeFirst()

            // Symmetrically remove the reverse edge (v -> u)
            remainingEdges[v]?.remove(u)

            // Recursively dive deeper
            dfsHierholzer(v, remainingEdges, circuit)
        }

        // When the stack unwinds (no more edges from u), add u to the circuit
        circuit.add(u)
    }

    private fun hasEulerianCircuit(): Boolean {
        if (!isConnected()) return false

        return graph.all { (_, neighbors) -> neighbors.size % 2 == 0 }
    }

    private fun isConnected(): Boolean {
        val verticesWithEdges = graph.filterValues { it.isNotEmpty() }.keys
        if (verticesWithEdges.isEmpty()) return true

        val startVertex = verticesWithEdges.first()
        val visited = mutableSetOf<Int>()

        fun dfs(vertex: Int) {
            visited.add(vertex)
            graph[vertex]?.forEach { neighbor ->
                if (neighbor !in visited) {
                    dfs(neighbor)
                }
            }
        }

        dfs(startVertex)

        return verticesWithEdges.all { it in visited }
    }
}

fun Map<Int, List<Int>>.findEulerianCircuit(): List<Int>? {
    return EulerianCircuit(this).findEulerianCircuit()
}

fun main() {
    val graph = mapOf(
        0 to listOf(1, 3),
        1 to listOf(0, 2),
        2 to listOf(1, 3),
        3 to listOf(2, 0)
    )

    val circuit = graph.findEulerianCircuit()

    when {
        circuit == null -> println("No Eulerian circuit exists")
        circuit.isEmpty() -> println("Graph has no edges")
        else -> println("Eulerian circuit: ${circuit.joinToString(" -> ")}")
    }
}