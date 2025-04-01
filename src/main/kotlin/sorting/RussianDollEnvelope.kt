package sorting

import java.util.*

class RussianDollEnvelope {
    fun maxEnvelopes(envelopes: Array<IntArray>): Int {
        // Step 1: Sort envelopes by width ascending and height descending
        envelopes.sortWith { a, b ->
            when {
                a[0] != b[0] -> a[0] - b[0]  // Sort by width ascending
                else -> b[1] - a[1]  // Sort by height descending
            }
        }

        // Step 2: Use TreeSet to keep track of LIS of heights
        val treeSet = TreeSet<Int>()

        for ((_, height) in envelopes) {
            // Step 3: Find the smallest element greater than or equal to the current height
            val ceilingHeight = treeSet.ceiling(height)
            if (ceilingHeight != null) {
                // If such an element exists, replace it with the current height
                treeSet.remove(ceilingHeight)
            }
            // Add the current height to the TreeSet
            treeSet.add(height)
        }

        // The size of the TreeSet represents the length of the longest increasing subsequence
        return treeSet.size
    }
}
