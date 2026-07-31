# 17.10 Critical Connections In A Network (Tarjan Bridges)

> **Source:** [`src/main/kotlin/graph/articulation_point/CriticalConnectionsInANetwork.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/articulation_point/CriticalConnectionsInANetwork.kt)
> **Pattern:** Tarjan's bridge-finding DFS · **Core page**

## The Problem

Given `n` servers and undirected `connections`, return the **critical connections** — edges whose removal disconnects the network (bridges).

- Constraints: $2 \le n \le 10^5$; the graph is connected.

## Examples

```
Input:  n = 4, connections = [[0,1],[1,2],[2,0],[1,3]]
Output: [[1,3]]   (the only edge whose removal splits the network)
```

## Intuition — an edge is a bridge iff no *back edge* from its subtree reaches above it

The classic **Tarjan bridge test**: during a DFS, give each node a discovery `label`. Define `low[u]` = the lowest label reachable from `u`'s subtree via tree edges *plus at most one back edge*. Then:

> Edge `(u, v)` (tree edge, `u` the parent) is a bridge ⟺ `low[v] > label[u]` — the child's subtree has no way back above `u`.

```
dfs(node, parent):
    label[node] = low[node] = depth++
    for neighbor in graph[node]:
        if neighbor == parent: continue
        if not visited[neighbor]:
            dfs(neighbor, node)
            if label[node] < low[neighbor]: result += (node, neighbor)   # bridge!
            low[node] = min(low[node], low[neighbor])   # subtree can reach up
        else:
            low[node] = min(low[node], label[neighbor]) # back edge: reach up
```

**Why is `low[v] > label[u]` exactly "no way around"?** If `v`'s subtree can reach any node *at or above* `u` (a back edge to `u` or an ancestor), then `low[v] <= label[u]` — the edge `(u,v)` is bypassable. Only when the subtree is fully confined below `u` (`low[v] > label[u]`) does `(u,v)` become the sole connection — a bridge. This is the [6.7](../ch06-graphs/strongly-connected-components.md) Tarjan idea (the same `low`-link bookkeeping) applied to bridges instead of SCCs.

**Why `label[neighbor]` (not `low[neighbor]`) on the back edge?** `low` may be polluted by cycles already merged; the discovery label is the honest "how far up does this edge reach." Using `low[neighbor]` would understate bridges — the classic Tarjan gotcha.

## Approach 1 — Remove each edge, BFS connectivity (O(E·V))

Delete an edge and check if the graph stays connected: correct, quadratic-ish on dense graphs.

## Approach 2 — Tarjan's single DFS (the repo's version, optimal)

```kotlin
class CriticalConnectionsInANetwork {
    private lateinit var G: Array<MutableList<Int>>
    private lateinit var result: MutableList<List<Int>>
    private var depth = 0
    private lateinit var label: IntArray       // discovery time
    private lateinit var low: IntArray         // lowest label reachable from the subtree
    private lateinit var visited: BooleanArray

    /**
     * @param n           number of servers
     * @param connections undirected edges
     * @return            all bridges (critical connections)
     */
    fun criticalConnections(n: Int, connections: List<List<Int>>): List<List<Int>> {
        G = Array(n) { mutableListOf() }
        result = mutableListOf()
        depth = 0
        label = IntArray(n)
        low = IntArray(n)
        visited = BooleanArray(n)

        for (edge in connections) {
            G[edge[0]].add(edge[1])
            G[edge[1]].add(edge[0])
        }

        dfs(0, -1)
        return result
    }

    private fun dfs(node: Int, parent: Int) {
        visited[node] = true
        label[node] = depth
        low[node] = depth
        depth++

        for (neighbour in G[node]) {
            if (neighbour == parent) continue

            if (!visited[neighbour]) {
                dfs(neighbour, node)

                // Subtree cannot reach above node: (node, neighbour) is a bridge
                if (label[node] < low[neighbour]) {
                    result.add(listOf(node, neighbour))
                }
                low[node] = minOf(low[node], low[neighbour])   // absorb subtree reach
            } else {
                low[node] = minOf(low[node], label[neighbour]) // back edge reach
            }
        }
    }
}
```

```java
import java.util.*;

public class CriticalConnectionsInANetwork {
    private List<List<Integer>> graph;
    private List<List<Integer>> result;
    private int[] label, low;
    private boolean[] visited;
    private int depth;

    /**
     * @param n           number of servers
     * @param connections undirected edges
     * @return            all bridges (critical connections)
     */
    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (List<Integer> e : connections) {
            graph.get(e.get(0)).add(e.get(1));
            graph.get(e.get(1)).add(e.get(0));
        }

        result = new ArrayList<>();
        label = new int[n]; low = new int[n];
        visited = new boolean[n];
        depth = 0;

        dfs(0, -1);
        return result;
    }

    private void dfs(int node, int parent) {
        visited[node] = true;
        label[node] = low[node] = depth++;

        for (int next : graph.get(node)) {
            if (next == parent) continue;

            if (!visited[next]) {
                dfs(next, node);

                if (label[node] < low[next]) {             // bridge!
                    result.add(List.of(node, next));
                }
                low[node] = Math.min(low[node], low[next]);
            } else {
                low[node] = Math.min(low[node], label[next]);   // back edge
            }
        }
    }
}
```

```cpp
#include <algorithm>
#include <vector>

