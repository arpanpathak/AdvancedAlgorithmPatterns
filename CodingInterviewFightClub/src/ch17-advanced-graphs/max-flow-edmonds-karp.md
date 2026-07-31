# 17.1 Max Flow (Edmonds-Karp)

> **Source:** [`src/main/kotlin/graph/flow_network/MaxFlowEdmondsKarp.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/flow_network/MaxFlowEdmondsKarp.kt)
> **Pattern:** BFS augmenting paths · **Core page**

## The Problem

Given a directed graph with edge **capacities**, a `source` and a `sink`, return the **maximum flow** from source to sink — the largest amount that can be pushed through the network while respecting every edge's capacity.

- Constraints: small-to-medium graphs (the matrix version); capacities positive.

## Examples

```
graph (capacity matrix), source = 0, sink = 3:
  0 -> 1: 3,  0 -> 2: 2,  1 -> 2: 1,  1 -> 3: 2,  2 -> 3: 3
Max flow = 5   (0-1-3 pushes 2, 0-2-3 pushes 2, 0-1-2-3 pushes 1)
```

## Intuition — keep finding a path that can still carry flow, then push it

The **residual graph** encodes what's still possible: `residual[u][v]` = unused capacity forward plus the ability to *undo* flow (`residual[v][u]` grows when flow is pushed `u -> v`). The algorithm:

1. **Find an augmenting path** (BFS, smallest hop count — that's the Edmonds-Karp specialization) from source to sink through edges with positive residual capacity.
2. **Compute the bottleneck** — the minimum residual capacity along the path.
3. **Push it:** subtract the bottleneck on forward edges, *add* it on the reverse edges (the undo).
4. Repeat until BFS finds no path. The sum of bottlenecks is the max flow.

**Why are the reverse edges the whole idea?** Without them, the first greedy choice could block a better routing. The reverse edge lets later augmenting paths *cancel* earlier flow — the "undo" that makes the greedy repeated push optimal. This is the [17.0](pattern-primer.md) augmenting-path engine with residual bookkeeping.

**Why BFS (and not DFS)?** BFS finds the *shortest* augmenting path in hops, which bounds the number of augmentations by $O(VE)$ — that's what turns the generic Ford-Fulkerson into polynomial Edmonds-Karp. (DFS can blow up on bad instances.)

## Approach 1 — Ford-Fulkerson with DFS

Same residual machinery, DFS paths: correct but can take $O(\text{maxflow})$ augmentations on adversarial inputs.

## Approach 2 — Edmonds-Karp (BFS) (the repo's version, optimal for this scale)

```kotlin
fun maxFlowEdmondsKarp(graph: Array<IntArray>, source: Int, sink: Int): Int {
    val n = graph.size
    val residual = Array(n) { graph[it].copyOf() }   // capacities still available (forward + undo)
    val parent = IntArray(n)
    var flow = 0

    // Minimum capacity along the found path
    fun calculateBottleneck(): Int {
        var v = sink
        var minCap = Int.MAX_VALUE
        while (v != source) {
            val u = parent[v]
            minCap = minOf(minCap, residual[u][v])
            v = u
        }
        return minCap
    }

    // BFS: find any augmenting path (shortest in hops)
    fun findAugmentingPath(): Int {
        parent.fill(-1)
        parent[source] = source
        val queue = ArrayDeque<Int>().apply { add(source) }

        while (queue.isNotEmpty()) {
            val u = queue.removeFirst()
            for (v in residual[u].indices) {
                if (parent[v] == -1 && residual[u][v] > 0) {   // unvisited + usable capacity
                    parent[v] = u
                    if (v == sink) return calculateBottleneck()   // path found
                    queue.add(v)
                }
            }
        }
        return 0                                         // no path: done
    }

    // Push the bottleneck; add reverse (undo) capacity
    fun updateResidual(minCap: Int) {
        var v = sink
        while (v != source) {
            val u = parent[v]
            residual[u][v] -= minCap                     // use up forward capacity
            residual[v][u] += minCap                     // add reverse capacity
            v = u
        }
    }

    while (true) {
        val minCap = findAugmentingPath()
        if (minCap == 0) break
        updateResidual(minCap)
        flow += minCap
    }
    return flow
}
```

```java
import java.util.*;

public class MaxFlowEdmondsKarp {
    /**
     * @param graph  capacity matrix (directed)
     * @param source source node
     * @param sink   sink node
     * @return       maximum flow
     */
    public int maxFlow(int[][] graph, int source, int sink) {
        int n = graph.length;
        int[][] residual = new int[n][];
        for (int i = 0; i < n; i++) residual[i] = graph[i].clone();
        int[] parent = new int[n];
        int flow = 0;

        while (true) {
            Arrays.fill(parent, -1);
            parent[source] = source;
            Deque<Integer> queue = new ArrayDeque<>();
            queue.add(source);

            while (!queue.isEmpty() && parent[sink] == -1) {
                int u = queue.poll();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && residual[u][v] > 0) {
                        parent[v] = u;
                        queue.add(v);
                    }
                }
            }
            if (parent[sink] == -1) break;               // no augmenting path

            int bottleneck = Integer.MAX_VALUE;          // min capacity along the path
            for (int v = sink; v != source; v = parent[v])
                bottleneck = Math.min(bottleneck, residual[parent[v]][v]);

            for (int v = sink; v != source; v = parent[v]) {
                residual[parent[v]][v] -= bottleneck;    // forward: consume
                residual[v][parent[v]] += bottleneck;    // reverse: undo capacity
            }
            flow += bottleneck;
        }
        return flow;
    }
}
```

```cpp
#include <climits>
#include <queue>
#include <vector>

