package tree.fenwick

class FenwickTree(size: Int) {
    private val tree = IntArray(size + 1)

    fun update(index: Int, value: Int) {
        var i = index + 1
        while (i < tree.size) {
            tree[i] += value
            i += i and -i
        }
    }

    fun prefixSum(index: Int): Int {
        var i = index + 1
        var sum = 0
        while (i > 0) {
            sum += tree[i]
            i -= i and -i
        }
        return sum
    }

    fun rangeSum(left: Int, right: Int): Int = prefixSum(right) - prefixSum(left - 1)
}

