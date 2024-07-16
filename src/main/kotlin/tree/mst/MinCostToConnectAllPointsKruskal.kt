package tree.mst

import kotlin.math.abs

class MinCostToConnectAllPointsKruskal {
    data class Edge(val point1: Int, val point2: Int, val weight: Int)

    data class UnionFindNode(var parent: Int, var rank: Int)

    val manhattanDistance = { p1: IntArray, p2: IntArray -> abs(p1[0] - p2[0]) + abs(p1[1] - p2[1]) }

    class UnionFind(size: Int) {
        private val nodes = Array(size) { UnionFindNode(it, 0) }

        fun find(x: Int): Int {
            if (nodes[x].parent != x) {
                nodes[x].parent = find(nodes[x].parent)
            }
            return nodes[x].parent
        }

        fun union(x: Int, y: Int) {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX != rootY) {
                when {
                    nodes[rootX].rank > nodes[rootY].rank -> nodes[rootY].parent = rootX
                    nodes[rootX].rank < nodes[rootY].rank -> nodes[rootX].parent = rootY
                    else -> {
                        nodes[rootY].parent = rootX
                        nodes[rootX].rank += 1
                    }
                }
            }
        }
    }

    fun minCostConnectPoints(points: Array<IntArray>): Int {
        val edges = mutableListOf<Edge>()
        for (i in points.indices) {
            for (j in i + 1 until points.size) {
                edges.add(Edge(i, j, manhattanDistance(points[i], points[j])))
            }
        }
        edges.sortBy { it.weight }

        val uf = UnionFind(points.size)
        var cost = 0
        for (edge in edges) {
            if (uf.find(edge.point1) != uf.find(edge.point2)) {
                uf.union(edge.point1, edge.point2)
                cost += edge.weight
            }
        }
        return cost
    }
}
