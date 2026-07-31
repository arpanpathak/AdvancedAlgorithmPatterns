# 17.8 Bellman-Ford

> **Source:** [`src/main/kotlin/graph/dp/BellmanFordAlgorithm.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/dp/BellmanFordAlgorithm.kt)
> **Pattern:** V-1 relaxations + negative-cycle check · **Core page**

## The Problem

Given a weighted graph (edges may be **negative**) and a `source`, find shortest distances to all vertices — or detect a **negative-weight cycle**.

- Constraints: small-to-medium graphs; weights fit in `Int`.

## Examples

```
vertices = 4, edges = [(0,1,4), (0,2,5), (1,2,-3), (2,3,4), (3,1,-6)], source = 0
Output: a negative cycle exists (1 -> 2 -> 3 -> 1 = -3 + 4 - 6 = -5)
```

## Intuition — relax every edge V-1 times; then one more pass catches negative cycles

Dijkstra ([6.5](../ch06-graphs/cheapest-flights-with-k-stops.md)) fails with negative edges — a "shorter later" path can invalidate settled nodes. **Bellman-Ford** takes the other route: no priority queue, just **V-1 full passes of relaxation**:

$$
\text{if } dist[u] + w < dist[v] \text{ then } dist[v] = dist[u] + w
$$

**Why V-1 passes?** Any simple path has at most `V - 1` edges. After the k-th pass, every vertex's distance is correct for paths of length ≤ k (induction: the k-th pass fixes all vertices whose shortest path uses exactly k edges). After V-1 passes, all shortest paths are found — for graphs *without* negative cycles.

**The V-th pass is the cycle detector:** in a graph with a negative cycle, distances can keep improving forever. One extra pass that still finds a relaxation ⟹ a negative cycle exists — because with no negative cycle, V-1 passes would have converged.

**`Int.MAX_VALUE` guards:** relaxing from an unreachable `u` (`dist[u] == MAX_VALUE`) would overflow — the `dist[u] != Int.MAX_VALUE` check is mandatory hygiene.

## Approach 1 — Dijkstra (fails with negatives)

The [6.5](../ch06-graphs/cheapest-flights-with-k-stops.md) engine assumes non-negative weights; settled nodes can't be improved.

## Approach 2 — V-1 relaxations + cycle check (the repo's version, optimal)

```kotlin
object BellmanFordAlgorithm {
    data class Edge(val from: Int, val to: Int, val weight: Int)

    /**
     * @param vertices node count
     * @param edges    weighted edges (negative allowed)
     * @param source   start node
     * @return         (distances, hasNegativeCycle)
     */
    fun bellmanFord(vertices: Int, edges: List<Edge>, source: Int): Pair<IntArray, Boolean> {
        val dist = IntArray(vertices) { Int.MAX_VALUE }.apply { this[source] = 0 }

        // V-1 full relaxations: after pass k, all paths of length <= k are exact
        repeat(vertices - 1) {
            edges.forEach { (u, v, w) ->
                if (dist[u] != Int.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w
                }
            }
        }

        // One more pass: any relaxation now proves a negative cycle
        val hasNegativeCycle = edges.any { (u, v, w) ->
            dist[u] != Int.MAX_VALUE && dist[u] + w < dist[v]
        }

        return dist to hasNegativeCycle
    }
}
```

```java
import java.util.*;

public class BellmanFord {
    public record Edge(int from, int to, int weight) {}

    /**
     * @param vertices node count
     * @param edges    weighted edges (negative allowed)
     * @param source   start node
     * @return         (distances, hasNegativeCycle)
     */
    public Object[] bellmanFord(int vertices, List<Edge> edges, int source) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        for (int pass = 0; pass < vertices - 1; pass++) {       // V-1 relaxations
            for (Edge e : edges) {
                if (dist[e.from()] != Integer.MAX_VALUE
                        && dist[e.from()] + e.weight() < dist[e.to()]) {
                    dist[e.to()] = dist[e.from()] + e.weight();
                }
            }
        }

        boolean negativeCycle = false;                           // V-th pass
        for (Edge e : edges) {
            if (dist[e.from()] != Integer.MAX_VALUE
                    && dist[e.from()] + e.weight() < dist[e.to()]) {
                negativeCycle = true;
                break;
            }
        }
        return new Object[]{dist, negativeCycle};
    }
}
```

```cpp
#include <climits>
#include <vector>

