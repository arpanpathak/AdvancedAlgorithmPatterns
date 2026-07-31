# 6.7 Strongly Connected Components

> **Source:** [`src/main/kotlin/graph/scc/Kosaraju.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/scc/Kosaraju.kt)
> **Pattern:** Kosaraju (two DFS passes) · **Core page**

## The Problem

Given a **directed** graph, partition its vertices into **strongly connected components (SCCs)** — maximal sets where every vertex can reach every other vertex *within the set*, following edge directions.

- Constraints: $1 \le V \le 10^5$; $0 \le E \le 10^5$.

## Examples

```
Input:  edges: 0->2, 2->1, 1->0, 2->3, 3->4
Output: SCCs: [0, 1, 2], [3], [4]
        (0,1,2 form a cycle, so each reaches the others; 3 and 4 are singletons)

Input:  a single cycle 0->1->2->0
Output: [0, 1, 2]    (one SCC — the whole graph)
```

## Intuition — two passes, and the transposed graph

Kosaraju is the most *memorable* SCC algorithm because its correctness is one clean idea, stated twice:

**Pass 1 — order the vertices.** Run DFS on the original graph, recording each vertex's *finish time* (when its DFS call returns — the post-order from [5.0](../ch05-trees/pattern-primer.md)). The finished vertices form a stack with a magic property: **the vertex on top (finished last) belongs to a source SCC of the condensation graph** — the condensation being the DAG you get by collapsing each SCC into a super-vertex.

**Pass 2 — walk the reversed graph in that order.** Reverse every edge (the *transpose* graph). Pop vertices off the finish-order stack and DFS from each unvisited one **on the transposed graph**. Each such DFS discovers exactly one SCC.

**Why does reversing help?** Contract SCCs into super-vertices and the graph becomes a DAG (cycles only exist *inside* components). A source SCC of the original is a **sink** of the transpose — every cross-component edge points *into* it. So a transpose-DFS started inside a source cannot escape into other components: all its transpose-neighbors are within the same SCC. And the finish order guarantees you always start from a source. Two passes, zero edge cases.

**The data:** the repo keeps *two* adjacency maps (`adj` and `revAdj`) built simultaneously in `addEdge` — no separate transpose construction pass needed.

## Approach 1 — Kosaraju (the repo's version, optimal)

```kotlin
import java.util.ArrayDeque

class Graph<T> {
    private val adj = mutableMapOf<T, MutableList<T>>()      // original graph
    private val revAdj = mutableMapOf<T, MutableList<T>>()   // transposed graph

    fun addEdge(u: T, v: T) {
        adj.getOrPut(u) { mutableListOf() }.add(v)
        revAdj.getOrPut(v) { mutableListOf() }.add(u)
    }

    /**
     * @return the strongly connected components, each as a list of vertices
     */
    fun getSCCs(): List<List<T>> {
        val visited = mutableSetOf<T>()
        val visitOrderStack = ArrayDeque<T>()

        // Pass 1: DFS on the original graph, recording finish order
        adj.keys.forEach { vertex ->
            if (vertex !in visited) fillOrder(vertex, visited, visitOrderStack)
        }

        visited.clear()
        // Pass 2: pop finish order; DFS on the transpose — each run is one SCC
        return buildList {
            while (visitOrderStack.isNotEmpty()) {
                val vertex = visitOrderStack.removeLast()
                if (vertex !in visited) {
                    add(buildList { dfsOnReversed(vertex, visited, this) })
                }
            }
        }
    }

    private fun fillOrder(vertex: T, visited: MutableSet<T>, stack: ArrayDeque<T>) {
        visited.add(vertex)
        adj[vertex]?.forEach { neighbor ->
            if (neighbor !in visited) fillOrder(neighbor, visited, stack)
        }
        stack.addLast(vertex)                               // finished -> push
    }

    private fun dfsOnReversed(vertex: T, visited: MutableSet<T>, component: MutableList<T>) {
        visited.add(vertex)
        component.add(vertex)
        revAdj[vertex]?.forEach { neighbor ->
            if (neighbor !in visited) dfsOnReversed(neighbor, visited, component)
        }
    }
}
```

```java
import java.util.*;

public class Kosaraju {
    private List<List<Integer>> adj, revAdj;

    /**
     * @param n     number of vertices (0..n-1)
     * @param edges directed edges [from, to]
     * @return      the strongly connected components
     */
    public List<List<Integer>> scc(int n, int[][] edges) {
        adj = new ArrayList<>();
        revAdj = new ArrayList<>();
        for (int i = 0; i < n; i++) { adj.add(new ArrayList<>()); revAdj.add(new ArrayList<>()); }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            revAdj.get(e[1]).add(e[0]);              // transpose, built up front
        }

        boolean[] visited = new boolean[n];
        Deque<Integer> finishOrder = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {                // Pass 1: finish order
            if (!visited[i]) fillOrder(i, visited, finishOrder);
        }

        Arrays.fill(visited, false);
        List<List<Integer>> components = new ArrayList<>();
        while (!finishOrder.isEmpty()) {             // Pass 2: walk the transpose
            int v = finishOrder.pop();
            if (!visited[v]) {
                List<Integer> comp = new ArrayList<>();
                dfsTransposed(v, visited, comp);
                components.add(comp);
            }
        }
        return components;
    }

    private void fillOrder(int v, boolean[] visited, Deque<Integer> stack) {
        visited[v] = true;
        for (int n : adj.get(v)) if (!visited[n]) fillOrder(n, visited, stack);
        stack.push(v);                               // finished -> push
    }

    private void dfsTransposed(int v, boolean[] visited, List<Integer> comp) {
        visited[v] = true;
        comp.add(v);
        for (int n : revAdj.get(v)) if (!visited[n]) dfsTransposed(n, visited, comp);
    }
}
```

```cpp
#include <functional>
#include <vector>