class MaxFlowEdmondsKarp {
public:
    /**
     * @param graph  capacity matrix (directed)
     * @param source source node
     * @param sink   sink node
     * @return       maximum flow
     */
    int maxFlow(std::vector<std::vector<int>>& graph, int source, int sink) {
        int n = graph.size();
        std::vector<std::vector<int>> residual = graph;
        std::vector<int> parent(n);
        int flow = 0;

        while (true) {
            std::fill(parent.begin(), parent.end(), -1);
            parent[source] = source;
            std::queue<int> q;
            q.push(source);

            while (!q.empty() && parent[sink] == -1) {
                int u = q.front(); q.pop();
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && residual[u][v] > 0) {
                        parent[v] = u;
                        q.push(v);
                    }
                }
            }
            if (parent[sink] == -1) break;               // no augmenting path

            int bottleneck = INT_MAX;                    // min capacity along the path
            for (int v = sink; v != source; v = parent[v])
                bottleneck = std::min(bottleneck, residual[parent[v]][v]);

            for (int v = sink; v != source; v = parent[v]) {
                residual[parent[v]][v] -= bottleneck;    // forward: consume
                residual[v][parent[v]] += bottleneck;    // reverse: undo capacity
            }
            flow += bottleneck;
        }
        return flow;
    }
};
```

```python
from collections import deque

def max_flow(graph: list[list[int]], source: int, sink: int) -> int:
    """
    @param graph:  capacity matrix (directed)
    @param source: source node
    @param sink:   sink node
    @return:       maximum flow
    """
    n = len(graph)
    residual = [row[:] for row in graph]   # capacities still available (forward + undo)
    flow = 0

    while True:
        parent = [-1] * n
        parent[source] = source
        q = deque([source])

        while q and parent[sink] == -1:
            u = q.popleft()
            for v in range(n):
                if parent[v] == -1 and residual[u][v] > 0:
                    parent[v] = u
                    q.append(v)

        if parent[sink] == -1:
            break                            # no augmenting path

        bottleneck = float("inf")            # min capacity along the path
        v = sink
        while v != source:
            bottleneck = min(bottleneck, residual[parent[v]][v])
            v = parent[v]

        v = sink
        while v != source:
            u = parent[v]
            residual[u][v] -= bottleneck    # forward: consume
            residual[v][u] += bottleneck    # reverse: undo capacity
            v = u
        flow += bottleneck
    return flow
