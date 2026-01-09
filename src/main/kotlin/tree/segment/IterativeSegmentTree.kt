package tree.segment

/**
 * Iterative Segment Tree using 2N space.
 * Leaves are stored in the range [n, 2n - 1].
 * Parents are stored in the range [1, n - 1].
 */
class IterativeSegmentTree<T>(
    private val n: Int,
    private val identity: T,
    private val aggregate: (T, T) -> T
) {
    private val tree = MutableList(2 * n) { identity }

    fun build(arr: List<T>) {
        // Place leaves in the second half of the array
        for (i in 0 until n) tree[n + i] = arr[i]
        // Build parents from the bottom up using children 2i and 2i+1
        for (i in n - 1 downTo 1) {
            tree[i] = aggregate(tree[2 * i], tree[2 * i + 1])
        }
    }

    fun update(index: Int, value: T) {
        var i = index + n
        tree[i] = value
        while (i > 1) {
            i /= 2 // Move to parent
            // Re-calculate parent based on current children
            tree[i] = aggregate(tree[2 * i], tree[2 * i + 1])
        }
    }

    fun query(left: Int, right: Int): T {
        var res = identity
        var l = left + n
        var r = right + n

        while (l < r) {
            // If l is a right child, it is the start of the range
            when {
                l % 2 == 1 -> res = aggregate(res, tree[l++])
            }
            // If r is a right child, its left sibling is the end of the range
            when {
                r % 2 == 1 -> res = aggregate(tree[--r], res)
            }
            l /= 2
            r /= 2
        }
        return res
    }
}

fun main() {
    val data = listOf(1, 3, 5, 7, 9, 11)
    val n = data.size

    // 1. Range Sum Example
    println("--- Range Sum ---")
    val sumTree = IterativeSegmentTree(n, 0) { a, b -> a + b }
    sumTree.build(data)
    println("Sum of [1, 4): ${sumTree.query(1, 4)}") // 3 + 5 + 7 = 15

    sumTree.update(2, 10) // Change 5 to 10
    println("Sum of [1, 4) after update: ${sumTree.query(1, 4)}") // 3 + 10 + 7 = 20

    // 2. Range Minimum Example
    println("\n--- Range Minimum ---")
    val minTree = IterativeSegmentTree(n, Int.MAX_VALUE) { a, b -> minOf(a, b) }
    minTree.build(data)
    println("Min of [2, 6): ${minTree.query(2, 6)}") // min(5, 7, 9, 11) = 5

    // 3. Range Maximum Example
    println("\n--- Range Maximum ---")
    val maxTree = IterativeSegmentTree(n, Int.MIN_VALUE) { a, b -> maxOf(a, b) }
    maxTree.build(data)
    println("Max of [0, 3): ${maxTree.query(0, 3)}") // max(1, 3, 5) = 5
}