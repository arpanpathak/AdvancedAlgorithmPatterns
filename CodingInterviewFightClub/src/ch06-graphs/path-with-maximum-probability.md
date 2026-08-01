# 6.33 Path With Maximum Probability

> **Source**: [`src/main/kotlin/probability/PathWithMaximumProbability.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/probability/PathWithMaximumProbability.kt)
> **Pattern**: max-Dijkstra · **Core page**

## The Problem

The highest-probability path from `start` to `end` (edge probabilities multiply).

- Constraints: n ≤ 10⁴.

## Examples

```
Input:  n = 3, edges = [[0,1],[1,2],[0,2]], succProb = [0.5,0.5,0.2], start = 0, end = 2
Output: 0.25   (0→1→2 = 0.5×0.5)
```

## Intuition — Dijkstra with a max-heap over probabilities

Edge weights multiply — take logs and the product becomes a sum, but the direct way: relax with `max(prob[v], prob[u] × p)` and a max-heap:

```kotlin
data class Edge(val neighbor: Int, val probability: Double)

val graph = Array(n) { mutableListOf<Edge>() }
edges.forEachIndexed { i, (u, v) ->
    graph[u].add(Edge(v, succProb[i]))
    graph[v].add(Edge(u, succProb[i]))
}

val probs = DoubleArray(n) { 0.0 }
val pq = PriorityQueue<Pair<Int, Double>>(compareByDescending { it.second })
probs[start] = 1.0
pq.add(start to 1.0)

while (pq.isNotEmpty()) {
    val (node, prob) = pq.poll()
    if (node == end) return prob

    for (edge in graph[node]) {
        val nextProb = prob * edge.probability
        if (nextProb > probs[edge.neighbor]) {
            probs[edge.neighbor] = nextProb
            pq.add(edge.neighbor to nextProb)
        }
    }
}
return 0.0
```

## Approach 1 — Max-Dijkstra (the repo's version, optimal)

```kotlin
import java.util.*

class PathWithMaximumProbability {
    /**
     * @param n            node count
     * @param edges        undirected edges
     * @param succProb     edge probabilities
     * @param start_node   start
     * @param end_node     end
     * @return             max path probability
     */
    fun maxProbability(n: Int, edges: Array<IntArray>, succProb: DoubleArray,
                       start_node: Int, end_node: Int): Double {
        val graph = Array(n) { mutableListOf<Edge>() }
        edges.forEachIndexed { i, (u, v) ->
            graph[u].add(Edge(v, succProb[i]))
            graph[v].add(Edge(u, succProb[i]))
        }

        val probs = DoubleArray(n) { 0.0 }
        val pq = PriorityQueue<Pair<Int, Double>>(compareByDescending { it.second })
        probs[start_node] = 1.0
        pq.add(start_node to 1.0)

        while (pq.isNotEmpty()) {
            val (node, prob) = pq.poll()
            if (node == end_node) return prob

            for (edge in graph[node]) {
                val nextProb = prob * edge.probability
                if (nextProb > probs[edge.neighbor]) {
                    probs[edge.neighbor] = nextProb
                    pq.add(edge.neighbor to nextProb)
                }
            }
        }
        return 0.0
    }

    private data class Edge(val neighbor: Int, val probability: Double)
}
```

```java
import java.util.*;

public class PathWithMaximumProbability {
    private static class Edge { int to; double p; Edge(int t, double p) { to = t; p = p; } }

    /**
     * @param n          node count
     * @param edges      undirected edges
     * @param succProb   edge probabilities
     * @param start_node start
     * @param end_node   end
     * @return           max path probability
     */
    public double maxProbability(int n, int[][] edges, double[] succProb,
                                 int start_node, int end_node) {
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        for (int i = 0; i < edges.length; i++) {
            graph.get(edges[i][0]).add(new Edge(edges[i][1], succProb[i]));
            graph.get(edges[i][1]).add(new Edge(edges[i][0], succProb[i]));
        }

        double[] probs = new double[n];
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> Double.compare(b.p, a.p));
        probs[start_node] = 1.0;
        pq.offer(new Edge(start_node, 1.0));

        while (!pq.isEmpty()) {
            Edge cur = pq.poll();
            if (cur.to == end_node) return cur.p;

            for (Edge e : graph.get(cur.to)) {
                double np = cur.p * e.p;
                if (np > probs[e.to]) {
                    probs[e.to] = np;
                    pq.offer(new Edge(e.to, np));
                }
            }
        }
        return 0.0;
    }
}
```

```cpp
#include <vector>
#include <queue>

