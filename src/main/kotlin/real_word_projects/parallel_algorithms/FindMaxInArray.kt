package real_word_projects.parallel_algorithms

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.random.Random

// Non-parallel function to find the maximum value in the entire array
fun findMaxNonParallel(array: IntArray): Int {
    var max = array[0]
    for (num in array) {
        if (num > max) {
            max = num
        }
    }
    return max
}

// Function to find the maximum in a part of the array (used in the parallel approach)
suspend fun findMaxInSegment(array: IntArray, start: Int, end: Int): Int {
    var max = array[start]
    for (i in start + 1 until end) {
        if (array[i] > max) {
            max = array[i]
        }
    }
    return max
}

// Parallel function to find the maximum using coroutines
suspend fun findMaxParallel(array: IntArray, numCoroutines: Int = Runtime.getRuntime().availableProcessors()): Int = coroutineScope {
    val segmentSize = array.size / numCoroutines
    val deferredResults = (0 until numCoroutines).map { index ->
        async {
            val start = index * segmentSize
            val end = if (index == numCoroutines - 1) array.size else start + segmentSize
            findMaxInSegment(array, start, end)
        }
    }
    deferredResults.awaitAll().maxOrNull() ?: Int.MIN_VALUE
}

fun main() {
    // Generate a large array with random values
    val size = 1_000_000
    val randomArray = IntArray(size) { Random.nextInt(0, 1000000) }

    // Measure time and compute maximum in a non-parallel way
    val timeNonParallel = measureTimeMillis {
        val maxNonParallel = findMaxNonParallel(randomArray)
        println("Maximum (Non-Parallel): $maxNonParallel")
    }
    println("Time taken (Non-Parallel): $timeNonParallel ms")

    // Measure time and compute maximum in a parallel way
    val timeParallel = measureTimeMillis {
        runBlocking {
            val maxParallel = findMaxParallel(randomArray)
            println("Maximum (Parallel): $maxParallel")
        }
    }
    println("Time taken (Parallel): $timeParallel ms")
}
