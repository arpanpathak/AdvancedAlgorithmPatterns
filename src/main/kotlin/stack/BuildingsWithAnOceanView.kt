package stack

class BuildingsWithAnOceanView {
    fun findBuildings(heights: IntArray): IntArray {
        val result = mutableListOf<Int>()

        for (i in heights.indices.reversed()) {
            // If the current building is taller than the previous tallest building
            if (result.isEmpty() || heights[i] > heights[result.last()]) {
                result.add(i)
            }
        }

        return result.reversed().toIntArray()
    }
}