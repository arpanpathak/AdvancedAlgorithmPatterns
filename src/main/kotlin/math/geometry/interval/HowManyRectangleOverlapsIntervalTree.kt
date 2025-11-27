package math.geometry.interval

data class Rectangle(
    val bottomX: Int,
    val bottomY: Int,
    val topX: Int,
    val topY: Int
) {
    val yInterval = bottomY..topY
}

enum class EventType(val value: Int) { START(1), END(-1) }

data class SweepEvent(
    val x: Int,
    val rect: Rectangle,
    val type: EventType
) : Comparable<SweepEvent> {
    override fun compareTo(other: SweepEvent): Int {
        if (this.x != other.x) return this.x.compareTo(other.x)
        return other.type.value.compareTo(this.type.value)
    }
}

/**
 * Simple Interval Tree Node for Y-intervals
 */
class IntervalTreeNode(
    val interval: IntRange,
    val rect: Rectangle,
    var maxEnd: Int = interval.last
) {
    var left: IntervalTreeNode? = null
    var right: IntervalTreeNode? = null
}

/**
 * Interval Tree for efficient Y-interval overlap queries
 */
class IntervalTree {
    private var root: IntervalTreeNode? = null

    fun insert(rect: Rectangle) {
        root = insertRec(root, IntervalTreeNode(rect.yInterval, rect))
    }

    fun remove(rect: Rectangle) {
        root = removeRec(root, rect.yInterval)
    }

    fun queryOverlaps(query: IntRange): List<Rectangle> {
        val results = mutableListOf<Rectangle>()
        queryRec(root, query, results)
        return results
    }

    private fun insertRec(node: IntervalTreeNode?, newNode: IntervalTreeNode): IntervalTreeNode {
        if (node == null) return newNode

        val compare = newNode.interval.first.compareTo(node.interval.first)

        if (compare < 0) {
            node.left = insertRec(node.left, newNode)
        } else {
            node.right = insertRec(node.right, newNode)
        }

        node.maxEnd = maxOf(node.maxEnd, newNode.interval.last)
        return node
    }

    private fun removeRec(node: IntervalTreeNode?, interval: IntRange): IntervalTreeNode? {
        if (node == null) return null

        when {
            interval.first < node.interval.first ->
                node.left = removeRec(node.left, interval)
            interval.first > node.interval.first ->
                node.right = removeRec(node.right, interval)
            else -> {
                // Found node to remove
                if (node.left == null) return node.right
                if (node.right == null) return node.left

                val minNode = findMin(node.right!!)
                node.right = removeRec(node.right, minNode.interval)
            }
        }

        node.maxEnd = maxOf(
            node.interval.last,
            node.left?.maxEnd ?: Int.MIN_VALUE,
            node.right?.maxEnd ?: Int.MIN_VALUE
        )
        return node
    }

    private fun findMin(node: IntervalTreeNode): IntervalTreeNode {
        return node.left?.let { findMin(it) } ?: node
    }

    private fun queryRec(node: IntervalTreeNode?, query: IntRange, results: MutableList<Rectangle>) {
        if (node == null) return

        // Check if current node overlaps
        if (overlaps(node.interval, query)) {
            results.add(node.rect)
        }

        // Check left subtree if it might contain overlaps
        if (node.left != null && node.left!!.maxEnd >= query.first) {
            queryRec(node.left, query, results)
        }

        // Check right subtree
        if (node.interval.first <= query.last) {
            queryRec(node.right, query, results)
        }
    }

    private fun overlaps(a: IntRange, b: IntRange): Boolean {
        return a.first <= b.last && a.last >= b.first
    }
}

/**
 * Ultra Efficient Sweep Line with Interval Tree
 */
fun countOverlappingPairsOptimized(rectangles: List<Rectangle>): Int {
    if (rectangles.size < 2) return 0

    val events = rectangles.flatMap { rect ->
        listOf(
            SweepEvent(rect.bottomX, rect, EventType.START),
            SweepEvent(rect.topX, rect, EventType.END)
        )
    }.sorted()

    var overlapCount = 0
    val intervalTree = IntervalTree()

    for (event in events) {
        when (event.type) {
            EventType.START -> {
                // Query all rectangles that overlap in Y-axis - O(log N + K)
                val overlappingRects = intervalTree.queryOverlaps(event.rect.yInterval)
                overlapCount += overlappingRects.size

                intervalTree.insert(event.rect)
            }
            EventType.END -> {
                intervalTree.remove(event.rect)
            }
        }
    }
    return overlapCount
}

fun main() {
    val rects = listOf(
        Rectangle(bottomX = 0, bottomY = 5, topX = 5, topY = 10),
        Rectangle(bottomX = 3, bottomY = 3, topX = 7, topY = 7),
        Rectangle(bottomX = 6, bottomY = 2, topX = 10, topY = 6),
        Rectangle(bottomX = 15, bottomY = 10, topX = 20, topY = 15)
    )

    println("Total overlapping pairs: ${countOverlappingPairsOptimized(rects)}") // Output: 2
}