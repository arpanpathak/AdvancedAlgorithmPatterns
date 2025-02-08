package math.dp

class PascalsTriangle {
    fun generate(numRows: Int): List<List<Int>> {
        val result = MutableList(numRows) { MutableList(it + 1) { 1 } }

        for (i in 2 until numRows) {
            for (j in 1 until i) {
                result[i][j] = result[i - 1][j - 1] + result[i - 1][j]
            }
        }

        return result
    }
}
