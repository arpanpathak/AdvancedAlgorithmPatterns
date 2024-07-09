package array.hashtable

class EqualRowAndColumnPairs {

    /**
     * Bruteforce...
     */
    fun equalPairs(grid: Array<IntArray>): Int {
        val n = grid.size
        var count = 0
        for (row in 0 until n) {
            for (col in 0 until n) {
                var equals = 0
                for (i in 0 until n) {
                    if (grid[row][i] != grid[i][col])
                        break
                    equals++
                }

                if (equals == n)
                    count++

            }

        }

        return count
    }

    fun equalPairs_hash(grid: Array<IntArray>): Int {
        val n = grid.size
        val rowMap = mutableMapOf<List<Int>, Int>()
        var count = 0

        // Step 1: Convert each row to a list and store in the map
        for (row in grid) {
            val rowList = row.toList()
            rowMap[rowList] = rowMap.getOrDefault(rowList, 0) + 1
        }

        // Step 2: Convert each column to a list and check against the row map
        for (col in 0 until n) {
            val colList = List(n) { grid[it][col] }
            count += rowMap.getOrDefault(colList, 0)
        }

        return count
    }

}