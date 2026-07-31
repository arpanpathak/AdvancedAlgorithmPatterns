# 19.1 Travelling Salesman — The Top-Down Functional Family

> **Sources:** [`src/main/kotlin/graph/tsp/TravellingSalesPersonTopDownDP.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/tsp/TravellingSalesPersonTopDownDP.kt) · `TravellingSalesmanRecursiveDP.kt` · `TravellingSalesPersonTopDownDP.kt` · `TravellingSalespersonProblemBruteforceMatrix.kt` · `TSPHelpKarp.kt`
> **Pattern:** variant gallery — top-down vs [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md)'s bottom-up Held-Karp

## The problem (recap)

The TSP: visit every city exactly once and return home, minimizing total distance. [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md) documents the **bottom-up Held-Karp** table. The repo also ships a **top-down functional** family — and this one is a joy to read: the whole DP is a single expression.

## The star: `TravellingSalesPersonTopDownDP.kt`

The entire algorithm is a `getOrPut` memoized recursion with bitmask helper extensions:

```kotlin
class TravellingSalesmanTopDownDP {
    private data class State(val visitedCitiesBitmask: Int, val currentCity: Int)

    private fun Int.isNotVisited(city: Int) = (this and (1 shl city)) == 0
    private fun Int.visitCity(city: Int) = this or (1 shl city)

    fun solveTSP(dist: Array<IntArray>): Int {
        val totalCities = dist.size
        // The bitmask where all bits are set: all cities visited
        val allCitiesVisitedMask = (1 shl totalCities) - 1
        val cache = mutableMapOf<State, Int>()

        fun tspDfs(visitedCitiesBitmask: Int, currentCity: Int): Int =
            cache.getOrPut(State(visitedCitiesBitmask, currentCity)) {
                when (visitedCitiesBitmask) {
                    // All cities visited: return the cost to go home (city 0)
                    allCitiesVisitedMask -> dist[currentCity][0]

                    // The unvisited city that minimizes the total tour cost
                    else -> (0 until totalCities)
                        .filter { nextCity -> visitedCitiesBitmask.isNotVisited(nextCity) }
                        .minOfOrNull { nextCity ->
                            dist[currentCity][nextCity]
                                + tspDfs(visitedCitiesBitmask.visitCity(nextCity), nextCity)
                        } ?: Int.MAX_VALUE
                }
            }

        return tspDfs(0.visitCity(0), 0)
    }
}
```

**What makes it cool:**

- **The state is a `data class`** — `(bitmask, currentCity)` as a value type, hashed by the map. No `Pair` fumbling.
- **Bitmask helpers read like prose** — `isNotVisited` / `visitCity` as `Int` extensions turn `(mask and (1 shl v)) == 0` into `visitedCitiesBitmask.isNotVisited(nextCity)`.
- **The recurrence is one expression** — `getOrPut` memoizes *and* returns; the `when` has the base case and the minimize-all-unvisited-cities case in the same breath. `minOfOrNull` + `?: Int.MAX_VALUE` handles "no unvisited city" without a loop.
- **Start state reads as intent** — `tspDfs(0.visitCity(0), 0)` = "home visited, at home". The bitmask starts at `1` (bit 0 set).

## The sibling: `TravellingSalesmanRecursiveDP.kt`

The same algorithm with explicit `memo.containsKey` bookkeeping and `memo[state] = it` caching:

```kotlin
class TravellingSalesmanRecursiveDP {
    fun solveTSP(dist: Array<IntArray>): Int {
        val n = dist.size
        val allVisited = (1 shl n) - 1
        val memo = mutableMapOf<Pair<Int, Int>, Int>()

        fun dp(mask: Int, u: Int): Int {
            // Base case: all cities visited, return the cost to go home (city 0)
            if (mask == allVisited) return dist[u][0]

            val state = mask to u
            if (memo.containsKey(state)) return memo[state]!!

            val minCost = (0 until n)
                .filter { v -> (mask and (1 shl v)) == 0 }   // unvisited cities only
                .minOfOrNull { v -> dist[u][v] + dp(mask or (1 shl v), v) }
                ?: Int.MAX_VALUE

            return minCost.also { memo[state] = it }         // cache it
        }

        // Start at home (index 0) with home already visited (mask 1)
        return dp(1, 0)
    }
}
```

**Why both?** The `RecursiveDP` version is the *pedagogical* spelling — explicit cache check, explicit `also { memo[state] = it }` — perfect for walking through in an interview. The `TopDownDP` version is the *production* spelling — `getOrPut` folds the check-and-store into one call. Same `O(n²·2ⁿ)` algorithm, two dialects, and the repo keeps both so you can see the refactor.

## The path-reconstructing bonus: `TSPSolver`

The same file even includes the version that recovers the **route**, not just the cost — a second `nextCity[mask to u]` memo records the best successor, and `reconstructPath()` walks it back from the start:

```kotlin
class TSPSolver(private val dist: Array<IntArray>) {
    private val n = dist.size
    private val allVisited = (1 shl n) - 1
    private val memo = mutableMapOf<Pair<Int, Int>, Int>()
    private val nextCity = mutableMapOf<Pair<Int, Int>, Int>()

    fun solve() {
        val minFuel = dp(1, 0)
        val path = reconstructPath()
        println("Minimum Fuel/Time: $minFuel")
        println("Optimal Route: ${path.joinToString(" -> ")}")
    }

    private fun dp(mask: Int, u: Int): Int {
        if (mask == allVisited) return dist[u][0]

        val state = mask to u
        if (memo.containsKey(state)) return memo[state]!!

        var minCost = Int.MAX_VALUE
        var bestNext = -1
        for (v in 0 until n) {
            if ((mask and (1 shl v)) == 0) {
                val cost = dist[u][v] + dp(mask or (1 shl v), v)
                if (cost < minCost) { minCost = cost; bestNext = v }
            }
        }
        nextCity[state] = bestNext               // remember the winning successor
        return minCost.also { memo[state] = it }
    }

    private fun reconstructPath(): List<Int> {
        val path = mutableListOf(0)
        var mask = 1
        var u = 0
        while (nextCity.containsKey(mask to u)) {
            val v = nextCity[mask to u]!!
            path.add(v)
            mask = mask or (1 shl v)
            u = v
        }
        return path
    }
}
```

The cost memo and the path memo are the **same state key** — one map answers "how much?", the other "which way?" — the standard "DP with reconstruction" trick ([2.2](../ch02-dynamic-programming/minimum-edit-distance.md) style) applied to bitmask DP.

## Bottom-up vs top-down, in one table

| | [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md) Held-Karp (bottom-up) | This page (top-down) |
|---|---|---|
| Direction | fill `dp[mask][city]` by growing masks | recurse from `(1, 0)`, memoize |
| Unreachable states | table cells with no path | never visited by construction (`filter isNotVisited`) |
| Readability | table fills are subtle (submask iteration) | recursion mirrors the decision "which city next?" |
| Interview fit | "I know the canonical form" | "here's the brute force, memoized" — easier to derive live |

## Complexity

Both directions, same bounds:

$$
T(n) = O(n^2 \cdot 2^n), \qquad S(n) = O(n \cdot 2^n)
$$

## Variants & follow-ups

- **TravellingSalespersonProblemBruteforceMatrix.kt** — the pre-memo `n!` permutation check; the contrast that justifies the bitmask.
- **Shortest Path Visiting All Nodes** ([17.5](../ch17-advanced-graphs/shortest-path-visiting-all-nodes.md)) — the same `(mask, node)` state space with BFS instead of TSP's cost-minimization.
- **Interview follow-up:** "Why is the bitmask the state and not a list of visited cities?" A `Set` can't be hashed cheaply, and the order doesn't matter — only *which* cities are visited. The `Int` bitmask is the smallest possible state key, which is exactly what makes the memo's `O(n·2ⁿ)` entries feasible.