class Kosaraju {
public:
    /**
     * @param n     number of vertices (0..n-1)
     * @param edges directed edges [from, to]
     * @return      the strongly connected components
     */
    std::vector<std::vector<int>> scc(int n, std::vector<std::pair<int,int>>& edges) {
        std::vector<std::vector<int>> adj(n), revAdj(n);
        for (auto& [u, v] : edges) {
            adj[u].push_back(v);
            revAdj[v].push_back(u);                  // transpose, built up front
        }

        std::vector<int> finishOrder;
        std::vector<bool> visited(n, false);

        std::function<void(int)> fillOrder = [&](int v) {
            visited[v] = true;
            for (int nxt : adj[v]) if (!visited[nxt]) fillOrder(nxt);
            finishOrder.push_back(v);                // finished -> record
        };
        for (int i = 0; i < n; i++) if (!visited[i]) fillOrder(i);

        std::fill(visited.begin(), visited.end(), false);
        std::vector<std::vector<int>> components;
        std::vector<int> comp;

        std::function<void(int)> dfsTransposed = [&](int v) {
            visited[v] = true;
            comp.push_back(v);
            for (int nxt : revAdj[v]) if (!visited[nxt]) dfsTransposed(nxt);
        };
        for (int i = (int)finishOrder.size() - 1; i >= 0; i--) {   // last finished first
            comp.clear();
            if (!visited[finishOrder[i]]) {
                dfsTransposed(finishOrder[i]);
                components.push_back(comp);
            }
        }
        return components;
    }
};
```

```python
def strongly_connected_components(n: int, edges: list[tuple[int, int]]) -> list[list[int]]:
    """
    @param n:     number of vertices (0..n-1)
    @param edges: directed edges (from, to)
    @return:      the strongly connected components
    """
    adj = [[] for _ in range(n)]
    rev_adj = [[] for _ in range(n)]
    for u, v in edges:
        adj[u].append(v)
        rev_adj[v].append(u)            # transpose, built up front

    visited = [False] * n
    finish_order: list[int] = []

    def fill_order(v: int) -> None:     # Pass 1: finish order
        visited[v] = True
        for nxt in adj[v]:
            if not visited[nxt]:
                fill_order(nxt)
        finish_order.append(v)          # finished -> record

    for v in range(n):
        if not visited[v]:
            fill_order(v)

    visited = [False] * n
    components: list[list[int]] = []

    def dfs_transposed(v: int, comp: list[int]) -> None:
        visited[v] = True
        comp.append(v)
        for nxt in rev_adj[v]:
            if not visited[nxt]:
                dfs_transposed(nxt, comp)

    for v in reversed(finish_order):    # Pass 2: walk the transpose
        if not visited[v]:
            comp: list[int] = []
            dfs_transposed(v, comp)
            components.append(comp)
    return components
