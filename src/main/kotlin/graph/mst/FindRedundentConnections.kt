package graph.mst

class FindRedundentConnections {
    fun findRedundantConnection(edges: Array<IntArray>): IntArray {
        val parent = IntArray(1001) { it }

        fun find(x: Int): Int {
            if (parent[x] != x) parent[x] = find(parent[x])
            return parent[x]
        }

        fun union(x: Int, y: Int): Boolean {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX == rootY) return false
            parent[rootY] = rootX
            return true
        }

        for ((u, v) in edges) {
            if (!union(u, v)) return intArrayOf(u, v)
        }
        return intArrayOf()
    }

}