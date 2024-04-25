package binarysearch

fun findBestBlock(blocks: List<Map<String, Boolean>>, requirements: List<String>): Int {
    // Step 1: Create a HashMap where each key is an amenity and each value is a sorted list of blocks where the amenity is present
    val amenityMap = mutableMapOf<String, MutableList<Int>>()
    blocks.forEachIndexed { index, block ->
        block.forEach { (amenity, present) ->
            if (present) {
                amenityMap.getOrPut(amenity) { mutableListOf() }.add(index)
            }
        }
    }

    // Ensure all required amenities are present
    requirements.forEach {
        if (amenityMap[it].isNullOrEmpty()) return -1 // Return -1 if any required amenity is not present in any block
    }

    // Step 2: Determine the optimal block
    var bestBlock = -1
    var bestMaxDistance = Int.MAX_VALUE

    // N * R * Log(K) , K = number of unique amininties present across all the blocks
    blocks.forEachIndexed { index, _ ->
        val maxDistance = requirements.map { amenity ->
            closestDistance(index, amenityMap[amenity]!!)
        }.maxOrNull()!!

        // Check if the current block has the smallest maximum distance
        if (maxDistance < bestMaxDistance) {
            bestMaxDistance = maxDistance
            bestBlock = index
        }
    }

    return bestBlock
}

// Function to find the minimum distance to the closest block with the given amenity
fun closestDistance(blockIndex: Int, blocksWithAmenity: List<Int>): Int {
    val pos = blocksWithAmenity.binarySearch(blockIndex)
    return if (pos >= 0) 0
    else {
        val insertPoint = -pos - 1
        val leftDistance = if (insertPoint > 0) blockIndex - blocksWithAmenity[insertPoint - 1] else Int.MAX_VALUE
        val rightDistance = if (insertPoint < blocksWithAmenity.size) blocksWithAmenity[insertPoint] - blockIndex else Int.MAX_VALUE
        minOf(leftDistance, rightDistance)
    }
}

// Custom Binary Search implementation
fun binarySearch(arr: List<Int>, target: Int): Int {
    var (low, high) = listOf(0, arr.lastIndex)

    while (low<= high) {
        val mid = low + (high - low) / 2
        when {
            arr[mid] < target -> low = mid + 1
            arr[mid] > target -> high = mid - 1
            else -> return mid
        }
    }

    return -(low + 1)
}



fun main() {
    val blocks = listOf(
        mapOf("gym" to false, "school" to true, "store" to false),
        mapOf("gym" to true, "school" to false, "store" to false),
        mapOf("gym" to true, "school" to true, "store" to false),
        mapOf("gym" to false, "school" to true, "store" to false),
        mapOf("gym" to false, "school" to true, "store" to true)
    )
    val requirements = listOf("gym", "school", "store")


    val bestBlock = findBestBlock(blocks, requirements)
    println("The best block to choose is: $bestBlock")
}
