# 17.7 Evaluate Division

> **Source:** [`src/main/kotlin/graph/EvalualteDivisions.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/EvalualteDivisions.kt)
> **Pattern:** edge-labeled graph BFS · **Core page**

## The Problem

Given `equations` like `["a","b"]` with `values` like `2.0` (meaning `a / b = 2.0`), answer `queries` of the form `["x","y"]` with `x / y`, or `-1.0` if undeterminable.

- Constraints: small graphs; values positive; answers fit in `Double`.

## Examples

```
equations = [["a","b"],["b","c"]], values = [2.0, 3.0]
queries: a/c = 6.0, b/a = 0.5, a/e = -1.0, a/a = 1.0
```

## Intuition — `a / b = 2.0` is an edge with a *multiplier* label

The equation `a / b = v` defines a directed edge `a -> b` with weight `v` **and** the reciprocal edge `b -> a` with weight `1/v`. Then a query `x / y` is: walk from `x` to `y` in this graph, **multiplying edge weights** along the way — the product is the ratio (cancellation telescopes along the path: `a/b · b/c = a/c`).

```
graph[a][b] = v;  graph[b][a] = 1/v

bfs(start, target):
    if either not in the graph: -1.0
    if start == target: 1.0
    queue of (node, product); visited set
    for (next, weight) in graph[node]: queue.add((next, product * weight))
    return the product when target is popped, else -1.0
```

**Why does the product work?** Any path `a -> x1 -> x2 -> y` multiplies to `a/x1 · x1/x2 · x2/y = a/y` — the intermediate variables cancel. The graph encodes a *consistent system* (given), so every path between two nodes yields the same product.

**Why BFS?** Shortest path in hops; the multiplier accumulates in the state `(node, product)` — the same "state carries more than the node id" move as [17.6](reorder-routes-to-make-all-paths-lead-to-city-zero.md) and the [17.0](pattern-primer.md) state-space pattern. (Union-Find with weights is the alternative — same math, different structure.)

**The special cases:** `start == target` → 1.0 (anything divided by itself); `start` or `target` unknown → -1.0; no path → -1.0. The repo handles all three explicitly.

## Approach 1 — Floyd-Warshall over the ratio graph (O(n^3))

Precompute all-pairs ratios: fine for tiny graphs, overkill for per-query BFS.

## Approach 2 — Product-accumulating BFS (the repo's version, optimal)

```kotlin
class EvaluateDivisions {
    private data class NodeState(val id: String, val product: Double)

    /**
     * @param equations pairs defining ratios
     * @param values    a / b = values[i]
     * @param queries   x / y to evaluate
     * @return          answers, -1.0 if undeterminable
     */
    fun calcEquation(equations: List<List<String>>, values: DoubleArray,
                     queries: List<List<String>>): DoubleArray {
        // Build graph: a -> {b: value}, b -> {a: 1/value}
        val graph = mutableMapOf<String, MutableMap<String, Double>>()
        equations.forEachIndexed { i, (u, v) ->
            graph.getOrPut(u) { mutableMapOf() }[v] = values[i]
            graph.getOrPut(v) { mutableMapOf() }[u] = 1.0 / values[i]
        }

        fun bfs(start: String, target: String): Double {
            if (start !in graph || target !in graph) return -1.0
            if (start == target) return 1.0

            val queue = ArrayDeque<NodeState>().apply { add(NodeState(start, 1.0)) }
            val visited = mutableSetOf(start)

            while (queue.isNotEmpty()) {
                val (curr, ratio) = queue.removeFirst()
                if (curr == target) return ratio

                graph[curr]?.forEach { (next, weight) ->
                    if (visited.add(next)) {
                        queue.add(NodeState(next, ratio * weight))
                    }
                }
            }
            return -1.0
        }

        return DoubleArray(queries.size) { i -> bfs(queries[i][0], queries[i][1]) }
    }
}
```

```java
import java.util.*;

public class EvaluateDivision {
    /**
     * @param equations pairs defining ratios
     * @param values    a / b = values[i]
     * @param queries   x / y to evaluate
     * @return          answers, -1.0 if undeterminable
     */
    public double[] calcEquation(List<List<String>> equations, double[] values,
                                 List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String u = equations.get(i).get(0), v = equations.get(i).get(1);
            graph.computeIfAbsent(u, k -> new HashMap<>()).put(v, values[i]);
            graph.computeIfAbsent(v, k -> new HashMap<>()).put(u, 1.0 / values[i]);
        }

        double[] result = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            result[i] = bfs(graph, queries.get(i).get(0), queries.get(i).get(1));
        }
        return result;
    }

    private double bfs(Map<String, Map<String, Double>> graph, String start, String target) {
        if (!graph.containsKey(start) || !graph.containsKey(target)) return -1.0;
        if (start.equals(target)) return 1.0;

        Deque<Object[]> queue = new ArrayDeque<>();      // {node, product}
        queue.add(new Object[]{start, 1.0});
        Set<String> visited = new HashSet<>();
        visited.add(start);

        while (!queue.isEmpty()) {
            Object[] state = queue.poll();
            String node = (String) state[0];
            double product = (double) state[1];
            if (node.equals(target)) return product;

            for (Map.Entry<String, Double> e : graph.get(node).entrySet()) {
                if (visited.add(e.getKey())) {
                    queue.add(new Object[]{e.getKey(), product * e.getValue()});
                }
            }
        }
        return -1.0;
    }
}
```

```cpp
#include <queue>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <vector>

