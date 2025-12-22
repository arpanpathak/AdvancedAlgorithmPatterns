package disjointset

class DisjointSet(n: Int) {
    private val parent = IntArray(n) { it }
    private val rank = IntArray(n)
    var components = n
        private set

    fun find(x: Int): Int {
        if (parent[x] != x) parent[x] = find(parent[x])
        return parent[x]
    }

    fun union(x: Int, y: Int) {
        val rootX = find(x)
        val rootY = find(y)
        if (rootX != rootY) {
            when {
                rank[rootX] > rank[rootY] -> parent[rootY] = rootX
                rank[rootX] < rank[rootY] -> parent[rootX] = rootY
                else -> {
                    parent[rootY] = rootX
                    rank[rootX]++
                }
            }
            components--
        }
    }
}

class TheEarliestMomentEveryoneBecameFriends {
    fun earliestAcq(logs: Array<IntArray>, n: Int): Int {
        val ds = DisjointSet(n)
        logs.sortBy { it[0] }

        logs.forEach { (time, x, y) ->
            ds.union(x, y)
            if (ds.components == 1) return time
        }

        return -1
    }
}
