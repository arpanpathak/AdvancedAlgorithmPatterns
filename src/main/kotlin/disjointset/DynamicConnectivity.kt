package disjointset

data class Edge(val u: Int, val v: Int, val time: Int)

class DSU(n: Int) {
    val parent = IntArray(n) { it }
    val size = IntArray(n) { 1 }
    public var components = n

    fun find(i: Int): Int = when (parent[i]) {
        i -> i
        else -> find(parent[i]).also { parent[i] = it }
    }

    fun union(i: Int, j: Int): Boolean {
        val rootI = find(i)
        val rootJ = find(j)
        if (rootI == rootJ) return false

        when {
            size[rootI] < size[rootJ] -> {
                parent[rootI] = rootJ
                size[rootJ] += size[rootI]
            }
            else -> {
                parent[rootJ] = rootI
                size[rootI] += size[rootJ]
            }
        }
        components--
        return true
    }
}

fun solve(n: Int, allEdges: Array<Edge>, removals: Array<Edge>): IntArray {
    val dsu = DSU(n)

    // Create a set of "removed" edges using (u, v) pairs for O(1) lookups
    val removedPairs = removals.map { it.u to it.v }.toSet()

    // 1. Build the end state: Only add edges not in the removal list
    for ((u, v, _) in allEdges) {
        if (u to v !in removedPairs && v to u !in removedPairs) {
            dsu.union(u, v)
        }
    }

    val result = IntArray(removals.size)

    // 2. Process removals backwards (Unfriend -> Friend)
    for (i in removals.indices.reversed()) {
        val (u, v, _) = removals[i]
        result[i] = dsu.components
        dsu.union(u, v)
    }

    return result
}

fun main() {
    val allEdges = arrayOf(
        Edge(0, 1, 10),
        Edge(1, 2, 20),
        Edge(2, 3, 30)
    )
    val removals = arrayOf(
        Edge(1, 2, 25), // Unfriend B-C
        Edge(0, 1, 35)  // Unfriend A-B
    )

    val timeline = solve(4, allEdges, removals)
    println(timeline.joinToString()) // Output: 2, 3
}