class BellmanFord {
    struct Edge { int from, to, weight; };

public:
    /**
     * @param vertices node count
     * @param edges    weighted edges (negative allowed)
     * @param source   start node
     * @return         (distances, hasNegativeCycle)
     */
    std::pair<std::vector<int>, bool> bellmanFord(int vertices,
                                                  std::vector<Edge>& edges, int source) {
        std::vector<int> dist(vertices, INT_MAX);
        dist[source] = 0;

        for (int pass = 0; pass < vertices - 1; pass++) {       // V-1 relaxations
            for (auto& e : edges) {
                if (dist[e.from] != INT_MAX && dist[e.from] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[e.from] + e.weight;
                }
            }
        }

        bool negativeCycle = false;                              // V-th pass
        for (auto& e : edges) {
            if (dist[e.from] != INT_MAX && dist[e.from] + e.weight < dist[e.to]) {
                negativeCycle = true;
                break;
            }
        }
        return {dist, negativeCycle};
    }
};
```

```python
def bellman_ford(vertices: int, edges: list[tuple[int, int, int]], source: int):
    """
    @param vertices: node count
    @param edges:    weighted edges (negative allowed)
    @param source:   start node
    @return:         (distances, has_negative_cycle)
    """
    dist = [float("inf")] * vertices
    dist[source] = 0

    for _ in range(vertices - 1):                    # V-1 relaxations
        for u, v, w in edges:
            if dist[u] != float("inf") and dist[u] + w < dist[v]:
                dist[v] = dist[u] + w

    has_negative_cycle = any(                        # V-th pass
        dist[u] != float("inf") and dist[u] + w < dist[v]
        for u, v, w in edges
    )
    return dist, has_negative_cycle
```

```rust
impl Solution {
    // (No LeetCode container; the algorithm is shown in the Kotlin/Java/C++ blocks.)
}
```

## Dry run

**Input:** the repo's example — `vertices = 4`, edges `(0,1,4), (0,2,5), (1,2,-3), (2,3,4), (3,1,-6)`, source 0.

```
dist = [0, INF, INF, INF]

pass 1: (0,1,4): dist[1]=4.  (0,2,5): dist[2]=5.  (1,2,-3): 4-3=1 < 5 -> dist[2]=1.
        (2,3,4): 1+4=5 -> dist[3]=5.  (3,1,-6): 5-6=-1 < 4 -> dist[1]=-1.
pass 2: (1,2,-3): -1-3=-4 < 1 -> dist[2]=-4.  (2,3,4): -4+4=0 < 5 -> dist[3]=0.
        (3,1,-6): 0-6=-6 < -1 -> dist[1]=-6.
pass 3: (1,2,-3): -6-3=-9 < -4 -> dist[2]=-9.  (2,3,4): -9+4=-5 < 0 -> dist[3]=-5.
        (3,1,-6): -5-6=-11 < -6 -> dist[1]=-11.
pass 4 (the check): (1,2,-3): -11-3=-14 < -9 -> STILL IMPROVING.

Result: negative cycle detected ✓   (cycle 1->2->3->1 sums to -3+4-6 = -5 < 0)
```

The four passes tell the story: distances keep decreasing through pass 3 *and* pass 4 — the signature of a negative cycle, where no number of relaxations can converge. Without the cycle, pass 4 would find nothing to improve (the V-1 bound).

## Complexity

**Time.** V passes × E edges:

$$
T(V, E) = O(V \cdot E)
$$

**Space.** The distance array:

$$
S(V) = O(V)
$$

## Variants & follow-ups

- **Floyd-Warshall** (`graph/dp/FloydWarshallAlgorithm.kt`) — all-pairs shortest paths: the same relaxation idea over a matrix, O(V³).
- **Cheapest Flights With K Stops** ([6.5](../ch06-graphs/cheapest-flights-with-k-stops.md)) — a *bounded* version of Bellman-Ford (k+1 relaxations) — the "exactly K legs" flavor.
- **Interview follow-up:** "Why does the extra pass catch negative cycles?" After V-1 passes every *simple* shortest path is settled — paths longer than V-1 edges necessarily repeat a vertex. A V-th-pass improvement can only come from a repeated vertex cycle, and that cycle must have negative total weight (else relaxing around it couldn't improve). Detecting "still improving" ⟺ negative cycle.
