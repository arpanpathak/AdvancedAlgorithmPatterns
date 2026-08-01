# 6.28 Optimize Water Distribution In A Village

> **Source**: [`src/main/kotlin/graph/mst/OptimizeWaterDistributionInAVillage.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/mst/OptimizeWaterDistributionInAVillage.kt)
> **Pattern**: MST with a virtual node · **Core page**

## The Problem

Min cost to give every house water — via pipes (edges) or a well (per-house cost).

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  n = 3, wells = [1,2,2], pipes = [[1,2,1],[2,3,1]]   -> Output: 3
```

## Intuition — the wells become a virtual node 0 with edges of well-cost

The [6.6](min-cost-to-connect-all-points.md) Prim: add node 0 connected to every house with the well cost — the MST over n+1 nodes picks wells or pipes:

```kotlin
val graph = List(n + 1) { mutableListOf<Edge>() }
wells.forEachIndexed { i, cost -> graph[0].add(Edge(i + 1, cost)) }
pipes.forEach { (u, v, cost) -> graph[u].add(Edge(v, cost)); graph[v].add(Edge(u, cost)) }

// Prim's with pq
```

## Approach 1 — Prim + virtual node (the repo's version, optimal)

```kotlin
class OptimizeWaterDistributionInAVillage {
    private data class Edge(val node: Int, val cost: Int)

    /**
     * @param n     house count
     * @param wells well costs
     * @param pipes pipe edges
     * @return      min water cost
     */
    fun minCostToSupplyWater(n: Int, wells: IntArray, pipes: Array<IntArray>): Int {
        val graph = List(n + 1) { mutableListOf<Edge>() }
        val visited = BooleanArray(n + 1)
        val pq = PriorityQueue<Edge>(compareBy { it.cost })

        wells.forEachIndexed { i, cost -> graph[0].add(Edge(i + 1, cost)) }
        pipes.forEach { (u, v, cost) ->
            graph[u].add(Edge(v, cost))
            graph[v].add(Edge(u, cost))
        }

        var totalCost = 0
        pq.offer(Edge(0, 0))

        while (pq.isNotEmpty()) {
            val (node, cost) = pq.poll()
            if (visited[node]) continue

            visited[node] = true
            totalCost += cost

            for (edge in graph[node]) {
                if (!visited[edge.node]) pq.offer(edge)
            }
        }
        return totalCost
    }
}
```

```java
import java.util.*;

public class OptimizeWaterDistribution {
    private static class Edge {
        int node, cost;
        Edge(int node, int cost) { this.node = node; this.cost = cost; }
    }

    /**
     * @param n     house count
     * @param wells well costs
     * @param pipes pipe edges
     * @return      min water cost
     */
    public int minCostToSupplyWater(int n, int[] wells, int[][] pipes) {
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i <= n; i++) graph.add(new ArrayList<>());

        for (int i = 0; i < n; i++) graph.get(0).add(new Edge(i + 1, wells[i]));
        for (int[] p : pipes) {
            graph.get(p[0]).add(new Edge(p[1], p[2]));
            graph.get(p[1]).add(new Edge(p[0], p[2]));
        }

        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);
        boolean[] visited = new boolean[n + 1];
        pq.offer(new Edge(0, 0));

        int total = 0;
        while (!pq.isEmpty()) {
            Edge e = pq.poll();
            if (visited[e.node]) continue;

            visited[e.node] = true;
            total += e.cost;

            for (Edge next : graph.get(e.node)) {
                if (!visited[next.node]) pq.offer(next);
            }
        }
        return total;
    }
}
```

```cpp
#include <vector>
#include <queue>

class OptimizeWaterDistribution {
    struct Edge { int node, cost; };
    struct Cmp { bool operator()(const Edge& a, const Edge& b) { return a.cost > b.cost; } };

public:
    /**
     * @param n     house count
     * @param wells well costs
     * @param pipes pipe edges
     * @return      min water cost
     */
    int minCostToSupplyWater(int n, std::vector<int>& wells, std::vector<std::vector<int>>& pipes) {
        std::vector<std::vector<Edge>> graph(n + 1);

        for (int i = 0; i < n; i++) graph[0].push_back({i + 1, wells[i]});
        for (auto& p : pipes) {
            graph[p[0]].push_back({p[1], p[2]});
            graph[p[1]].push_back({p[0], p[2]});
        }

        std::priority_queue<Edge, std::vector<Edge>, Cmp> pq;
        std::vector<bool> visited(n + 1, false);
        pq.push({0, 0});

        int total = 0;
        while (!pq.empty()) {
            auto e = pq.top(); pq.pop();
            if (visited[e.node]) continue;

            visited[e.node] = true;
            total += e.cost;

            for (auto& next : graph[e.node]) {
                if (!visited[next.node]) pq.push(next);
            }
        }
        return total;
    }
};
```

```python
import heapq

def min_cost_to_supply_water(n: int, wells: list[int], pipes: list[list[int]]) -> int:
    """
    @param n:     house count
    @param wells: well costs
    @param pipes: pipe edges
    @return:      min water cost
    """
    graph = [[] for _ in range(n + 1)]

    for i, cost in enumerate(wells, start=1):
        graph[0].append((i, cost))
    for u, v, cost in pipes:
        graph[u].append((v, cost))
        graph[v].append((u, cost))

    pq = [(0, 0)]
    visited = [False] * (n + 1)
    total = 0

    while pq:
        cost, node = heapq.heappop(pq)
        if visited[node]:
            continue

        visited[node] = True
        total += cost

        for nxt, c in graph[node]:
            if not visited[nxt]:
                heapq.heappush(pq, (c, nxt))

    return total
```

```rust
use std::collections::BinaryHeap;
use std::cmp::Reverse;

impl Solution {
    /// @param n     house count
    /// @param wells well costs
    /// @param pipes pipe edges
    /// @return      min water cost
    pub fn min_cost_to_supply_water(n: i32, wells: Vec<i32>, pipes: Vec<Vec<i32>>) -> i32 {
        let n = n as usize;
        let mut graph = vec![Vec::new(); n + 1];

        for (i, &cost) in wells.iter().enumerate() {
            graph[0].push((i + 1, cost));
        }
        for p in &pipes {
            graph[p[0] as usize].push((p[1] as usize, p[2]));
            graph[p[1] as usize].push((p[0] as usize, p[2]));
        }

        let mut pq: BinaryHeap<Reverse<(i32, usize)>> = BinaryHeap::new();
        let mut visited = vec![false; n + 1];
        pq.push(Reverse((0, 0)));

        let mut total = 0;
        while let Some(Reverse((cost, node))) = pq.pop() {
            if visited[node] { continue; }

            visited[node] = true;
            total += cost;

            for &(nxt, c) in &graph[node] {
                if !visited[nxt] { pq.push(Reverse((c, nxt))); }
            }
        }
        total
    }
}
```

## Dry run

**Input:** the example.

```
virtual edges: 0-1 (1), 0-2 (2), 0-3 (2).  pipes: 1-2 (1), 2-3 (1).
Prim: pop 0 (0).  add 0-1 (1), 0-2 (2), 0-3 (2).
pop 1 (1).  add 1-2 (1).  pop 2 (1).  add 2-3 (1).  pop 3 (1).
total = 0+1+1+1 = 3 ✓  (pipe 1-2, pipe 2-3, well at 1)
```

## Complexity

**Time.** Prim's:

$$
T(n, e) = O((n + e) \log n)
$$

**Space.** Graph + PQ:

$$
S(n, e) = O(n + e)
$$

## Variants & follow-ups

- **Min Cost To Connect All Points** ([6.6](min-cost-to-connect-all-points.md)) — the MST engine.
- **Interview follow-up:** "Why the virtual node?" A well is just an edge from a super-source — the problem becomes a plain MST; the virtual node makes the choice well-vs-pipe automatic.
