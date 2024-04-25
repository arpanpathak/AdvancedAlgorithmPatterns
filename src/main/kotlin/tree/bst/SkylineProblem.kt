package tree.bst

import java.util.*

class SkylineProblem {
    fun getSkyline(buildings: Array<IntArray>): List<List<Int>> {
        val points = buildings.flatMap {
            listOf(Pair(it[0], -it[2]), Pair(it[1], it[2]))
        }.sortedWith(compareBy({ it.first }, { it.second }))

        val heightMap = TreeMap<Int, Int>(reverseOrder()).also { it[0] = 1 }
        val result = mutableListOf<List<Int>>()
        var previousHeight = 0

        for ((x, height) in points) {
            // We've reached a starting point.
            if (height < 0) {
                heightMap[-height] = (heightMap[-height] ?: 0) + 1
            } else if (heightMap[height] == 1) {
                heightMap.remove(height)
            } else {
                heightMap[height]?.dec()
            }

            val currentHeight = heightMap.firstKey()
            if (currentHeight != previousHeight) {
                result.add(listOf(x, currentHeight))
                previousHeight = currentHeight
            }
        }

        return result
    }
}
