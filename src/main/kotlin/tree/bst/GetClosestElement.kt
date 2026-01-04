package tree.bst

import java.util.TreeMap

/**
 * A dynamic data structure that tracks integers and efficiently
 * finds the element closest to a given target.
 * * Performance:
 * - add/remove: O(log N)
 * - getClosest: O(log N)
 */
class ClosestTracker {
    // Stores frequency of each number to handle duplicates properly
    private val tree = TreeMap<Int, Int>()

    fun add(value: Int) {
        tree[value] = (tree[value] ?: 0) + 1
    }

    fun remove(value: Int) {
        val count = tree[value] ?: return
        if (count == 1) tree.remove(value) else tree[value] = count - 1
    }

    fun getClosest(target: Int): Int? {
        val floor = tree.floorKey(target)
        val ceiling = tree.ceilingKey(target)

        return when {
            // Case 1: One or both are null (target is outside tree range)
            floor == null || ceiling == null -> ceiling ?: floor

            // Case 2: Exact match found (distance is 0)
            floor == target -> floor

            // Case 3: Compare distances
            else -> if (target - floor <= ceiling - target) floor else ceiling
        }
    }

    fun isEmpty() = tree.isEmpty()
}

// --- Usage ---
fun main() {
    val tracker = ClosestTracker()
    tracker.add(10)
    tracker.add(20)
    tracker.add(30)

    println(tracker.getClosest(24)) // Output: 20 (since 24-20=4, 30-24=6)
    println(tracker.getClosest(25)) // Output: 20 (tie-break prefers smaller)
    println(tracker.getClosest(5))  // Output: 10 (floor is null)
}