```

```rust
use std::collections::VecDeque;

impl Solution {
    /// @param graph  capacity matrix (directed)
    /// @param source source node
    /// @param sink   sink node
    /// @return       maximum flow
    pub fn max_flow(graph: Vec<Vec<i32>>, source: usize, sink: usize) -> i32 {
        let n = graph.len();
        let mut residual = graph.clone();   // capacities still available (forward + undo)
        let mut flow = 0;

        loop {
            let mut parent = vec![usize::MAX; n];
            parent[source] = source;
            let mut q = VecDeque::from([source]);

            while let Some(u) = q.pop_front() {
                for v in 0..n {
                    if parent[v] == usize::MAX && residual[u][v] > 0 {
                        parent[v] = u;
                        q.push_back(v);
                    }
                }
            }
            if parent[sink] == usize::MAX { break; }   // no augmenting path

            let mut bottleneck = i32::MAX;             // min capacity along the path
            let mut v = sink;
            while v != source {
                bottleneck = bottleneck.min(residual[parent[v]][v]);
                v = parent[v];
            }

            let mut v = sink;
            while v != source {
                let u = parent[v];
                residual[u][v] -= bottleneck;          // forward: consume
                residual[v][u] += bottleneck;          // reverse: undo capacity
                v = u;
            }
            flow += bottleneck;
        }
        flow
    }
}
```

## Dry run

**Input:** `0->1:3, 0->2:2, 1->2:1, 1->3:2, 2->3:3`, source 0, sink 3.

```
BFS 1: path 0 -> 1 -> 3, bottleneck = min(3, 2) = 2.
  residual: 0->1:1, 1->0:2 | 1->3:0, 3->1:2.   flow = 2.
BFS 2: path 0 -> 2 -> 3, bottleneck = min(2, 3) = 2.
  residual: 0->2:0, 2->0:2 | 2->3:1, 3->2:2.   flow = 4.
BFS 3: path 0 -> 1 -> 2 -> 3, bottleneck = min(1, 1, 1) = 1.
  residual: 0->1:0, 1->0:3 | 1->2:0, 2->1:1 | 2->3:0, 3->2:3.  flow = 5.
BFS 4: from 0, no positive-capacity edge remains -> parent[sink] = -1 -> stop.

Output: 5 ✓
```

The third path is the subtle one: the naive first-come routing (`0-1-3` then `0-2-3`) leaves a *middle* path `0-1-2-3` with capacity 1 that only BFS-3 discovers — and the reverse edges (like `1->0:2`) are what let future augmentations correct earlier choices if they ever blocked a better routing. The residual bookkeeping *is* the algorithm.

## Complexity

**Time.** $O(V)$ augmentations × $O(E)$ BFS each:

$$
T(V, E) = O(VE^2)
$$

**Space.** The residual matrix:

$$
S = O(V^2)
$$

## Variants & follow-ups

- **Maximum Bipartite Matching** ([17.2](maximum-bipartite-matching.md)) — the flow engine specialized to assignment problems (or model it as flow with unit capacities).
- **Min Cut / Max Flow Min Cut Theorem** — the max flow value equals the min cut capacity; the classic interview proof question this algorithm answers implicitly.
- **Edge-list Edmonds-Karp variants** (`src/main/kotlin/graph/flow_network/EdmondsKarp*.kt`) — the same algorithm over adjacency lists with parallel-edge support.
- **Interview follow-up:** "Why are reverse edges necessary?" A greedy path selection can use an edge that a better global routing needs elsewhere. The reverse edge gives later augmentations a way to *cancel* that usage — flow pushed `u -> v` can be "unpushed" via `v -> u`. Without reverse edges, the algorithm is not optimal; with them, the residual graph fully describes what's still possible.
