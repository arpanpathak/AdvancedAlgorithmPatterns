package real_word_projects.parallel_algorithms

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun generateRandomMatrix(size: Int): Array<IntArray> =
    Array(size) { IntArray(size) { Random.nextInt(100) } }

fun matrixMultiplySequential(a: Array<IntArray>, b: Array<IntArray>): Array<IntArray> {
    val result = Array(a.size) { IntArray(b[0].size) }
    for (i in a.indices) {
        for (j in b[0].indices) {
            for (k in b.indices) {
                result[i][j] += a[i][k] * b[k][j]
            }
        }
    }
    return result
}

suspend fun matrixMultiplyParallel(a: Array<IntArray>, b: Array<IntArray>): Array<IntArray> = coroutineScope {
    val result = Array(a.size) { IntArray(b[0].size) }
    val numCoroutines = Runtime.getRuntime().availableProcessors()
    val chunkSize = (a.size + numCoroutines - 1) / numCoroutines

    val jobs = List(numCoroutines) { idx ->
        async(Dispatchers.Default) {
            val startRow = idx * chunkSize
            val endRow = minOf(startRow + chunkSize, a.size)
            for (i in startRow until endRow) {
                for (j in b[0].indices) {
                    for (k in b.indices) {
                        result[i][j] += a[i][k] * b[k][j]
                    }
                }
            }
        }
    }
    jobs.awaitAll()
    result
}

fun main() {
    val size = 2000
    val a = generateRandomMatrix(size)
    val b = generateRandomMatrix(size)

    val timeSequential = measureTimeMillis {
        val result = matrixMultiplySequential(a, b)
        println("Sequential result sample: ${result[0][0]}")
    }
    println("Time taken (Sequential): $timeSequential ms")

    val timeParallel = measureTimeMillis {
        runBlocking {
            val result = matrixMultiplyParallel(a, b)
            println("Parallel result sample: ${result[0][0]}")
        }
    }
    println("Time taken (Parallel): $timeParallel ms")
}
