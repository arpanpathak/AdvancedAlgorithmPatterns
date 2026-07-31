# 17.14 Find Articulation Points

> **Source:** [`src/main/kotlin/graph/articulation_point/FindArticulationPoints.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/articulation_point/FindArticulationPoints.kt)
> **Pattern:** Tarjan's low-link DFS · **Core page**

## The Problem

All vertices whose removal **disconnects** the graph (articulation points / cut vertices).

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  edges = [[0,1],[0,2],[1,2],[2,3],[3,4]]
Output: {2, 3}   (removing 2 splits {0,1} from {3,4}; removing 3 splits 2's side)
```

## Intuition — a vertex is a cut point iff a child can't climb above it

The Tarjan DFS ([17.10](critical-connections-in-a-network.md) sibling) tracks `label` (discovery time) and `low` (earliest reachable label via back edges). A vertex `u` is an articulation point iff:

- **root with ≥ 2 children** in the DFS tree — removing it disconnects its subtrees;
- **non-root with a child `v` where `low[v] >= label[u]`** — `v`'s subtree can't reach anything above `u`; cutting `u` strands it.

```kotlin
fun dfs(u: Int, parent: Int) {
    label[u] = low[u] = time++
    var children = 0

    for (v in G[u]) {
        if (v == parent) continue

        if (label[v] == -1) {              // tree edge
            children++
            dfs(v, u)
            low[u] = minOf(low[u], low[v])

            if (parent == -1 && children >= 2) cutVertices.add(u)          // root rule
            if (parent != -1 && low[v] >= label[u]) cutVertices.add(u)     // non-root rule
        } else {
            low[u] = minOf(low[u], label[v])   // back edge: climb
        }
    }
}
```

**Why `low[v] >= label[u]` (vs `>` in bridges)?** A bridge needs `low[v] > label[u]` (the edge alone is the connection); an articulation point needs `low[v] >= label[u]` — the child reaches *at best* u itself. Removing u disconnects even if v can reach u.

**Why the special root rule?** The root has no `low`-comparison parent — it's a cut point iff its DFS tree has ≥ 2 children (one subtree can't reach another without the root).

## Approach 1 — Remove-and-test each vertex (O(V·(V+E)))

For each vertex, remove and check connectivity: correct, cubic-ish.

## Approach 2 — Tarjan's low-link (the repo's version, optimal)

```kotlin
class FindArticulationPoints {
    private lateinit var G: Array<MutableList<Int>>
    private var time = 0
    private lateinit var label: IntArray
    private lateinit var low: IntArray

    /**
     * @param n           vertex count
     * @param connections undirected edges
     * @return            articulation points
     */
    fun findCutVertices(n: Int, connections: List<List<Int>>): Set<Int> {
        G = Array(n) { mutableListOf() }
        time = 0
        label = IntArray(n) { -1 }
        low = IntArray(n)
        val cutVertices = mutableSetOf<Int>()

        for (edge in connections) {
            val i = edge[0]
            val j = edge[1]
            G[i].add(j)
            G[j].add(i)
        }

        fun dfs(u: Int, parent: Int) {
            label[u] = low[u] = time++
            var children = 0

            for (v in G[u]) {
                if (v == parent) continue

                if (label[v] == -1) {
                    children++
                    dfs(v, u)
                    low[u] = minOf(low[u], low[v])

                    if (parent == -1 && children >= 2) cutVertices.add(u)
                    if (parent != -1 && low[v] >= label[u]) cutVertices.add(u)
                } else {
                    low[u] = minOf(low[u], label[v])
                }
            }
        }

        for (i in 0 until n) if (label[i] == -1) dfs(i, -1)
        return cutVertices
    }
}
```

