package disjointset

class NumberOfIsland_II_Optimized {
    fun numIslands2(m: Int, n: Int, positions: Array<IntArray>): List<Int> {
        val parent = IntArray(m * n) { -1 }
        val result = mutableListOf<Int>()
        val dirs = arrayOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        var count = 0

        fun find(i: Int): Int {
            if (parent[i] != i) {
                parent[i] = find(parent[i])
            }
            return parent[i]
        }

        fun union(i: Int, j: Int) {
            // If the parent isn't land then we can't union this
            if (parent[j] == -1) return

            val rootI = find(i)
            val rootJ = find(j)
            if (rootI != rootJ) {
                parent[rootI] = rootJ
                count--
            }
        }

        for (pos in positions) {
            val (r, c) = pos
            val index = r * n + c

            if (parent[index] != -1) {
                result.add(count)
                continue
            }

            parent[index] = index
            count++

            for ((dr, dc) in dirs) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until m && nc in 0 until n) {
                    union(index, nr * n + nc)
                }
            }
            result.add(count)
        }
        return result
    }
}