class EvaluateDivision {
public:
    /**
     * @param equations pairs defining ratios
     * @param values    a / b = values[i]
     * @param queries   x / y to evaluate
     * @return          answers, -1.0 if undeterminable
     */
    std::vector<double> calcEquation(std::vector<std::vector<std::string>>& equations,
                                     std::vector<double>& values,
                                     std::vector<std::vector<std::string>>& queries) {
        std::unordered_map<std::string, std::unordered_map<std::string, double>> graph;
        for (int i = 0; i < (int)equations.size(); i++) {
            auto& u = equations[i][0], & v = equations[i][1];
            graph[u][v] = values[i];
            graph[v][u] = 1.0 / values[i];
        }

        std::vector<double> result;
        for (auto& q : queries) result.push_back(bfs(graph, q[0], q[1]));
        return result;
    }

private:
    double bfs(std::unordered_map<std::string, std::unordered_map<std::string, double>>& graph,
               const std::string& start, const std::string& target) {
        if (!graph.count(start) || !graph.count(target)) return -1.0;
        if (start == target) return 1.0;

        std::queue<std::pair<std::string, double>> q;      // {node, product}
        q.push({start, 1.0});
        std::unordered_set<std::string> visited{start};

        while (!q.empty()) {
            auto [node, product] = q.front(); q.pop();
            if (node == target) return product;

            for (auto& [next, weight] : graph[node]) {
                if (visited.insert(next).second) {
                    q.push({next, product * weight});
                }
            }
        }
        return -1.0;
    }
};
```

```python
from collections import deque

def calc_equation(equations: list[list[str]], values: list[float],
                  queries: list[list[str]]) -> list[float]:
    """
    @param equations: pairs defining ratios
    @param values:    a / b = values[i]
    @param queries:   x / y to evaluate
    @return:          answers, -1.0 if undeterminable
    """
    graph = {}
    for (u, v), val in zip(equations, values):
        graph.setdefault(u, {})[v] = val
        graph.setdefault(v, {})[u] = 1.0 / val

    def bfs(start: str, target: str) -> float:
        if start not in graph or target not in graph:
            return -1.0
        if start == target:
            return 1.0

        q = deque([(start, 1.0)])
        visited = {start}

        while q:
            node, product = q.popleft()
            if node == target:
                return product
            for nxt, weight in graph[node].items():
                if nxt not in visited:
                    visited.add(nxt)
                    q.append((nxt, product * weight))
        return -1.0

    return [bfs(u, v) for u, v in queries]
```

```rust
use std::collections::{HashMap, HashSet, VecDeque};

impl Solution {
    /// @param equations pairs defining ratios
    /// @param values    a / b = values[i]
    /// @param queries   x / y to evaluate
    /// @return          answers, -1.0 if undeterminable
    pub fn calc_equation(equations: Vec<Vec<String>>, values: Vec<f64>,
                         queries: Vec<Vec<String>>) -> Vec<f64> {
        let mut graph: HashMap<&str, HashMap<&str, f64>> = HashMap::new();
        for (i, e) in equations.iter().enumerate() {
            graph.entry(&e[0]).or_default().insert(&e[1], values[i]);
            graph.entry(&e[1]).or_default().insert(&e[0], 1.0 / values[i]);
        }

        fn bfs(graph: &HashMap<&str, HashMap<&str, f64>>, start: &str, target: &str) -> f64 {
            if !graph.contains_key(start) || !graph.contains_key(target) { return -1.0; }
            if start == target { return 1.0; }

            let mut q: VecDeque<(&str, f64)> = VecDeque::new();
            q.push_back((start, 1.0));
            let mut visited: HashSet<&str> = HashSet::new();
            visited.insert(start);

            while let Some((node, product)) = q.pop_front() {
                if node == target { return product; }
                if let Some(neighbors) = graph.get(node) {
                    for (nxt, weight) in neighbors {
                        if visited.insert(nxt) {
                            q.push_back((nxt, product * weight));
                        }
                    }
                }
            }
            -1.0
        }

        queries.iter().map(|q| bfs(&graph, &q[0], &q[1])).collect()
    }
}
```

## Dry run

**Input:** `equations = [["a","b"],["b","c"]]`, `values = [2.0, 3.0]`.

```
graph: a -> {b: 2.0}        b -> {a: 0.5, c: 3.0}        c -> {b: 1/3}

query a/c:  bfs(a, c):
  q=[(a,1.0)].  pop a -> b: push (b, 1.0*2.0=2.0).
  pop b -> target? no.  neighbors: a (visited), c: push (c, 2.0*3.0=6.0).
  pop c == target -> return 6.0 ✓
query b/a:  bfs(b, a):
  pop b -> a: push (a, 1.0*0.5=0.5).  pop a == target -> 0.5 ✓
query a/e:  e not in graph -> -1.0 ✓
query a/a:  start == target -> 1.0 ✓
```

The telescoping is visible in `a/c`: `a/b · b/c = 2.0 · 3.0 = 6.0 = a/c` — the intermediate `b` cancels in the multiplication. The reciprocal edge (`b -> a: 0.5`) handles queries in the "wrong" direction, and the three special cases cover everything else.

## Complexity

**Time.** Per query, a BFS over the ratio graph:

$$
T(Q, V, E) = O(Q \cdot (V + E))
$$

**Space.** The graph:

$$
S = O(V + E)
$$

## Variants & follow-ups

- **Weighted Union-Find** — the alternative structure: store parent + ratio-to-parent; find returns the accumulated product. Same math, $O(\alpha)$ per query after build.
- **Network Delay / longest-path** — the same edge-labeled traversal with sums instead of products.
- **Interview follow-up:** "Why is the ratio along any path the same?" The equations define a *consistent* system (the problem guarantees it), so the products telescope — `a/x · x/y = a/y` regardless of the intermediate path. That consistency is what lets BFS return the first path's product without checking alternatives.