class PathWithMaximumProbability {
    struct Edge { int to; double p; };
    struct Cmp { bool operator()(const Edge& a, const Edge& b) { return a.p < b.p; } };

public:
    /**
     * @param n          node count
     * @param edges      undirected edges
     * @param succProb   edge probabilities
     * @param start_node start
     * @param end_node   end
     * @return           max path probability
     */
    double maxProbability(int n, std::vector<std::vector<int>>& edges,
                          std::vector<double>& succProb, int start_node, int end_node) {
        std::vector<std::vector<Edge>> graph(n);
        for (int i = 0; i < (int)edges.size(); i++) {
            graph[edges[i][0]].push_back({edges[i][1], succProb[i]});
            graph[edges[i][1]].push_back({edges[i][0], succProb[i]});
        }

        std::vector<double> probs(n, 0.0);
        std::priority_queue<Edge, std::vector<Edge>, Cmp> pq;
        probs[start_node] = 1.0;
        pq.push({start_node, 1.0});

        while (!pq.empty()) {
            auto cur = pq.top(); pq.pop();
            if (cur.to == end_node) return cur.p;

            for (auto& e : graph[cur.to]) {
                double np = cur.p * e.p;
                if (np > probs[e.to]) {
                    probs[e.to] = np;
                    pq.push({e.to, np});
                }
            }
        }
        return 0.0;
    }
};
```

```python
import heapq

def max_probability(n: int, edges: list[list[int]], succ_prob: list[float],
                    start_node: int, end_node: int) -> float:
    """
    @param n:          node count
    @param edges:      undirected edges
    @param succ_prob:  edge probabilities
    @param start_node: start
    @param end_node:   end
    @return:           max path probability
    """
    graph = [[] for _ in range(n)]
    for (u, v), p in zip(edges, succ_prob):
        graph[u].append((v, p))
        graph[v].append((u, p))

    probs = [0.0] * n
    pq = [(-1.0, start_node)]
    probs[start_node] = 1.0

    while pq:
        neg_p, node = heapq.heappop(pq)
        p = -neg_p

        if node == end_node:
            return p

        for nxt, ep in graph[node]:
            np = p * ep
            if np > probs[nxt]:
                probs[nxt] = np
                heapq.heappush(pq, (-np, nxt))

    return 0.0
```

```rust
use std::collections::BinaryHeap;
use std::cmp::Ordering;

#[derive(PartialEq)]
struct State { p: f64, node: usize }
impl Eq for State {}
impl PartialOrd for State { fn partial_cmp(&self, o: &Self) -> Option<Ordering> { self.p.partial_cmp(&o.p) } }
impl Ord for State { fn cmp(&self, o: &Self) -> Ordering { self.p.partial_cmp(&o.p).unwrap() } }

impl Solution {
    /// @param n          node count
    /// @param edges      undirected edges
    /// @param succ_prob  edge probabilities
    /// @param start_node start
    /// @param end_node   end
    /// @return           max path probability
    pub fn max_probability(n: i32, edges: Vec<Vec<i32>>, succ_prob: Vec<f64>,
                           start_node: i32, end_node: i32) -> f64 {
        let n = n as usize;
        let mut graph = vec![Vec::new(); n];
        for (i, e) in edges.iter().enumerate() {
            graph[e[0] as usize].push((e[1] as usize, succ_prob[i]));
            graph[e[1] as usize].push((e[0] as usize, succ_prob[i]));
        }

        let mut probs = vec![0.0; n];
        let mut pq: BinaryHeap<State> = BinaryHeap::new();
        probs[start_node as usize] = 1.0;
        pq.push(State { p: 1.0, node: start_node as usize });

        while let Some(cur) = pq.pop() {
            if cur.node == end_node as usize { return cur.p; }

            for &(nxt, ep) in &graph[cur.node] {
                let np = cur.p * ep;
                if np > probs[nxt] {
                    probs[nxt] = np;
                    pq.push(State { p: np, node: nxt });
                }
            }
        }
        0.0
    }
}
```

## Dry run

**Input:** the example.

```
start 0: pq (1.0, 0).  relax: 1: 0.5, 2: 0.2.
pop (0.5, 1): relax 2: 0.5*0.5 = 0.25 > 0.2 -> 0.25.  relax 0: 0.5*0.5 = 0.25 < 1.
pop (0.25, 2): end -> return 0.25 ✓
```

## Complexity

**Time.** Max-Dijkstra:

$$
T(n, e) = O(e \log n)
$$

**Space.** Graph + PQ:

$$
S(n, e) = O(n + e)
$$

## Variants & follow-ups

- **Network Delay Time** ([6.12](network-delay-time.md)) — min-Dijkstra twin.
- **Interview follow-up:** "Why does the max-heap relax work?" Multiplying probabilities is monotone — a higher prefix probability always dominates, so the greedy pop order of Dijkstra applies unchanged.
