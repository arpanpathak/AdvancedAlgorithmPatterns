package tree.segment

class DynamicSegmentTree(
    private val timeRange: IntRange,
    private val aggregate: (Int, Int) -> Int
) {
    private class Node {
        var value = 0
        var lazyIncrement = 0
        var leftChild: Node? = null
        var rightChild: Node? = null

        fun applyIncrement(inc: Int) {
            value += inc
            lazyIncrement += inc
        }
    }

    private val root = Node()

    fun update(queryLeft: Int, queryRight: Int, increment: Int) =
        lazyUpdate(root, timeRange.first, timeRange.last, queryLeft, queryRight, increment)

    fun query(queryLeft: Int, queryRight: Int): Int =
        query(root, timeRange.first, timeRange.last, queryLeft, queryRight)

    private fun lazyUpdate(node: Node, start: Int, end: Int, qL: Int, qR: Int, inc: Int) {
        when {
            qL > end || qR < start -> return
            qL <= start && end <= qR -> node.applyIncrement(inc)
            else -> {
                pushDown(node)
                val mid = start + (end - start) / 2
                lazyUpdate(node.leftChild!!, start, mid, qL, qR, inc)
                lazyUpdate(node.rightChild!!, mid + 1, end, qL, qR, inc)

                node.value = aggregate(
                    node.leftChild?.value ?: 0,
                    node.rightChild?.value ?: 0
                )
            }
        }
    }

    private fun query(node: Node?, start: Int, end: Int, qL: Int, qR: Int): Int = when {
        node == null || qL > end || qR < start -> 0
        qL <= start && end <= qR -> node.value
        else -> {
            pushDown(node)
            val mid = start + (end - start) / 2
            aggregate(
                query(node.leftChild, start, mid, qL, qR),
                query(node.rightChild, mid + 1, end, qL, qR)
            )
        }
    }

    private fun pushDown(node: Node) {
        val left = node.leftChild ?: Node().also { node.leftChild = it }
        val right = node.rightChild ?: Node().also { node.rightChild = it }

        if (node.lazyIncrement != 0) {
            left.applyIncrement(node.lazyIncrement)
            right.applyIncrement(node.lazyIncrement)
            node.lazyIncrement = 0
        }
    }
}

class MyCalendarTwo_II() {
    private val tree = DynamicSegmentTree(0..1_000_000_000, ::maxOf)

    fun book(startTime: Int, endTime: Int): Boolean {
        val end = endTime - 1

        return when (tree.query(startTime, end)) {
            in 2..Int.MAX_VALUE -> false
            else -> {
                tree.update(startTime, end, 1)
                true
            }
        }
    }
}