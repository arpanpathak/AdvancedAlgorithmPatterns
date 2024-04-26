package tree.mst

import kotlin.math.abs

class MinCostToConnectAllPointsKruskal {
    data class Edge(val point1: Int, val point2: Int, val weight: Int)
    val manhattanDistance = { p1: IntArray, p2: IntArray -> abs(p1[0] - p2[0]) + abs(p1[1] - p2[1])  }

    class UnionFind(size: Int) {
        private val parent = (0 until size).associateWith { it }.toMutableMap()
        private val rank = (0 until size).associateWith { 0 }.toMutableMap()

        fun find(x: Int): Int = parent[x]!!.takeIf { it == x } ?: find(parent[x]!!).also { parent[x] = it }

        fun union(x: Int, y: Int) {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX != rootY) {
                when {
                    rank[rootX]!! > rank[rootY]!! -> parent[rootY] = rootX
                    rank[rootX]!! < rank[rootY]!! -> parent[rootX] = rootY
                    else -> {
                        parent[rootY] = rootX
                        rank[rootX] = rank[rootX]!! + 1
                    }
                }
            }
        }
    }

    fun minCostConnectPoints(points: Array<IntArray>): Int {
        val edges = points.indices.flatMap { i ->
            (i + 1 until points.size).map { j ->
                Edge(i, j, manhattanDistance(points[i], points[j]))
            }
        }.sortedBy { it.weight }

        val uf = UnionFind(points.size)
        return edges.asSequence().filter { uf.find(it.point1) != uf.find(it.point2) }
            .onEach { uf.union(it.point1, it.point2) }
            .sumOf { it.weight }
    }
}