class CriticalConnectionsInANetwork {
    std::vector<std::vector<int>> graph;
    std::vector<std::vector<int>> result;
    std::vector<int> label, low;
    std::vector<bool> visited;
    int depth = 0;

    void dfs(int node, int parent) {
        visited[node] = true;
        label[node] = low[node] = depth++;

        for (int next : graph[node]) {
            if (next == parent) continue;

            if (!visited[next]) {
                dfs(next, node);

                if (label[node] < low[next]) {             // bridge!
                    result.push_back({node, next});
                }
                low[node] = std::min(low[node], low[next]);
            } else {
                low[node] = std::min(low[node], label[next]);   // back edge
            }
        }
    }

public:
    /**
     * @param n           number of servers
     * @param connections undirected edges
     * @return            all bridges (critical connections)
     */
    std::vector<std::vector<int>> criticalConnections(int n,
                                                      std::vector<std::vector<int>>& connections) {
        graph.assign(n, {});
        for (auto& e : connections) {
            graph[e[0]].push_back(e[1]);
            graph[e[1]].push_back(e[0]);
        }
        label.assign(n, 0); low.assign(n, 0);
        visited.assign(n, false);

        dfs(0, -1);
        return result;
    }
};
```

```python
def critical_connections(n: int, connections: list[list[int]]) -> list[list[int]]:
    """
    @param n:           number of servers
    @param connections: undirected edges
    @return:            all bridges (critical connections)
    """
    graph = [[] for _ in range(n)]
    for u, v in connections:
        graph[u].append(v)
        graph[v].append(u)

    label = [0] * n
    low = [0] * n
    visited = [False] * n
    result = []
    depth = 0

    def dfs(node: int, parent: int) -> None:
        nonlocal depth
        visited[node] = True
        label[node] = low[node] = depth
        depth += 1

        for nxt in graph[node]:
            if nxt == parent:
                continue

            if not visited[nxt]:
                dfs(nxt, node)

                if label[node] < low[nxt]:           # subtree can't reach above: bridge
                    result.append([node, nxt])
                low[node] = min(low[node], low[nxt])
            else:
                low[node] = min(low[node], label[nxt])   # back edge reach

    dfs(0, -1)
    return result
