# 17.4 Travelling Salesman (Held-Karp)

> **Source:** [`src/main/kotlin/graph/tsp/TSPHelpKarp.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/tsp/TSPHelpKarp.kt)
> **Pattern:** bitmask DP · **Core page**

## The Problem

Given a `distanceMatrix` between `cityCount` cities, find the minimum-cost **Hamiltonian cycle**: visit every city exactly once and return to the start.

- Constraints: small `n` (the DP is $O(n^2 2^n)$); distances non-negative.

## Examples

```
distanceMatrix (3 cities):
  0->1: 10, 0->2: 15, 1->0: 10, 1->2: 35, 2->0: 15, 2->1: 35
Optimal tour: 0 -> 1 -> 2 -> 0 = 10 + 35 + 15 = 60
```

## Intuition — there are $n!$ tours but only $2^n$ subsets

The brute force tries every ordering ($n!$). **Held-Karp** observes that two partial tours *ending at the same city with the same visited set* are indistinguishable for the future — only the cheapest one matters. The state:

$$
\text{cost}[\text{mask}][c] = \text{min cost to have visited exactly the cities in mask, ending at } c
$$

Transitions: from `cost[before][p]`, visit an unvisited city `c`:

$$
\text{cost}[\text{before} \cup \{c\}][c] = \min(\dots,\; \text{cost}[\text{before}][p] + d[p][c])
$$

**The mask-as-set** is the [16.0](../ch16-bit-manipulation/pattern-primer.md) bitmask: `1 shl city` marks membership; `mask xor (1 shl c)` = "the set before adding c". The repo's loops: for each mask, for each current city, for each previous city — filling `cost[mask][current]` from `cost[mask-without-current][previous]`.

**Why does the subset structure beat the permutation structure?** Permutations: $n!$. Subsets-with-an-endpoint: $n \cdot 2^n$ states, each with $n$ transitions → $O(n^2 2^n)$. The reduction is the whole point — memoization over "what's visited + where we are" collapses the factorial.

**The return leg:** after visiting all cities, add the distance from the final city back to `startCity` — the repo's final loop minimizes `cost[allVisited][final] + d[final][start]`. The tour closes the cycle.

## Approach 1 — Brute force all permutations (O(n!))

Enumerate every ordering: correct, dies at $n \ge 12$.

## Approach 2 — Held-Karp bitmask DP (the repo's version, optimal)

```kotlin
class TSPHeldKarpSolver(private val distanceMatrix: Array<IntArray>, private val cityCount: Int) {
    private val INF = 1_000_000_000

    /**
     * @param startCity where the tour starts and ends
     * @return          min tour cost (and route) visiting every city once
     */
    fun solve(startCity: Int = 0): TSPResult {
        if (cityCount < 2 || startCity !in 0 until cityCount) {
            return TSPResult(0, listOf(startCity))
        }

        val totalCityCombinations = 1 shl cityCount

        // cost[visitedCities][currentCity] = min cost to reach currentCity having visited these cities
        val cost = Array(totalCityCombinations) { IntArray(cityCount) { INF } }
        val cameFrom = Array(totalCityCombinations) { IntArray(cityCount) { -1 } }

        cost[1 shl startCity][startCity] = 0        // start: only startCity visited

        for (visitedSoFar in 1 until totalCityCombinations) {
            if (visitedSoFar and (1 shl startCity) == 0) continue   // must include the start

            for (currentCity in 0 until cityCount) {
                if (visitedSoFar and (1 shl currentCity) == 0) continue   // current must be visited

                val visitedBeforeThisCity = visitedSoFar xor (1 shl currentCity)   // set minus current

                for (previousCity in 0 until cityCount) {
                    if (visitedBeforeThisCity and (1 shl previousCity) == 0) continue

                    val totalCost = cost[visitedBeforeThisCity][previousCity] +
                            distanceMatrix[previousCity][currentCity]

                    if (totalCost < cost[visitedSoFar][currentCity]) {
                        cost[visitedSoFar][currentCity] = totalCost
                        cameFrom[visitedSoFar][currentCity] = previousCity
                    }
                }
            }
        }

        // Close the cycle: min over ending city of cost + return-to-start
        val allCitiesVisited = totalCityCombinations - 1
        var minTourCost = INF
        var lastCityBeforeReturn = -1

        for (finalStop in 0 until cityCount) {
            if (finalStop == startCity) continue
            val completeTourCost = cost[allCitiesVisited][finalStop] + distanceMatrix[finalStop][startCity]
            if (completeTourCost < minTourCost) {
                minTourCost = completeTourCost
                lastCityBeforeReturn = finalStop
            }
        }
        // ... (route reconstruction walks cameFrom backward)
        return TSPResult(minTourCost, reconstructRoute(cameFrom, startCity, lastCityBeforeReturn))
    }
}
```

```java
public class TspHeldKarp {
    private static final int INF = 1_000_000_000;

    /**
     * @param dist distance matrix
     * @param start starting city
     * @return     min tour cost visiting every city once and returning to start
     */
    public int solve(int[][] dist, int start) {
        int n = dist.length;
        int size = 1 << n;
        int[][] cost = new int[size][n];
        for (int[] row : cost) Arrays.fill(row, INF);

        cost[1 << start][start] = 0;                 // start state

        for (int mask = 1; mask < size; mask++) {
            if ((mask & (1 << start)) == 0) continue;
            for (int cur = 0; cur < n; cur++) {
                if ((mask & (1 << cur)) == 0) continue;
                int before = mask ^ (1 << cur);      // set minus current
                for (int prev = 0; prev < n; prev++) {
                    if ((before & (1 << prev)) == 0) continue;
                    cost[mask][cur] = Math.min(cost[mask][cur],
                            cost[before][prev] + dist[prev][cur]);
                }
            }
        }

        int full = size - 1;
        int best = INF;
        for (int last = 0; last < n; last++) {
            if (last == start) continue;
            best = Math.min(best, cost[full][last] + dist[last][start]);   // close the cycle
        }
        return best;
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class TspHeldKarp {
    static constexpr int INF = 1'000'000'000;

public:
    /**
     * @param dist  distance matrix
     * @param start starting city
     * @return      min tour cost visiting every city once and returning to start
     */
    int solve(std::vector<std::vector<int>>& dist, int start) {
        int n = dist.size();
        int size = 1 << n;
        std::vector<std::vector<int>> cost(size, std::vector<int>(n, INF));

        cost[1 << start][start] = 0;                 // start state

        for (int mask = 1; mask < size; mask++) {
            if ((mask & (1 << start)) == 0) continue;
            for (int cur = 0; cur < n; cur++) {
                if ((mask & (1 << cur)) == 0) continue;
                int before = mask ^ (1 << cur);      // set minus current
                for (int prev = 0; prev < n; prev++) {
                    if ((before & (1 << prev)) == 0) continue;
                    cost[mask][cur] = std::min(cost[mask][cur],
                            cost[before][prev] + dist[prev][cur]);
                }
            }
        }

        int full = size - 1;
        int best = INF;
        for (int last = 0; last < n; last++) {
            if (last == start) continue;
            best = std::min(best, cost[full][last] + dist[last][start]);   // close the cycle
        }
        return best;
    }
};
```

```python
def tsp_held_karp(dist: list[list[int]], start: int = 0) -> int:
    """
    @param dist:  distance matrix
    @param start: starting city
    @return:      min tour cost visiting every city once and returning to start
    """
    n = len(dist)
    INF = float("inf")
    size = 1 << n
    cost = [[INF] * n for _ in range(size)]

    cost[1 << start][start] = 0              # start state

    for mask in range(1, size):
        if not (mask & (1 << start)):
            continue
        for cur in range(n):
            if not (mask & (1 << cur)):
                continue
            before = mask ^ (1 << cur)       # set minus current
            for prev in range(n):
                if not (before & (1 << prev)):
                    continue
                cost[mask][cur] = min(cost[mask][cur],
                                      cost[before][prev] + dist[prev][cur])

    full = size - 1
    return min(cost[full][last] + dist[last][start]
               for last in range(n) if last != start)      # close the cycle
```

```rust
impl Solution {
    /// @param dist  distance matrix
    /// @param start starting city
    /// @return      min tour cost visiting every city once and returning to start
    pub fn tsp_held_karp(dist: Vec<Vec<i32>>, start: usize) -> i32 {
        let n = dist.len();
        let size = 1usize << n;
        let inf = i32::MAX / 2;
        let mut cost = vec![vec![inf; n]; size];

        cost[1 << start][start] = 0;                    // start state

        for mask in 1..size {
            if mask & (1 << start) == 0 { continue; }
            for cur in 0..n {
                if mask & (1 << cur) == 0 { continue; }
                let before = mask ^ (1 << cur);         // set minus current
                for prev in 0..n {
                    if before & (1 << prev) == 0 { continue; }
                    cost[mask][cur] = cost[mask][cur].min(cost[before][prev] + dist[prev][cur]);
                }
            }
        }

        let full = size - 1;
        (0..n).filter(|&l| l != start)
              .map(|l| cost[full][l] + dist[l][start])  // close the cycle
              .min()
              .unwrap()
    }
}
```

> **Sources:** [`src/main/kotlin/graph/tsp/TravellingSalesPersonTopDownDP.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/tsp/TravellingSalesPersonTopDownDP.kt) · `TravellingSalesmanRecursiveDP.kt` · `TravellingSalesPersonTopDownDP.kt` · `TravellingSalespersonProblemBruteforceMatrix.kt` · `TSPHelpKarp.kt`
> **Pattern:** variant gallery — top-down vs [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md)'s bottom-up Held-Karp

### The problem (recap)

The TSP: visit every city exactly once and return home, minimizing total distance. [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md) documents the **bottom-up Held-Karp** table. The repo also ships a **top-down functional** family — and this one is a joy to read: the whole DP is a single expression.

### The star: `TravellingSalesPersonTopDownDP.kt`

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

### The sibling: `TravellingSalesmanRecursiveDP.kt`

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

### The path-reconstructing bonus: `TSPSolver`

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

### Bottom-up vs top-down, in one table

| | [17.4](../ch17-advanced-graphs/travelling-salesman-held-karp.md) Held-Karp (bottom-up) | This page (top-down) |
|---|---|---|
| Direction | fill `dp[mask][city]` by growing masks | recurse from `(1, 0)`, memoize |
| Unreachable states | table cells with no path | never visited by construction (`filter isNotVisited`) |
| Readability | table fills are subtle (submask iteration) | recursion mirrors the decision "which city next?" |
| Interview fit | "I know the canonical form" | "here's the brute force, memoized" — easier to derive live |


## Dry run

**Input:** 3 cities, distances `0-1:10, 1-2:35, 2-0:15` (symmetric triangle), start 0.

```
cost[mask][city], INF = large
cost[001][0] = 0                                  (only city 0 visited, at 0)

mask=011 {0,1}:
  cur=1: before=010 {1}? no wait: before = 011 ^ 010 = 001 {0}.
    prev=0 (in {0}): cost[011][1] = cost[001][0] + d[0][1] = 0 + 10 = 10.
mask=101 {0,2}:
  cur=2: before=001 {0}: prev=0: cost[101][2] = 0 + d[0][2] = 15.
mask=111 {0,1,2}:
  cur=1: before=101 {0,2}: prev=0: cost[101][0]? INF.  prev=2: cost[101][2] + d[2][1] = 15+35 = 50.
  cur=2: before=011 {0,1}: prev=1: cost[011][1] + d[1][2] = 10+35 = 45.

close the cycle: full=111.
  end at 1: cost[111][1] + d[1][0] = 50 + 10 = 60.
  end at 2: cost[111][2] + d[2][0] = 45 + 15 = 60.
Output: 60 ✓   (0 -> 1 -> 2 -> 0, or 0 -> 2 -> 1 -> 0)
```

The DP's shape in miniature: every mask is filled only from `mask-minus-one-city`, and only the *cheapest* way to reach each `(mask, city)` survives — the other routes to the same state are provably dominated. Two different tours collapsing to the same state is exactly the factorial→subset compression.

## Complexity

**Time.** $n \cdot 2^n$ states × $n$ transitions:

$$
T(n) = O(n^2 \cdot 2^n)
$$

**Space.** The cost table:

$$
S(n) = O(n \cdot 2^n)
$$

## Variants & follow-ups

- **Shortest Path Visiting All Nodes** ([17.5](shortest-path-visiting-all-nodes.md)) — the same mask-as-state idea, but *unweighted BFS* over states instead of DP.
- **TSP variants** (`src/main/kotlin/graph/tsp/`) — brute-force matrix, recursive DP, and top-down versions of the same problem; `TravellingSalesmanRecursiveDP.kt` is the memoized DFS flavor.
- **Interview follow-up:** "Why do only subsets matter, not orders?" A partial tour's future depends only on (visited set, current city) — the *order* of the visited cities is irrelevant to what comes next, so all orders with the same (mask, end) are collapsed to their minimum. That's the entire reduction: $n!$ orders → $n \cdot 2^n$ states.
