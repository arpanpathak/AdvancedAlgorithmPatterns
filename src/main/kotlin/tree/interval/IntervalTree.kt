package tree.interval

class IntervalTree {
    data class Interval(val start: Int, val end: Int) : Comparable<Interval> {
        override fun compareTo(other: Interval) = start.compareTo(other.start)
        infix fun overlaps(other: Interval) = start <= other.end && end >= other.start
    }

    private class Node(val interval: Interval) {
        var maxEnd = interval.end
        var left: Node? = null
        var right: Node? = null
    }

    private var root: Node? = null

    fun insert(start: Int, end: Int) {
        require(start <= end) { "Invalid interval" }

        fun insertNode(node: Node?, interval: Interval): Node {
            when {
                node == null -> return Node(interval)
                interval < node.interval -> node.left = insertNode(node.left, interval)
                else -> node.right = insertNode(node.right, interval)
            }

            node.maxEnd = node.interval.end

            // Update the max end.
            node.left?.let { node.maxEnd = maxOf(node.maxEnd, it.maxEnd) }
            node.right?.let { node.maxEnd = maxOf(node.maxEnd, it.maxEnd) }

            return node
        }

        root = insertNode(root, Interval(start, end))
    }

    fun findOverlapping(start: Int, end: Int): List<Interval> {
        val result = mutableListOf<Interval>()

        fun findOverlaps(node: Node?, query: Interval) {
            when {
                node == null -> return
                node.interval overlaps query -> result.add(node.interval)
            }

            node?.left?.takeIf { it.maxEnd >= query.start }?.let {
                findOverlaps(it, query)
            }

            findOverlaps(node?.right, query)
        }

        findOverlaps(root, Interval(start, end))
        return result
    }
}

fun main() {
    val tree = IntervalTree()
    tree.insert(15, 20)
    tree.insert(10, 30)
    tree.insert(17, 19)
    tree.insert(5, 20)
    tree.insert(12, 15)
    tree.insert(30, 40)

    println("Overlapping with [14, 16]: ${tree.findOverlapping(14, 16)}")
    println("Overlapping with [21, 23]: ${tree.findOverlapping(21, 23)}")
    println("Overlapping with [25, 35]: ${tree.findOverlapping(25, 35)}")
}