```java
import java.util.*;

public class FindArticulationPoints {
    private List<Integer>[] g;
    private int[] label, low;
    private int time = 0;

    private void dfs(int u, int parent, Set<Integer> cuts) {
        label[u] = low[u] = time++;
        int children = 0;

        for (int v : g[u]) {
            if (v == parent) continue;

            if (label[v] == -1) {
                children++;
                dfs(v, u, cuts);
                low[u] = Math.min(low[u], low[v]);

                if (parent == -1 && children >= 2) cuts.add(u);
                if (parent != -1 && low[v] >= label[u]) cuts.add(u);
            } else {
                low[u] = Math.min(low[u], label[v]);
            }
        }
    }

    /**
     * @param n           vertex count
     * @param connections undirected edges
     * @return            articulation points
     */
    @SuppressWarnings("unchecked")
    public List<Integer> findCutVertices(int n, List<List<Integer>> connections) {
        g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        label = new int[n];
        low = new int[n];
        Arrays.fill(label, -1);

        for (List<Integer> e : connections) {
            g[e.get(0)].add(e.get(1));
            g[e.get(1)].add(e.get(0));
        }

        Set<Integer> cuts = new HashSet<>();
        for (int i = 0; i < n; i++) if (label[i] == -1) dfs(i, -1, cuts);
        return new ArrayList<>(cuts);
    }
}
```

```cpp
#include <vector>
#include <set>

class FindArticulationPoints {
    std::vector<std::vector<int>> g;
    std::vector<int> label, low;
    int time = 0;

    void dfs(int u, int parent, std::set<int>& cuts) {
        label[u] = low[u] = time++;
        int children = 0;

        for (int v : g[u]) {
            if (v == parent) continue;

            if (label[v] == -1) {
                children++;
                dfs(v, u, cuts);
                low[u] = std::min(low[u], low[v]);

                if (parent == -1 && children >= 2) cuts.insert(u);
                if (parent != -1 && low[v] >= label[u]) cuts.insert(u);
            } else {
                low[u] = std::min(low[u], label[v]);
            }
        }
    }

public:
    /**
     * @param n           vertex count
     * @param connections undirected edges
     * @return            articulation points
     */
    std::set<int> findCutVertices(int n, std::vector<std::vector<int>>& connections) {
        g.assign(n, {});
        label.assign(n, -1);
        low.assign(n, 0);

        for (auto& e : connections) {
            g[e[0]].push_back(e[1]);
            g[e[1]].push_back(e[0]);
        }

        std::set<int> cuts;
        for (int i = 0; i < n; i++) if (label[i] == -1) dfs(i, -1, cuts);
        return cuts;
    }
};
```

```python
def find_cut_vertices(n: int, connections: list[list[int]]) -> set[int]:
    """
    @param n:           vertex count
    @param connections: undirected edges
    @return:            articulation points
    """
    graph = [[] for _ in range(n)]
    for u, v in connections:
        graph[u].append(v)
        graph[v].append(u)

    label = [-1] * n
    low = [0] * n
    time = 0
    cuts = set()

    def dfs(u: int, parent: int) -> None:
        nonlocal time
        label[u] = low[u] = time
        time += 1
        children = 0

        for v in graph[u]:
            if v == parent:
                continue

            if label[v] == -1:
                children += 1
                dfs(v, u)
                low[u] = min(low[u], low[v])

                if parent == -1 and children >= 2:
                    cuts.add(u)                     # root rule
                if parent != -1 and low[v] >= label[u]:
                    cuts.add(u)                     # non-root rule
            else:
                low[u] = min(low[u], label[v])      # back edge

    for i in range(n):
        if label[i] == -1:
            dfs(i, -1)
    return cuts
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param n           vertex count
    /// @param connections undirected edges
    /// @return            articulation points
    pub fn find_cut_vertices(n: i32, connections: Vec<Vec<i32>>) -> HashSet<i32> {
        let n = n as usize;
        let mut g = vec![Vec::new(); n];
        for e in &connections {
            g[e[0] as usize].push(e[1] as usize);
            g[e[1] as usize].push(e[0] as usize);
        }

        let mut label = vec![-1; n];
        let mut low = vec![0; n];
        let mut time = 0;
        let mut cuts = HashSet::new();

        fn dfs(u: usize, parent: i32, g: &Vec<Vec<usize>>, label: &mut Vec<i32>,
               low: &mut Vec<i32>, time: &mut i32, cuts: &mut HashSet<i32>) {
            label[u] = *time;
            low[u] = *time;
            *time += 1;
            let mut children = 0;

            for &v in &g[u] {
                if v as i32 == parent { continue; }

                if label[v] == -1 {
                    children += 1;
                    dfs(v, u as i32, g, label, low, time, cuts);
                    low[u] = low[u].min(low[v]);

                    if parent == -1 && children >= 2 { cuts.insert(u as i32); }
                    if parent != -1 && low[v] >= label[u] { cuts.insert(u as i32); }
                } else {
                    low[u] = low[u].min(label[v]);   // back edge
                }
            }
        }

        for i in 0..n {
            if label[i] == -1 { dfs(i, -1, &g, &mut label, &mut low, &mut time, &mut cuts); }
        }
        cuts
    }
}
```

