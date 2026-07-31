# 17.6 Reorder Routes To Make All Paths Lead To City Zero

> **Source:** [`src/main/kotlin/graph/ReorderRoutesToMakeAllPathsLeadToCityZero.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/ReorderRoutesToMakeAllPathsLeadToCityZero.kt)
> **Pattern:** directed-edge DFS · **Core page**

## The Problem

There are `n` cities and `n-1` **directed** roads (a tree). Return the **minimum number of roads to reverse** so that every city can reach city 0.

- Constraints: $2 \le n \le 5 \times 10^4$; the roads form a tree rooted (directionally) at... well, 0 must become reachable from all.

## Examples

```
Input:  n = 6, connections = [[0,1],[1,3],[2,3],[4,0],[4,5]]
Output: 3    (reverse [1,3], [2,3], [4,5] — 0 reaches everything via the tree)
```

## Intuition — DFS from 0; every road pointing *away* must be flipped

The undirected tree is a tree; direction tells you whether a road points *toward* city 0 or *away* from it. Start DFS at 0:

- a road `u -> v` used as `u` → `v` (pointing **away** from the root) must be **reversed** — otherwise a city beyond `v` can't reach 0;
- a road `v -> u` pointing **toward** the root is fine — no flip.

Implementation trick: store each edge in **both** adjacency lists (the repo stores the whole edge, then checks `from == city` to know which end we're at). The DFS visits the tree once; each time we traverse an edge *away from the root*, count a flip.

**Why is every away-edge a forced flip?** The roads form a tree: exactly one path between any city and 0. If any road on that path points away from 0, the path is broken and must be fixed — and flipping that one road fixes it (the tree's uniqueness means no alternative route). So the answer is precisely "how many tree edges point away from 0" — a single DFS count.

**Why store the whole edge and check orientation?** The repo's `graph[city].add(edge)` for both endpoints (forward + backward) keeps one edge object; at traversal time `if (from == city)` distinguishes the direction — the "is this edge leaving me?" test. Cleaner than two separate adjacency lists when you need the original orientation.

## Approach 1 — BFS the other way (also correct)

Reverse every edge conceptually and BFS from 0, counting each original edge used in reverse: same answer, same complexity — the DFS version below is the direct form.

## Approach 2 — DFS counting away-edges (the repo's version, optimal)

```kotlin
class ReorderRoutesToMakeAllPathsLeadToCityZero {
    /**
     * @param n           number of cities
     * @param connections directed roads [from, to]
     * @return            minimum roads to reverse so every city reaches 0
     */
    fun minReorder(n: Int, connections: Array<IntArray>): Int {
        val graph = Array(n) { mutableListOf<IntArray>() }

        // Build the undirected adjacency, keeping each edge's original direction
        for (edge in connections) {
            graph[edge[0]].add(edge)     // forward endpoint
            graph[edge[1]].add(edge)     // backward endpoint
        }

        var changes = 0
        val visited = BooleanArray(n)

        fun dfs(city: Int) {
            visited[city] = true
            for (edge in graph[city]) {
                val (from, to) = edge
                val neighbor = if (from == city) to else from
                if (!visited[neighbor]) {
                    if (from == city) changes++   // edge points away from 0: must flip
                    dfs(neighbor)
                }
            }
        }

        dfs(0)
        return changes
    }
}
```

```java
import java.util.*;

public class ReorderRoutesToCityZero {
    private List<List<int[]>> graph;
    private int changes = 0;

    /**
     * @param n           number of cities
     * @param connections directed roads [from, to]
     * @return            minimum roads to reverse so every city reaches 0
     */
    public int minReorder(int n, int[][] connections) {
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        for (int[] edge : connections) {
            graph.get(edge[0]).add(edge);    // forward endpoint
            graph.get(edge[1]).add(edge);    // backward endpoint
        }

        dfs(0, new boolean[n]);
        return changes;
    }

    private void dfs(int city, boolean[] visited) {
        visited[city] = true;
        for (int[] edge : graph.get(city)) {
            int neighbor = (edge[0] == city) ? edge[1] : edge[0];
            if (!visited[neighbor]) {
                if (edge[0] == city) changes++;   // edge points away from 0: must flip
                dfs(neighbor, visited);
            }
        }
    }
}
```

```cpp
#include <vector>

class ReorderRoutesToCityZero {
    std::vector<std::vector<std::vector<int>>> graph;
    int changes = 0;

    void dfs(int city, std::vector<bool>& visited) {
        visited[city] = true;
        for (auto& edge : graph[city]) {
            int neighbor = (edge[0] == city) ? edge[1] : edge[0];
            if (!visited[neighbor]) {
                if (edge[0] == city) changes++;   // edge points away from 0: must flip
                dfs(neighbor, visited);
            }
        }
    }

public:
    /**
     * @param n           number of cities
     * @param connections directed roads [from, to]
     * @return            minimum roads to reverse so every city reaches 0
     */
    int minReorder(int n, std::vector<std::vector<int>>& connections) {
        graph.assign(n, {});
        for (auto& edge : connections) {
            graph[edge[0]].push_back(edge);    // forward endpoint
            graph[edge[1]].push_back(edge);    // backward endpoint
        }
        std::vector<bool> visited(n, false);
        dfs(0, visited);
        return changes;
    }
};
```

```python
def min_reorder(n: int, connections: list[list[int]]) -> int:
    """
    @param n:           number of cities
    @param connections: directed roads [from, to]
    @return:            minimum roads to reverse so every city reaches 0
    """
    graph = [[] for _ in range(n)]
    for a, b in connections:
        graph[a].append((a, b))    # forward endpoint
        graph[b].append((a, b))    # backward endpoint

    changes = 0
    visited = [False] * n

    def dfs(city: int) -> None:
        nonlocal changes
        visited[city] = True
        for frm, to in graph[city]:
            neighbor = to if frm == city else frm
            if not visited[neighbor]:
                if frm == city:    # edge points away from 0: must flip
                    changes += 1
                dfs(neighbor)

    dfs(0)
    return changes
```

```rust
impl Solution {
    /// @param n           number of cities
    /// @param connections directed roads [from, to]
    /// @return            minimum roads to reverse so every city reaches 0
    pub fn min_reorder(n: i32, connections: Vec<Vec<i32>>) -> i32 {
        let n = n as usize;
        let mut graph = vec![Vec::new(); n];          // (from, to) per endpoint
        for e in &connections {
            let (a, b) = (e[0] as usize, e[1] as usize);
            graph[a].push((a, b));                   // forward endpoint
            graph[b].push((a, b));                   // backward endpoint
        }

        let mut visited = vec![false; n];
        let mut changes = 0;

        fn dfs(city: usize, graph: &Vec<Vec<(usize, usize)>>, visited: &mut Vec<bool>, changes: &mut i32) {
            visited[city] = true;
            for &(frm, to) in &graph[city] {
                let neighbor = if frm == city { to } else { frm };
                if !visited[neighbor] {
                    if frm == city { *changes += 1; }   // edge points away from 0: must flip
                    dfs(neighbor, graph, visited, changes);
                }
            }
        }

        dfs(0, &graph, &mut visited, &mut changes);
        changes
    }
}
```

## Dry run

**Input:** `n = 6`, `connections = [[0,1],[1,3],[2,3],[4,0],[4,5]]`.

```
undirected adjacency (each edge stored at both endpoints):
  0: [0->1], [4->0]    1: [0->1], [1->3]    2: [2->3]
  3: [1->3], [2->3]    4: [4->0], [4->5]    5: [4->5]

dfs(0):
  edge [0->1]: from==0 (away) -> changes=1.  dfs(1).
    edge [0->1]: neighbor 0 visited -> skip.
    edge [1->3]: from==1 (away) -> changes=2.  dfs(3).
      edge [1->3]: neighbor 1 visited.
      edge [2->3]: from==2, neighbor 2 -> not visited; from!=3? at city 3, edge [2->3]: from=2 != 3 -> toward 0 -> no flip.  dfs(2).
        edge [2->3]: neighbor 3 visited.
  edge [4->0] at city 0: from==4 != 0 -> toward 0 -> no flip.  dfs(4).
    edge [4->0]: neighbor 0 visited.
    edge [4->5]: from==4 (away) -> changes=3.  dfs(5).
      edge [4->5]: neighbor 4 visited.

Output: 3 ✓
```

The orientation test `from == city` is the whole trick: at city 3, the edge `[2->3]` has `from = 2 ≠ 3`, meaning it points *toward* the root — no flip. Every away-edge (`0->1`, `1->3`, `4->5`) is counted exactly once, and the tree's uniqueness guarantees each is necessary.

## Complexity

**Time.** One DFS over the tree:

$$
T(n) = O(n)
$$

**Space.** The adjacency storage:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Minimum Edges To Reverse To Reach Destination** — the weighted version: each reversed edge costs 1 (0-1 BFS) instead of being free-forced by the tree structure.
- **Evaluate Division** ([17.7](evaluate-division.md)) — the same "edges carry extra meaning (direction/weight)" traversal, with products instead of flips.
- **Interview follow-up:** "Why is the answer just a count of away-edges?" The roads form a tree, so each city has exactly one path to 0 — and that path needs every edge pointing toward 0. An edge pointing away from 0 on any such path breaks exactly that path, and flipping it fixes it. The tree structure makes the count both necessary and sufficient.
