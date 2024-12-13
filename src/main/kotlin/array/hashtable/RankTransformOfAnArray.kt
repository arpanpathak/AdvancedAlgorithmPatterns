package array.hashtable

class RankTransformOfAnArray {
    fun arrayRankTransform(arr: IntArray): IntArray {
        // Step 1: Sort the array and remove duplicates
        val sortedArr = arr.sorted()

        // Step 2: Create a map of value to rank (1-based)
        val rankMap = mutableMapOf<Int, Int>()
        var rank = 1
        for (num in sortedArr) {
            // rankMap[num] = rank++
            rankMap.getOrPut(num) { rank++ }
        }

        // Step 3: Replace original elements with their ranks
        return arr.map { rankMap[it]!! }.toIntArray()
    }
}
