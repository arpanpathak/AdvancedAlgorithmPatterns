package tree.segment

class MyCalendarTwo() {
    private class Node {
        var peakBookings = 0
        var lazyIncrement = 0
        var leftChild: Node? = null
        var rightChild: Node? = null
    }

    private val root = Node()
    private val minTime = 0
    private val maxTime = 1_000_000_000

    fun book(startTime: Int, endTime: Int): Boolean {
        val end = endTime - 1

        // Negative check first: if range max is already 2, adding 1 makes it 3
        return when (query(root, minTime, maxTime, startTime, end)) {
            in 2..Int.MAX_VALUE -> false
            else -> {
                lazyUpdate(root, minTime, maxTime, startTime, end, 1)
                true
            }
        }
    }

    private fun lazyUpdate(node: Node, rangeStart: Int, rangeEnd: Int, queryLeft: Int, queryRight: Int, value: Int) {
        if (queryLeft <= rangeStart && rangeEnd <= queryRight) {
            node.peakBookings += value
            node.lazyIncrement += value
            return
        }

        pushDown(node)
        val mid = rangeStart + (rangeEnd - rangeStart) / 2

        if (queryLeft <= mid) {
            node.leftChild?.let { lazyUpdate(it, rangeStart, mid, queryLeft, queryRight, value) }
        }
        if (queryRight > mid) {
            node.rightChild?.let { lazyUpdate(it, mid + 1, rangeEnd, queryLeft, queryRight, value) }
        }

        val leftMax = node.leftChild?.peakBookings ?: 0
        val rightMax = node.rightChild?.peakBookings ?: 0
        node.peakBookings = maxOf(leftMax, rightMax)
    }

    private fun query(node: Node?, rangeStart: Int, rangeEnd: Int, queryLeft: Int, queryRight: Int): Int =
        when {
            node == null || queryLeft > rangeEnd || queryRight < rangeStart -> 0
            queryLeft <= rangeStart && rangeEnd <= queryRight -> node.peakBookings
            else -> {
                pushDown(node)
                val mid = rangeStart + (rangeEnd - rangeStart) / 2
                maxOf(
                    query(node.leftChild, rangeStart, mid, queryLeft, queryRight),
                    query(node.rightChild, mid + 1, rangeEnd, queryLeft, queryRight)
                )
            }
        }

    private fun pushDown(node: Node) {
        val left = node.leftChild ?: Node().also { node.leftChild = it }
        val right = node.rightChild ?: Node().also { node.rightChild = it }

        if (node.lazyIncrement != 0) {
            val shift = node.lazyIncrement
            listOf(left, right).forEach { child ->
                child.peakBookings += shift
                child.lazyIncrement += shift
            }
            node.lazyIncrement = 0
        }
    }
}