```

```rust
impl Solution {
    /// @param n           number of servers
    /// @param connections undirected edges
    /// @return            all bridges (critical connections)
    pub fn critical_connections(n: i32, connections: Vec<Vec<i32>>) -> Vec<Vec<i32>> {
        let n = n as usize;
        let mut graph = vec![Vec::new(); n];
        for c in &connections {
            graph[c[0] as usize].push(c[1] as usize);
            graph[c[1] as usize].push(c[0] as usize);
        }

        let (mut label, mut low) = (vec![0usize; n], vec![0usize; n]);
        let mut visited = vec![false; n];
        let mut depth = 0usize;
        let mut result: Vec<Vec<i32>> = Vec::new();

        fn dfs(node: usize, parent: usize, graph: &Vec<Vec<usize>>, label: &mut Vec<usize>,
               low: &mut Vec<usize>, visited: &mut Vec<bool>, depth: &mut usize,
               result: &mut Vec<Vec<i32>>) {
            visited[node] = true;
            label[node] = *depth;
            low[node] = *depth;
            *depth += 1;

            for &nxt in &graph[node] {
                if nxt == parent { continue; }

                if !visited[nxt] {
                    dfs(nxt, node, graph, label, low, visited, depth, result);

                    if label[node] < low[nxt] {        // subtree can't reach above: bridge
                        result.push(vec![node as i32, nxt as i32]);
                    }
                    low[node] = low[node].min(low[nxt]);
                } else {
                    low[node] = low[node].min(label[nxt]);   // back edge reach
                }
            }
        }

        dfs(0, usize::MAX, &graph, &mut label, &mut low, &mut visited, &mut depth, &mut result);
        result
    }
}
```

### 5. `CriticalConnectionsInANetworkShortCode.kt` — Tarjan bridges, compressed

[17.10](../ch17-advanced-graphs/critical-connections-in-a-network.md) documents the full class; this file compresses the bridge DFS into the smallest faithful form:

```kotlin
// sketch of the short-code shape (CriticalConnectionsInANetworkShortCode.kt)
// label/low arrays + one dfs() that emits a bridge when label[node] < low[neighbor]
// — the same algorithm as 17.10, with the class scaffolding stripped to the essentials
```

**What's cool:** it proves the algorithm has ~15 essential lines. When an interviewer asks "can you write it tighter?" — this file is the answer: no result-list as a field, no explicit `depth` class member, the recursion carries everything.


## Dry run

**Input:** `n = 4`, `connections = [[0,1],[1,2],[2,0],[1,3]]`.

```
dfs(0): label[0]=low[0]=0.  neighbors: 1.
  dfs(1): label[1]=low[1]=1.  neighbors: 0 (parent, skip), 2, 3.
    dfs(2): label[2]=low[2]=2.  neighbors: 1 (parent, skip), 0.
      0 is visited (back edge) -> low[2] = min(2, label[0]=0) = 0.
    back in dfs(1): low[1] = min(1, low[2]=0) = 0.  label[1]=1 < low[2]=0? NO -> not a bridge.
    dfs(3): label[3]=low[3]=3.  neighbor 1 (parent, skip).
    back in dfs(1): label[1]=1 < low[3]=3? YES -> bridge [1,3].  low[1]=min(0,3)=0.
  back in dfs(0): label[0]=0 < low[1]=0? NO -> not a bridge.

Output: [[1,3]] ✓
```

The `low` propagation is the whole story: node 2's back edge to 0 drags `low[2]` down to 0, and that 0 flows up through 1 — so the cycle edges (`0-1`, `1-2`, `2-0`) all fail the `label < low` test. Only node 3's subtree is confined (`low[3] = 3 > label[1] = 1`), marking `1-3` as the single point of failure.

## Complexity

**Time.** One DFS pass:

$$
T(V, E) = O(V + E)
$$

**Space.** Label/low/visited arrays + recursion:

$$
S(V) = O(V)
$$

## Variants & follow-ups

- **Articulation Points** (`graph/articulation_point/FindArticulationPoints.kt`) — the same `low`-link machinery for *vertices* instead of edges (root rule + child rule differ slightly).
- **Strongly Connected Components** ([6.7](../ch06-graphs/strongly-connected-components.md)) — Tarjan's other use of `low`-links, for directed graphs.
- **Interview follow-up:** "Why `label[neighbor]` on a back edge instead of `low[neighbor]`?" `low[neighbor]` may already include cycles merged from other branches — it overstates how far up the back edge reaches, understating bridges. The discovery `label` is the honest "this edge connects to node X at depth label[X]"; using it keeps the bridge test exact.
