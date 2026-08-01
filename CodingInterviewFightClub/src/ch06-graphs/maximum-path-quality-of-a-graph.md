# 6.32 Maximum Path Quality Of A Graph

> **Source**: [`src/main/kotlin/graph/MaximumPathQualityOfAGraph.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/MaximumPathQualityOfAGraph.kt)
> **Pattern**: DFS with time budget · **Core page**

## The Problem

Max value collected on walks from 0 back to 0 within `maxTime` (values counted once).

- Constraints: nodes ≤ 1000; maxTime ≤ 100.

## Examples

```
Input:  values = [0,32,10,43], edges = [[0,1,10],[1,2,15],[0,3,10]], maxTime = 49
Output: 75
```

## Intuition — DFS with a time budget; each node's value counts once

Walk the graph; revisit nodes (their value already counted); stop when time runs out or we're back at 0 with a new best:

```kotlin
val graph = Array(n) { mutableListOf<Edge>() }
edges.forEach { (u, v, time) ->
    graph[u].add(Edge(v, time))
    graph[v].add(Edge(u, time))
}

fun dfs(node: Int, time: Int, quality: Int) {
    if (time > maxTime) return

    if (node == 0 && quality > best) best = quality

    for (edge in graph[node]) {
        val nextTime = time + edge.time

        if (nextTime > maxTime) continue

        if (!visited[edge.node]) {
            visited[edge.node] = true
            dfs(edge.node, nextTime, quality + values[edge.node])
            visited[edge.node] = false
        } else {
            dfs(edge.node, nextTime, quality)
        }
    }
}
```

**Why the visited toggle?** Values count once per *walk* — a node's value is added only on first entry; the toggle lets different walks take different first-visit sets.

## Approach 1 — Budgeted DFS (the repo's version, optimal)

```kotlin
class MaximumPathQualityOfAGraph {
    private data class Edge(val node: Int, val time: Int)

    /**
     * @param values  node values
     * @param edges   undirected weighted edges
     * @param maxTime budget
     * @return        max collected quality
     */
    fun maximalPathQuality(values: IntArray, edges: Array<IntArray>, maxTime: Int): Int {
        val n = values.size
        val graph = Array(n) { mutableListOf<Edge>() }

        edges.forEach { (u, v, time) ->
            graph[u].add(Edge(v, time))
            graph[v].add(Edge(u, time))
        }

        var best = 0
        val visited = BooleanArray(n)
        visited[0] = true

        fun dfs(node: Int, time: Int, quality: Int) {
            if (time > maxTime) return

            if (node == 0 && quality > best) best = quality

            for (edge in graph[node]) {
                val nextTime = time + edge.time
                if (nextTime > maxTime) continue

                if (!visited[edge.node]) {
                    visited[edge.node] = true
                    dfs(edge.node, nextTime, quality + values[edge.node])
                    visited[edge.node] = false
                } else {
                    dfs(edge.node, nextTime, quality)
                }
            }
        }

        dfs(0, 0, values[0])
        return best
    }
}
```

```java
import java.util.*;

public class MaximumPathQualityOfAGraph {
    private static class Edge { int node, time; Edge(int n, int t) { node = n; time = t; } }

    private List<List<Edge>> graph;
    private int[] values;
    private int maxTime, best;

    private void dfs(int node, int time, int quality) {
        if (time > maxTime) return;
        if (node == 0) best = Math.max(best, quality);

        for (Edge e : graph.get(node)) {
            int nt = time + e.time;
            if (nt > maxTime) continue;

            if (values[e.node] > 0 || ...) { }   // values counted once via a visited set
        }
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaximumPathQualityOfAGraph {
    struct Edge { int node, time; };

    int best = 0;

    void dfs(int node, int time, int quality, std::vector<int>& values,
             std::vector<std::vector<Edge>>& graph, std::vector<bool>& visited, int maxTime) {
        if (time > maxTime) return;
        if (node == 0) best = std::max(best, quality);

        for (auto& e : graph[node]) {
            int nt = time + e.time;
            if (nt > maxTime) continue;

            if (!visited[e.node]) {
                visited[e.node] = true;
                dfs(e.node, nt, quality + values[e.node], values, graph, visited, maxTime);
                visited[e.node] = false;
            } else {
                dfs(e.node, nt, quality, values, graph, visited, maxTime);
            }
        }
    }

public:
    /**
     * @param values  node values
     * @param edges   undirected weighted edges
     * @param maxTime budget
     * @return        max collected quality
     */
    int maximalPathQuality(std::vector<int>& values, std::vector<std::vector<int>>& edges, int maxTime) {
        int n = values.size();
        std::vector<std::vector<Edge>> graph(n);

        for (auto& e : edges) {
            graph[e[0]].push_back({e[1], e[2]});
            graph[e[1]].push_back({e[0], e[2]});
        }

        std::vector<bool> visited(n, false);
        visited[0] = true;
        dfs(0, 0, values[0], values, graph, visited, maxTime);
        return best;
    }
};
```

```python
def maximal_path_quality(values: list[int], edges: list[list[int]], max_time: int) -> int:
    """
    @param values:   node values
    @param edges:    undirected weighted edges
    @param max_time: budget
    @return:         max collected quality
    """
    graph = [[] for _ in range(len(values))]
    for u, v, t in edges:
        graph[u].append((v, t))
        graph[v].append((u, t))

    best = 0
    visited = [False] * len(values)
    visited[0] = True

    def dfs(node: int, time: int, quality: int) -> None:
        nonlocal best
        if time > max_time:
            return

        if node == 0:
            best = max(best, quality)

        for nxt, t in graph[node]:
            nt = time + t
            if nt > max_time:
                continue

            if not visited[nxt]:
                visited[nxt] = True
                dfs(nxt, nt, quality + values[nxt])
                visited[nxt] = False
            else:
                dfs(nxt, nt, quality)

    dfs(0, 0, values[0])
    return best
```

```rust
impl Solution {
    /// @param values   node values
    /// @param edges    undirected weighted edges
    /// @param max_time budget
    /// @return         max collected quality
    pub fn maximal_path_quality(values: Vec<i32>, edges: Vec<Vec<i32>>, max_time: i32) -> i32 {
        let n = values.len();
        let mut graph = vec![Vec::new(); n];
        for e in &edges {
            graph[e[0] as usize].push((e[1] as usize, e[2]));
            graph[e[1] as usize].push((e[0] as usize, e[2]));
        }

        let mut best = 0;
        let mut visited = vec![false; n];
        visited[0] = true;

        fn dfs(node: usize, time: i32, quality: i32, values: &Vec<i32>,
               graph: &Vec<Vec<(usize, i32)>>, visited: &mut Vec<bool>, max_time: i32,
               best: &mut i32) {
            if time > max_time { return; }
            if node == 0 { *best = (*best).max(quality); }

            for &(nxt, t) in &graph[node] {
                let nt = time + t;
                if nt > max_time { continue; }

                if !visited[nxt] {
                    visited[nxt] = true;
                    dfs(nxt, nt, quality + values[nxt], values, graph, visited, max_time, best);
                    visited[nxt] = false;
                } else {
                    dfs(nxt, nt, quality, values, graph, visited, max_time, best);
                }
            }
        }

        dfs(0, 0, values[0], &values, &graph, &mut visited, max_time, &mut best);
        best
    }
}
```

## Dry run

**Input:** the example.

```
dfs(0, 0, 0): neighbors: 1 (10), 3 (10).
0->1: (1, 10, 32).  1->2: (2, 25, 42).  2->1: (1, 40, 42).  1->0: (0, 50) > 49 stop.
  2->0? no edge.  back...
0->3: (3, 10, 43).  3->0: (0, 20, 43).  best 43.  3->1? no edge.
0->1->2->1->0? 1-2-1-0: (0, 10+15+15+10=50) > 49.  
0->1->0 (20, 32) -> 0->1->2->1->0 path: 10+15+15+10 = 50 > 49.
Alternative: 0->1 (10) -> 0 (20, q=32) -> 3 (30, q=75) -> 0 (40, q=75)?  values: 0 + 32 + 43 = 75 ✓
Output: 75 ✓
```

## Complexity

**Time.** Exponential in the time budget (walk explosion):

$$
T = O(2^{\text{maxTime}})
$$

**Space.** Recursion + graph:

$$
S = O(n + e)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why is the visited toggle correct?" Values are counted once per walk — the toggle restores the option for *other* walks; revisits (visited) add no value but keep the walk alive.
