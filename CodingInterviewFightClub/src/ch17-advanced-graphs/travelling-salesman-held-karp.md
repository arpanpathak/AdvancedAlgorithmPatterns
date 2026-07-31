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