```

```rust
impl Solution {
    /// @param n     number of vertices (0..n-1)
    /// @param edges directed edges (from, to)
    /// @return      the strongly connected components
    pub fn strongly_connected_components(n: usize, edges: &[(usize, usize)]) -> Vec<Vec<usize>> {
        let mut adj = vec![Vec::new(); n];
        let mut rev_adj = vec![Vec::new(); n];
        for &(u, v) in edges {
            adj[u].push(v);
            rev_adj[v].push(u);             // transpose, built up front
        }

        let mut visited = vec![false; n];
        let mut finish_order = Vec::new();

        fn fill_order(
            v: usize, adj: &Vec<Vec<usize>>, visited: &mut Vec<bool>, order: &mut Vec<usize>,
        ) {
            visited[v] = true;
            for &nxt in &adj[v] {
                if !visited[nxt] {
                    fill_order(nxt, adj, visited, order);
                }
            }
            order.push(v);                  // finished -> record
        }
        for v in 0..n {
            if !visited[v] {
                fill_order(v, &adj, &mut visited, &mut finish_order);
            }
        }

        visited.fill(false);
        let mut components = Vec::new();

        fn dfs_transposed(
            v: usize, rev_adj: &Vec<Vec<usize>>, visited: &mut Vec<bool>, comp: &mut Vec<usize>,
        ) {
            visited[v] = true;
            comp.push(v);
            for &nxt in &rev_adj[v] {
                if !visited[nxt] {
                    dfs_transposed(nxt, rev_adj, visited, comp);
                }
            }
        }
        for &v in finish_order.iter().rev() {   // last finished first
            if !visited[v] {
                let mut comp = Vec::new();
                dfs_transposed(v, &rev_adj, &mut visited, &mut comp);
                components.push(comp);
            }
        }
        components
    }
}
```

### Approach 2 — Tarjan's SCC (single pass, no transpose)

The notes' Tarjan alternative finds every SCC in **one DFS** — no reversed graph. Each node gets a discovery `id` and a `lowLink` (the smallest id reachable from its subtree); when `lowLink == id`, the node is the root of an SCC, and the recursion stack above it is popped off as that component:

```kotlin
class Graph<T> {
    private val graph = mutableMapOf<T, MutableList<T>>()

    fun addEdge(from: T, to: T) {
        graph.getOrPut(from) { mutableListOf() }.add(to)
    }

