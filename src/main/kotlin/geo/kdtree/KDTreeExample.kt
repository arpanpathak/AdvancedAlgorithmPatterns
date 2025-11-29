package geo.kdtree

import java.util.PriorityQueue

// --- Utility Function ---

/**
 * Calculates the squared Euclidean distance between two points.
 */
fun distanceSq(p1: Point, p2: Point): Double {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    return dx * dx + dy * dy
}

// --- Data Structures ---

data class Point(val x: Double, val y: Double)

// Stores a neighbor's data and sorts based on distance (Max-Heap logic)
data class Neighbor(val point: Point, val distanceSq: Double) : Comparable<Neighbor> {
    // Reverses comparison: makes the largest distance (farthest point) appear at the top.
    override fun compareTo(other: Neighbor): Int {
        return other.distanceSq.compareTo(this.distanceSq)
    }
}

data class KDTreeNode(
    val point: Point,
    val depth: Int,
    var left: KDTreeNode? = null,
    var right: KDTreeNode? = null
)

// FIX: Added 'val' keywords to the Rectangle data class constructor parameters.
data class Rectangle(
    val minX: Double, val maxX: Double, val minY: Double, val maxY: Double
) {
    fun contains(point: Point): Boolean {
        return point.x in minX..maxX &&
                point.y in minY..maxY
    }
}

// --- KDTree Class ---

class KDTree {
    private var root: KDTreeNode? = null
    private val K = 2 // Dimension of the space (2D)

    // ## Insertion Logic (Optimized)

    fun insert(point: Point) {
        root = insert(root, point, 0)
    }

    private fun insert(node: KDTreeNode?, newPoint: Point, depth: Int): KDTreeNode {
        if (node == null) return KDTreeNode(newPoint, depth)

        val axis = depth % K
        val newDepth = depth + 1

        val shouldGoLeft: Boolean = when (axis) {
            0 -> newPoint.x < node.point.x
            1 -> newPoint.y < node.point.y
            else -> throw IllegalStateException("k-d tree is only 2D (K=$K)")
        }

        return node.apply {
            when (shouldGoLeft) {
                true -> left = insert(left, newPoint, newDepth)
                false -> right = insert(right, newPoint, newDepth)
            }
        }
    }

    // ## k-Nearest Neighbors Search Logic

    fun findKNearestNeighbors(query: Point, k: Int): List<Point> {
        if (root == null || k <= 0) return emptyList()
        val maxHeap = PriorityQueue<Neighbor>()
        findKNearestNeighbors(root, query, k, maxHeap)
        return maxHeap.map { it.point }.reversed()
    }

    private fun findKNearestNeighbors(
        node: KDTreeNode?,
        query: Point,
        k: Int,
        maxHeap: PriorityQueue<Neighbor>
    ) {
        if (node == null) return

        val nodeDistSq = distanceSq(node.point, query)
        if (maxHeap.size < k) {
            maxHeap.add(Neighbor(node.point, nodeDistSq))
        } else if (nodeDistSq < maxHeap.peek().distanceSq) {
            maxHeap.poll()
            maxHeap.add(Neighbor(node.point, nodeDistSq))
        }

        val axis = node.depth % K
        val queryCoord: Double = if (axis == 0) query.x else query.y
        val nodeCoord: Double = if (axis == 0) node.point.x else node.point.y

        val isQueryLeft = queryCoord < nodeCoord
        val nearChild = if (isQueryLeft) node.left else node.right
        val farChild = if (isQueryLeft) node.right else node.left

        val distanceToSplitSq = (queryCoord - nodeCoord) * (queryCoord - nodeCoord)

        findKNearestNeighbors(nearChild, query, k, maxHeap)

        val farthestDistInHeap = maxHeap.peek()?.distanceSq ?: Double.POSITIVE_INFINITY

        if (distanceToSplitSq < farthestDistInHeap) {
            findKNearestNeighbors(farChild, query, k, maxHeap)
        }
    }

    // ## Range Search Logic

    fun rangeSearch(rect: Rectangle): List<Point> {
        val results = mutableListOf<Point>()
        rangeSearch(root, rect, results)
        return results
    }

    private fun rangeSearch(node: KDTreeNode?, rect: Rectangle, results: MutableList<Point>) {
        if (node == null) return

        if (rect.contains(node.point)) {
            results.add(node.point)
        }

        val axis = node.depth % K
        val splitCoord = if (axis == 0) node.point.x else node.point.y

        when (axis) {
            0 -> { // Split on X
                if (rect.minX <= splitCoord) {
                    rangeSearch(node.left, rect, results)
                }
                if (rect.maxX >= splitCoord) {
                    rangeSearch(node.right, rect, results)
                }
            }
            1 -> { // Split on Y
                if (rect.minY <= splitCoord) {
                    rangeSearch(node.left, rect, results)
                }
                if (rect.maxY >= splitCoord) {
                    rangeSearch(node.right, rect, results)
                }
            }
            else -> {}
        }
    }

    // ## Utility Methods

    fun printTree() {
        println("--- KD-Tree Structure ---")
        printTree(root, "")
    }

    private fun printTree(node: KDTreeNode?, prefix: String) {
        if (node != null) {
            val axisName = if (node.depth % K == 0) "X" else "Y"
            println("${prefix}Depth ${node.depth}: (${node.point.x}, ${node.point.y}) - Split on $axisName")
            printTree(node.left, "$prefix|--L ")
            printTree(node.right, "$prefix|--R ")
        }
    }
}

// --- Main Testing Function ---

fun main() {
    val tree = KDTree()
    val points = listOf(
        Point(7.0, 2.0), Point(5.0, 4.0), Point(9.0, 6.0), Point(2.0, 3.0),
        Point(4.0, 7.0), Point(8.0, 1.0), Point(1.0, 8.0)
    )

    // 1. Insertion Test
    println("--- 1. Insertion & Structure Test ---")
    points.forEach { tree.insert(it) }
    tree.printTree()

    // 2. k-Nearest Neighbor Search Test
    println("\n--- 2. k-Nearest Neighbor Test (k=3) ---")
    val queryPoint = Point(2.5, 4.0)
    val kValue = 3
    val nearestNeighbors = tree.findKNearestNeighbors(queryPoint, kValue)

    println("Query Point: (${queryPoint.x}, ${queryPoint.y})")
    println("k=$kValue Nearest Neighbors (Nearest to Farthest):")

    nearestNeighbors.forEach { nn ->
        val distSq = distanceSq(queryPoint, nn)
        println("  (${nn.x}, ${nn.y}) (Distance Squared: $distSq)")
    }

    // 3. Range Search Test
    println("\n--- 3. Range Search Test ---")
    val queryRect = Rectangle(minX = 3.0, maxX = 8.0, minY = 3.0, maxY = 7.5)
    val foundPoints = tree.rangeSearch(queryRect)

    println("Query Box: X:[${queryRect.minX}, ${queryRect.maxX}], Y:[${queryRect.minY}, ${queryRect.maxY}]")
    println("Found ${foundPoints.size} points:")
    foundPoints.forEach { println("  (${it.x}, ${it.y})") }
}
