# 17.17 Floyd-Warshall

> **Source**: [`src/main/kotlin/graph/dp/FloydWarshallAlgorithm.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/dp/FloydWarshallAlgorithm.kt)
> **Pattern**: all-pairs shortest paths · **Core page**

## The Problem

The shortest path between **every pair** of vertices (weighted, possibly negative; no negative cycles).

- Constraints: n ≤ 500.

## Examples

```
Input:  graph = [[0,5,INF,10],[INF,0,3,INF],[INF,INF,0,1],[INF,INF,INF,0]]
Output: all-pairs distances ([[0,5,8,9],[INF,0,3,4],[INF,INF,0,1],[INF,INF,INF,0]])
```

## Intuition — relax through every intermediate k

`dist[i][j]` = shortest path using intermediates from {0..k} — the DP where k is the "allowed intermediate" dimension:

```kotlin
val dist = Array(n) { i -> IntArray(n) { j -> graph[i][j] } }

for (k in 0 until n) {            // k = allowed intermediate
    for (i in 0 until n) {
        for (j in 0 until n) {
            if (dist[i][k] < INF && dist[k][j] < INF) {
                dist[i][j] = minOf(dist[i][j], dist[i][k] + dist[k][j])
            }
        }
    }
}
return dist
```

**Why k as the outer loop?** The DP order matters: using k as an intermediate requires the paths through {0..k−1} already computed — k outermost makes each relaxation use only earlier-k results. The [17.x](../ch17-advanced-graphs/pattern-primer.md) all-pairs DP, O(n³).

**Why the INF guards?** `dist[i][k] + dist[k][j]` overflows if either side is unreachable — the guard skips non-paths.

## Approach 1 — Dijkstra per source (O(n² log n))

n Dijkstras: fine for sparse, O(n² log n) — but fails negative edges.

## Approach 2 — Floyd's DP (the repo's version, optimal for dense/all-pairs)

```kotlin
object FloydWarshallAlgorithm {
    const val INF = 1_000_000_000

    /**
     * @param graph weighted adjacency matrix
     * @return      all-pairs shortest distances
     */
    fun floydWarshall(graph: Array<IntArray>): Array<IntArray> {
        val n = graph.size
        val dist = Array(n) { i -> IntArray(n) { j -> graph[i][j] } }

        for (k in 0 until n) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    if (dist[i][k] < INF && dist[k][j] < INF) {
                        dist[i][j] = minOf(dist[i][j], dist[i][k] + dist[k][j])
                    }
                }
            }
        }
        return dist
    }
}
```

```java
public class FloydWarshall {
    /**
     * @param graph weighted adjacency matrix
     * @return      all-pairs shortest distances
     */
    public int[][] floydWarshall(int[][] graph) {
        int n = graph.length;
        int INF = 1_000_000_000;
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) System.arraycopy(graph[i], 0, dist[i], 0, n);

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] < INF && dist[k][j] < INF)
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
        return dist;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class FloydWarshall {
public:
    /**
     * @param graph weighted adjacency matrix
     * @return      all-pairs shortest distances
     */
    std::vector<std::vector<int>> floydWarshall(std::vector<std::vector<int>>& graph) {
        int n = graph.size();
        const int INF = 1e9;
        auto dist = graph;

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] < INF && dist[k][j] < INF)
                        dist[i][j] = std::min(dist[i][j], dist[i][k] + dist[k][j]);
        return dist;
    }
};
```

```python
def floyd_warshall(graph: list[list[int]]) -> list[list[int]]:
    """
    @param graph: weighted adjacency matrix
    @return:      all-pairs shortest distances
    """
    INF = 10**9
    n = len(graph)
    dist = [row[:] for row in graph]

    for k in range(n):
        for i in range(n):
            for j in range(n):
                if dist[i][k] < INF and dist[k][j] < INF:
                    dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])

    return dist
```

```rust
impl Solution {
    /// @param graph weighted adjacency matrix
    /// @return      all-pairs shortest distances
    pub fn floyd_warshall(graph: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        let n = graph.len();
        let inf = 1_000_000_000;
        let mut dist = graph;

        for k in 0..n {
            for i in 0..n {
                for j in 0..n {
                    if dist[i][k] < inf && dist[k][j] < inf {
                        dist[i][j] = dist[i][j].min(dist[i][k] + dist[k][j]);
                    }
                }
            }
        }
        dist
    }
}
```

## Dry run

**Input:** the example matrix.

```
dist0: [[0,5,INF,10],[INF,0,3,INF],[INF,INF,0,1],[INF,INF,INF,0]]
k=0: no changes (no path through 0 improves).
k=1: [2][0]? INF.  [0][2] = min(INF, 5+3) = 8.  [0][3] = min(10, 5+INF) = 10.  [2][3]? INF.
k=2: [0][3] = min(10, 8+1) = 9.  [1][3] = min(INF, 3+1) = 4.
k=3: nothing.
Output: [[0,5,8,9],[INF,0,3,4],[INF,INF,0,1],[INF,INF,INF,0]] ✓
```

The k-outer loop's magic: after k=2, `dist[0][3]` uses the path 0→1→2→3 (discovered incrementally as k grows). Each intermediate gets "allowed" exactly once — the DP's correctness is the ordering.

## Complexity

**Time.** Triple loop:

$$
T(n) = O(n^3)
$$

**Space.** The distance matrix:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Bellman-Ford** ([17.8](bellman-ford.md)) — the single-source negative-edge sibling.
- **Dijkstra** ([6.5](../ch06-graphs/cheapest-flights-with-k-stops.md)) — the non-negative single-source engine.
- **Interview follow-up:** "Why is k the outer loop?" Reusing k as an intermediate needs paths through {0..k−1} finalized — k outermost gives that ordering. Swapping the loops (i or j outside) breaks the DP's correctness — a classic trap.
