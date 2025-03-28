package disjointset

class NumberOfIsland_II {
    private class UnionFind {
        val parent = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        val rank = mutableMapOf<Pair<Int, Int>, Int>()
        var count = 0

        fun find(x: Pair<Int, Int>): Pair<Int, Int> {
            if (parent[x] != x) {
                parent[x] = find(parent[x]!!) // Path compression
            }
            return parent[x]!!
        }

        fun union(x: Pair<Int, Int>, y: Pair<Int, Int>) {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX == rootY) return

            when {
                rank[rootX]!! > rank[rootY]!! -> parent[rootY] = rootX
                rank[rootX]!! < rank[rootY]!! -> parent[rootX] = rootY
                else -> {
                    parent[rootY] = rootX
                    rank[rootX] = rank[rootX]!! + 1
                }
            }
            count--
        }

        fun addLand(position: Pair<Int, Int>) {
            if (parent.containsKey(position)) return
            parent[position] = position
            rank[position] = 0
            count++
        }
    }

    fun numIslands2(m: Int, n: Int, positions: Array<IntArray>): List<Int> {
        val uf = UnionFind()
        val result = mutableListOf<Int>()
        val directions = arrayOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

        for (pos in positions) {
            val (r, c) = pos
            val current = r to c
            uf.addLand(current)

            for ((dr, dc) in directions) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until m && nc in 0 until n) {
                    val neighbor = nr to nc
                    if (uf.parent.containsKey(neighbor)) {
                        uf.union(current, neighbor)
                    }
                }
            }
            result.add(uf.count)
        }
        return result
    }
}
