package tree.segment

/**
 * Segment Tree implementation supporting range sum queries and range updates with lazy propagation.
 *
 * Time Complexities:
 * - Construction: O(N)
 * - Range Query: O(log N)
 * - Range Update: O(log N)
 *
 * Space Complexity: O(N)
 */
class SegmentTree<T : Number>(private val arr: Array<T>) {
    private val n = arr.size

    private val size = when {
        n and (n - 1) == 0 -> 2 * n - 1
        else -> {
            var power = 1
            while (power < n) power = power shl 1
            2 * power - 1
        }
    }

    private val tree = LongArray(size) { 0L }
    private val lazy = LongArray(size) { 0L }

    init {
        build(0, n - 1, 0)
    }

    /**
     * Builds the segment tree recursively
     * @param low current segment start index
     * @param high current segment end index
     * @param pos current node position in tree array
     */
    private fun build(low: Int, high: Int, pos: Int) {
        when (low) {
            high -> tree[pos] = arr[low].toLong()
            else -> {
                val mid = (low + high) / 2
                build(low, mid, 2 * pos + 1)
                build(mid + 1, high, 2 * pos + 2)
                tree[pos] = tree[2 * pos + 1] + tree[2 * pos + 2]
            }
        }
    }

    /**
     * Propagates lazy updates to children nodes
     * @param pos current node position
     * @param low current segment start index
     * @param high current segment end index
     */
    private fun pushDown(pos: Int, low: Int, high: Int) {
        if (lazy[pos] != 0L) {
            tree[pos] += lazy[pos] * (high - low + 1)
            if (low != high) {
                lazy[2 * pos + 1] += lazy[pos]
                lazy[2 * pos + 2] += lazy[pos]
            }
            lazy[pos] = 0
        }
    }

    /**
     * Returns the sum of elements in range [qlow, qhigh]
     * @param qlow query range start index (inclusive)
     * @param qhigh query range end index (inclusive)
     * @return sum of elements in the given range
     */
    fun rangeSum(qlow: Int, qhigh: Int): Long {
        return query(qlow, qhigh, 0, n - 1, 0)
    }

    /**
     * Recursive helper for range sum query
     * @param qlow query range start index
     * @param qhigh query range end index
     * @param low current segment start index
     * @param high current segment end index
     * @param pos current node position in tree array
     * @return sum of elements in the query range
     */
    private fun query(qlow: Int, qhigh: Int, low: Int, high: Int, pos: Int): Long {
        pushDown(pos, low, high)

        return when {
            qlow > high || qhigh < low -> 0L
            qlow <= low && qhigh >= high -> tree[pos]
            else -> {
                val mid = (low + high) / 2
                query(qlow, qhigh, low, mid, 2 * pos + 1) +
                        query(qlow, qhigh, mid + 1, high, 2 * pos + 2)
            }
        }
    }

    /**
     * Adds delta to all elements in range [qlow, qhigh]
     * @param qlow update range start index (inclusive)
     * @param qhigh update range end index (inclusive)
     * @param delta value to add to each element in range
     */
    fun rangeUpdate(qlow: Int, qhigh: Int, delta: Long) {
        update(qlow, qhigh, delta, 0, n - 1, 0)
    }

    /**
     * Recursive helper for range update
     * @param qlow update range start index
     * @param qhigh update range end index
     * @param delta value to add to each element
     * @param low current segment start index
     * @param high current segment end index
     * @param pos current node position in tree array
     */
    private fun update(qlow: Int, qhigh: Int, delta: Long, low: Int, high: Int, pos: Int) {
        pushDown(pos, low, high)

        when {
            qlow > high || qhigh < low -> return
            qlow <= low && qhigh >= high -> {
                lazy[pos] += delta
                pushDown(pos, low, high)
            }
            else -> {
                val mid = (low + high) / 2
                update(qlow, qhigh, delta, low, mid, 2 * pos + 1)
                update(qlow, qhigh, delta, mid + 1, high, 2 * pos + 2)
                tree[pos] = tree[2 * pos + 1] + tree[2 * pos + 2]
            }
        }
    }
}
