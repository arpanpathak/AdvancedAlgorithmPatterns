package real_word_projects

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

// ConcurrentHashMap to store Fibonacci results for memoization
val memo = ConcurrentHashMap<Int, Long>()

// Iterative method to calculate Fibonacci numbers
fun fibonacciIterative(n: Int): Long {
    if (n <= 1) return n.toLong()
    var a = 0L
    var b = 1L
    for (i in 2..n) {
        val sum = a + b
        a = b
        b = sum
    }
    return b
}

// Parallel recursive Fibonacci function using coroutines
suspend fun fibonacciParallel(n: Int): Long {
    if (n <= 1) return n.toLong()
    memo[n]?.let { return it }

    // Compute the two previous Fibonacci numbers in parallel
    val fibNMinus1 = coroutineScope { async { fibonacciParallel(n - 1) } }
    val fibNMinus2 = coroutineScope { async { fibonacciParallel(n - 2) } }

    // Wait for results and add them
    val result = fibNMinus1.await() + fibNMinus2.await()

    // Store the computed result in the map
    memo[n] = result
    return result
}

fun main() = runBlocking {
    val n = 100

    // Measure time for iterative Fibonacci
    val timeIterative = measureTimeMillis {
        val result = fibonacciIterative(n)
        println("Iterative Fibonacci of $n is $result")
    }
    println("Time taken for iterative method: $timeIterative ms\n")

    // Measure time for parallel Fibonacci
    val timeParallel = measureTimeMillis {
        println("Parallel Fibonacci of $n is ${fibonacciParallel(n)}")
    }
    println("Time taken for parallel computation: $timeParallel ms")
}
