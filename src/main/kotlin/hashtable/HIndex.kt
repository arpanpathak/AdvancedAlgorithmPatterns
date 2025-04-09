package hashtable

/**
 * Stupid AF Problem TBH
 */
class HIndex {
    fun hIndex(citations: IntArray): Int {
        val n = citations.size
        val papers = IntArray(n + 1)

        for (c in citations)
            papers[minOf(c, n)]++
        var k = n
        var s = papers[n]

        while (k > s) {
            s += papers[k--]
        }

        return k
    }
}