    fun findSCC(): List<List<T>> {
        val sccs = mutableListOf<List<T>>()
        val ids = mutableMapOf<T, Int>()        // discovery time
        val lowLinks = mutableMapOf<T, Int>()   // lowest reachable id
        val stack = ArrayDeque<T>()             // DFS recursion stack
        val inStack = mutableSetOf<T>()
        var id = 0

        fun tarjanDfs(node: T) {
            ids[node] = id
            lowLinks[node] = id
            id++
            stack.addLast(node)
            inStack.add(node)

            graph[node]?.forEach { neighbor ->
                when {
                    neighbor !in ids -> {        // tree edge
                        tarjanDfs(neighbor)
                        lowLinks[node] = minOf(lowLinks[node]!!, lowLinks[neighbor]!!)
                    }
                    neighbor in inStack -> {     // back edge: forms a cycle
                        lowLinks[node] = minOf(lowLinks[node]!!, ids[neighbor]!!)
                    }
                }
            }

            if (lowLinks[node] == ids[node]) {   // root of an SCC: pop the stack
                val scc = mutableListOf<T>()
                var current: T
                do {
                    current = stack.removeLast()
                    inStack.remove(current)
                    scc.add(current)
                } while (current != node)
                sccs.add(scc)
            }
        }

        graph.keys.forEach { if (it !in ids) tarjanDfs(it) }
        return sccs
    }
}
```

The `lowLink` is the [17.10](../ch17-advanced-graphs/critical-connections-in-a-network.md) bridge logic's cousin — where the bridge test compares parent vs child `low`, Tarjan-SCC compares a node's `lowLink` to its own discovery `id` and pops a whole component when they're equal. Kosaraju ([Approach 1](#approach-1--kosaraju-the-repos-version-optimal)) needs two passes but no recursion bookkeeping; Tarjan needs one pass but a live stack.

## Dry run

**Input:** the repo's test — edges `0->2, 2->1, 1->0, 2->3, 3->4`.

```
Transpose (rev) edges: 2->0, 1->2, 0->1, 3->2, 4->3
rev-neighbors: rev[0]=[1], rev[1]=[2], rev[2]=[0], rev[3]=[2], rev[4]=[3]

Pass 1 (original graph, push on finish):
  fillOrder(0): 0 -> 2 -> 1 (1's only neighbor 0 is visited -> finish 1, push 1)
                          -> 3 -> 4 (finish 4, push 4; finish 3, push 3)
                     finish 2, push 2
                finish 0, push 0
  finishOrder (bottom->top): [1, 4, 3, 2, 0]      pop order: 0, 2, 3, 4, 1

Pass 2 (transpose, pop 0 first — 0 is in the source SCC {0,1,2} of the original):
  pop 0: dfsTransposed(0): rev[0]=[1] -> visit 1: rev[1]=[2] -> visit 2: rev[2]=[0] visited.
         component = {0, 1, 2}          ✓ contained!  (rev[2]=[0] only — no escape edge)
  pop 2, 1: already visited -> skip
  pop 3: dfsTransposed(3): rev[3]=[2] visited.  component = {3}     ✓
  pop 4: dfsTransposed(4): rev[4]=[3] visited.  component = {4}     ✓
SCCs: [0, 1, 2], [3], [4] ✓
```

Watch the escape-proofing: the original has edge `2 -> 3` *leaving* the {0,1,2} component. Its transpose is `3 -> 2`, which points *into* the component — so when pass 2 starts inside {0,1,2}, there is no transpose edge leading out. That's the source-vs-sink argument, visible in one line (`rev[2]=[0]`).

## Complexity

**Time.** Two full traversals (plus $O(E)$ transpose construction — free here, built during `addEdge`):

$$
T(V, E) = O(V + E)
$$

**Space.** Both adjacency lists plus the finish stack:

$$
S(V, E) = O(V + E)
$$

## Variants & follow-ups

- **Tarjan's SCC** — a single DFS pass with `low[]` link values; same $O(V+E)$ but no transpose. The "can you do it without the reversed graph?" interview follow-up.
- **Condensation graph** — contract SCCs into super-vertices: the result is a DAG. Feed that DAG to [Kahn's algorithm](course-schedule-ii.md) for "minimum edges to make all nodes reachable" (link sources to sinks), 2-SAT, and cycle-free dynamic programming over components.
- **Course prerequisites with groups** (`src/main/kotlin/graph/`) — SCCs let you treat a strongly-connected prerequisite group as one unit.
- **Interview follow-up:** "Why must pass 2 use the transpose *and* the reverse finish order?" Either one alone fails: transpose-DFS in arbitrary order can leak between components; finish order on the original graph can't *walk* components at all (edges point the wrong way). The combination is what makes each transpose-DFS land exactly on one SCC.