## Dry run

**Input:** `edges = [[0,1],[0,2],[1,2],[2,3],[3,4]]`.

```
DFS from 0: label[0]=0, low[0]=0.  children: 1, 2
  dfs(1): label=1, low=1.  neighbor 0 (parent).  back to 0: low[0] = min(0, low[1]=1) = 0.
  dfs(2) via 0: label=2, low=2.  neighbor 3:
    dfs(3): label=3, low=3.  neighbor 4:
      dfs(4): label=4, low=4.  no children.
      back: low[3] = min(3, low[4]=4) = 3.  4: low[4]=4 >= label[3]=3 -> cut {3}
    back: low[2] = min(2, low[3]=3) = 2.  3: low[3]=3 >= label[2]=2 -> cut {2, 3}
  back: low[0] = min(0, low[2]=2) = 0.  neighbor 1 (already labeled): low[0] = min(0, label[1]=1) = 0.
  0 has children {1, 2} -> but 0's dfs counted: children=2 -> root rule? parent == -1 -> cut {0, 2, 3}? 
```

**Correction — the root rule applies only when the children are DFS-tree children that don't share a back edge:**

```
The edge 1-2 is a back edge between 0's two children — they can reach each other without 0.
So removing 0 does NOT disconnect 1 and 2. The correct DFS order matters:

dfs(0): visit 1 first: dfs(1): neighbors 0(parent), 2:
  dfs(2): neighbors 0 (already labeled -> back edge: low[2] = min(2, label[0]=0) = 0), 1(parent), 3:
    dfs(3): ... cut {3}, then {2} via child 3... 
    low[2] = 0 (via the back edge to 0)
  back to 1: low[1] = min(1, low[2]=0) = 0.
  check at 1: child 2: low[2]=0 >= label[1]=1? NO (0 < 1) -> 1 is NOT a cut.
back to 0: child 1: low[1]=0 >= label[0]=0? root rule instead: children = 1 so far.
  then neighbor 2 (already labeled): low[0] = min(0, label[2]=2) = 0.

children of root 0 = 1 (only 1, since 2 was visited through 1) -> root NOT a cut.

Output: {2, 3} ✓
```

The back edge 1–2 is what rescues vertex 0 and 1: 2's low drops to 0 (the root) through the back edge, so no child of 1 or 0 is stranded. The `low[v] >= label[u]` rule fires only for 2 (child 3 can't climb past it) and 3 (child 4 can't climb past it) — exactly the cut vertices.

## Complexity

**Time.** One DFS:

$$
T(V, E) = O(V + E)
$$

**Space.** label/low + recursion:

$$
S(V, E) = O(V)
$$

## Variants & follow-ups

- **Critical Connections In A Network** ([17.10](critical-connections-in-a-network.md)) — the bridge twin (`>` vs `>=`, no root rule).
- **Strongly Connected Components** ([6.7](../ch06-graphs/strongly-connected-components.md)) — Kosaraju/Tarjan's shared ancestry.
- **Interview follow-up:** "Why `>=` for cut vertices but `>` for bridges?" A bridge separates its endpoints' sides — the child must not reach the parent *at all* (`low[v] > label[u]`). A cut vertex separates even if the child can reach *the vertex itself* — the subtree must climb above u (`low[v] >= label[u]`). The one-character difference is the whole distinction.
