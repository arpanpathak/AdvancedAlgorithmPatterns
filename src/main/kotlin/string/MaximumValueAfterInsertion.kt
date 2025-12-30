package string

class MaximumValueAfterInsertion {
    fun maxValue(n: String, x: Int): String {
        val isNegative = n[0] == '-'
        val xChar = x.digitToChar()

        val insertIndex = when (isNegative) {
            true -> (1 until n.length).firstOrNull { n[it] > xChar }
            false -> n.indices.firstOrNull { n[it] < xChar }
        } ?: n.length

        return StringBuilder(n).insert(insertIndex, xChar).toString()
        // Another way could be to use substring
       // return n.substring(0, i) + xChar + n.substring(i)
    }
}
