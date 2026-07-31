# 19.14 The Structure Gallery — Compression, Histograms, and BuildList

> **Sources:** [`RectangleArea_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/math/geometry/RectangleArea_II.kt) · `MaximalRectangle.kt` · `BinaryTreeVerticalOrderTraversal.kt` · `ReverseWordsInString.kt` · `math/pow.kt` · `graph/scc/Kosaraju.kt`
> **Pattern:** variant gallery — the "structure tricks" the main pages mention but don't show

## 1. `RectangleArea_II.kt` — coordinate compression, functionally

The "area of the union of axis-aligned rectangles" hard problem — the `flatMap`-to-`distinct`-to-`sorted` X-coordinate extraction is the compression's whole setup in one chain:

```kotlin
class RectangleArea_II {
    fun rectangleArea(rectangles: Array<IntArray>): Int {
        val MOD = 1_000_000_007L

        // 1. Collect all unique X coordinates to define the vertical strips
        val xCoords = rectangles.flatMap { listOf(it[0], it[2]) }.distinct().sorted()

        var totalArea = 0L

        // 2. Iterate each vertical strip [xCoords[i], xCoords[i+1]]
        for (i in 0 until xCoords.size - 1) {
            val width = (xCoords[i + 1] - xCoords[i]).toLong()
            if (width == 0L) continue

            // 3. Find the rectangles covering this strip
            val activeYIntervals = rectangles
                .filter { it[0] <= xCoords[i] && it[2] >= xCoords[i + 1] }
                .map { it[1] to it[3] }
                .sortedBy { it.first }

            // 4. Union the Y intervals (the 1-D sub-problem)
            var currentYHeight = 0L
            var lastY = -1
            for ((yStart, yEnd) in activeYIntervals) {
                // ... standard interval union: add only the uncovered part
            }
            totalArea = (totalArea + width * currentYHeight) % MOD
        }
        return totalArea.toInt()
    }
}
```

**What's cool:** `flatMap { listOf(it[0], it[2]) }` flattens each rectangle into its two X-edges; `distinct().sorted()` dedupes and orders — the compressed axis in two lines. The strip loop then re-filters the rectangles per strip (`filter` on coverage) and reduces to the **1-D interval-union sub-problem** — the [11.9](../ch11-greedy/non-overlapping-intervals.md) sort-and-merge machinery. Compression + sweep, told functionally.

## 2. `MaximalRectangle.kt` — the histogram-stack upgrade

The classic "largest rectangle in a binary matrix" via **per-row histograms + the [8.5](../ch08-stacks/largest-rectangle-in-histogram.md) stack**:

```kotlin
class MaximalRectangle {
    fun largestRectangleArea(heights: IntArray): Int {
        val stack = Stack<Int>()
        var (maxArea, i) = listOf(0, 0)

        while (i <= heights.size) {
            val currentHeight = if (i == heights.size) 0 else heights[i]   // sentinel 0 flushes

            when {
                stack.isEmpty() || heights[stack.last()] <= currentHeight -> stack.add(i++)
                else -> {
                    val height = heights[stack.pop()]
                    val width = if (stack.isEmpty()) i else i - stack.peek() - 1
                    maxArea = maxOf(maxArea, height * width)
                }
            }
        }
        return maxArea
    }
    // ... plus the per-row histogram accumulation: heights[j] = if (matrix[i][j] == '1') heights[j] + 1 else 0
}
```

**What's cool:** the `i == heights.size ? 0` sentinel flushes the stack without a post-loop; the `when` is the monotonic-stack three-way decision ([8.5](../ch08-stacks/largest-rectangle-in-histogram.md) compressed); and the row-major histogram update turns the matrix problem into repeated 1-D problems.

## 3. `BinaryTreeVerticalOrderTraversal.kt` — the `data class` BFS state

The vertical-order BFS carries `(node, column)` — and the repo even shows the *functional DFS sketch* commented out, with `TreeMap` + `getOrPut`:

```kotlin
// The commented-out functional DFS (the "what if" sketch):
//   fun dfs(node: TreeNode?, verticalIndex: Int = 0) {
//       if (node == null) return
//       val bucket = result.getOrPut(verticalIndex) { LinkedList() }
//       bucket.add(node.`val`)
//       dfs(node.left, verticalIndex - 1)
//       dfs(node.right, verticalIndex + 1)
//   }
//   return result.map { it.value }

// The BFS version uses an explicit state carrier:
data class VerticalIndex(val node: TreeNode, val verticalIndex: Int)

fun verticalOrder(root: TreeNode?): List<List<Int>> {
    if (root == null) return emptyList()
    val result = TreeMap<Int, ArrayList<Int>>()
    val queue: Queue<VerticalIndex> = LinkedList()
    queue.offer(VerticalIndex(root, 0))
    // ... BFS with (node, column) pairs; TreeMap keeps columns sorted
}
```

**What's cool:** the commented DFS is the *teaching artifact* — it shows the natural (but order-incorrect) recursion before the BFS that fixes level order; `getOrPut(verticalIndex) { LinkedList() }` is the bucket-create idiom; and the `data class` state carrier is the [6.x](../ch06-graphs/pattern-primer.md) "BFS with payload" pattern.

## 4. `ReverseWordsInString.kt` — split, filter, swap

[9.6](../ch09-strings/reverse-words-in-a-string.md) documents the two-pointer word reversal; this file is the **split-filter-swap** flavor:

```kotlin
class ReverseWordsInString {
    fun reverseWords(s: String): String {
        val words = s.split(" ").filter { it.isNotEmpty() }.toMutableList()

        var (start, end) = Pair(0, words.lastIndex)
        while (start < end) {
            words[end] = words[start].also { words[start] = words[end] }   // the also-swap
            start++
            end--
        }
        return words.joinToString(separator = " ").trim()
    }
}
```

**What's cool:** `split(" ").filter { it.isNotEmpty() }` handles the multi-space case declaratively (the [9.6](../ch09-strings/reverse-words-in-a-string.md) scanner's whitespace-skip in one filter); the `also`-swap is Kotlin's idiomatic exchange; `joinToString` rebuilds. Different machinery, same O(n).

## 5. `math/pow.kt` — the binary exponentiation pair

Both the recursive and iterative fast-pow in one file:

```kotlin
class pow {
    fun myPow(x: Double, n: Int): Double {
        fun pow(x: Double, n: Long): Double {
            if (n == 0L) return 1.0
            val half = pow(x, n / 2)
            return when (n % 2) {
                0L -> half * half
                else -> x * half * half
            }
        }
        return if (n >= 0) pow(x, n.toLong()) else 1 / pow(x, n.toLong())
    }

    fun myPowIterative(x: Double, n: Int): Double {
        var base = x
        var exponent = n.toLong()
        var result = 1.0
        // ... classic: while (exponent > 0) { if odd result *= base; base *= base; exponent /= 2 }
    }
}
```

**What's cool:** the `when (n % 2)` odd/even branch is the recurrence in one expression; the `Long` cast dodges `Int.MIN_VALUE` overflow; and having *both* the recursion and the loop in one file is the "same algorithm, two costumes" thesis of this chapter in miniature.

## 6. `graph/scc/Kosaraju.kt` — SCCs with `buildList`

The [6.7](../ch06-graphs/strongly-connected-components.md) algorithm, with the component collection expressed via `buildList`:

```kotlin
// Learnt new construct called build list...
return buildList {
    while (visitOrderStack.isNotEmpty()) {
        val vertex = visitOrderStack.removeLast()
        if (vertex !in visited) {
            add(buildList { dfsOnReversed(vertex, visited, this) })   // one component per add
        }
    }
}
```

**What's cool:** the nested `buildList` — the outer builds the component list, the inner builds one SCC via the DFS — is the "collect the result functionally" idiom; the `dfsOnReversed(vertex, visited, this)` writes into the receiver directly. The repo even comments the discovery ("Learnt new construct called build list") — a delightful artifact of the learning process.

## The meta-lesson

The structure tricks in this file — compression, sentinel-flush, state-carrier data classes, `buildList` — are the *"how would you make this faster/cleaner?"* answers the main pages promise. Each one is a reusable idea: compression for sparse coordinates, sentinel-0 for stack flushing, `data class` for BFS payloads, `buildList` for recursive collection.

## Variants & follow-ups

- **Largest Rectangle In Histogram** ([8.5](../ch08-stacks/largest-rectangle-in-histogram.md)) — the stack the MaximalRectangle row-loop calls.
- **Skyline Problem** ([7.8](../ch07-heaps/the-skyline-problem.md)) — the sweep-line sibling of RectangleArea_II's strips.
- **Reverse Words In A String** ([9.6](../ch09-strings/reverse-words-in-a-string.md)) — the scanner version this page's split-filter replaces.
- **Interview follow-up:** "When does coordinate compression matter?" When coordinates are sparse (rectangles at x ∈ {0, 10⁹}) — iterating raw coordinates is O(10⁹); iterating the compressed set is O(2n). The `flatMap.distinct().sorted()` chain IS the compression: the axis's meaningful points are exactly the rectangle edges.
