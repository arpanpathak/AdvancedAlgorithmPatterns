# 6.9 Redundant Connection

> **Source:** [`src/main/kotlin/graph/mst/FindRedundentConnections.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/graph/mst/FindRedundentConnections.kt)
> **Pattern:** Union-Find cycle detection · **Core page**

## The Problem

Given `edges` of an undirected graph that was a tree plus **one extra edge**, return that redundant edge (the one that creates the cycle; return the last such edge in the input order).

- Constraints: $3 \le n \le 1000$; edges form a tree + one extra.

## Examples

```
Input:  edges = [[1,2],[1,3],[2,3]]   -> Output: [2,3]
Input:  edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]   -> Output: [1,4]
```

## Intuition — the redundant edge is the first one that *joins two already-connected nodes*

A tree plus one edge has exactly one cycle; the extra edge is precisely the one whose endpoints are **already connected through earlier edges**. Union-Find answers "already connected?" in near-O(1):

```
for (u, v) in edges:
    if find(u) == find(v): return [u, v]     # already in the same component: this edge closes a cycle
    union(u, v)
```

**Why is the *first* such edge the answer?** The problem asks for the edge "that appears last in the input" among candidates — and the greedy scan naturally returns the first edge that creates a cycle *in input order*, which is exactly the redundant one (removing it restores the tree; all earlier edges were cycle-free by construction). The last-in-input convention matches the "return the edge that completes the cycle when processed in order" semantics.

**Why Union-Find and not DFS?** The incremental "connect two components, detect when they're already one" is literally what Union-Find is for — the same structure that powered [6.6](min-cost-to-connect-all-points.md)'s Kruskal. A DFS per edge would be $O(n^2)$; Union-Find is amortized $O(\alpha(n))$ per edge.

**The `parent[x] = find(parent[x])` compression** — the repo's `find` does path compression recursively, flattening the tree so future finds are O(1)-ish. The 1001-size parent array covers node labels 1..n (1-indexed).

## Approach 1 — DFS per edge (O(n^2))

For each edge, check connectivity without it: correct, quadratic.

## Approach 2 — Union-Find incremental (the repo's version, optimal)

```kotlin
class FindRedundentConnections {
    /**
     * @param edges tree + one extra edge
     * @return      the redundant edge
     */
    fun findRedundantConnection(edges: Array<IntArray>): IntArray {
        val parent = IntArray(1001) { it }       // node labels 1..n

        fun find(x: Int): Int {
            if (parent[x] != x) parent[x] = find(parent[x])   // path compression
            return parent[x]
        }

        fun union(x: Int, y: Int): Boolean {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX == rootY) return false     // already connected: this edge closes a cycle
            parent[rootY] = rootX
            return true
        }

        for ((u, v) in edges) {
            if (!union(u, v)) return intArrayOf(u, v)   // the redundant edge
        }
        return intArrayOf()
    }
}
```

```java
public class FindRedundantConnection {
    private int[] parent;

    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
        return parent[x];
    }

    private boolean union(int x, int y) {
        int rootX = find(x), rootY = find(y);
        if (rootX == rootY) return false;        // already connected: cycle
        parent[rootY] = rootX;
        return true;
    }

    /**
     * @param edges tree + one extra edge
     * @return      the redundant edge
     */
    public int[] findRedundantConnection(int[][] edges) {
        parent = new int[1001];
        for (int i = 0; i < parent.length; i++) parent[i] = i;

        for (int[] e : edges) {
            if (!union(e[0], e[1])) return e;   // the redundant edge
        }
        return new int[0];
    }
}
```

```cpp
#include <vector>

class FindRedundantConnection {
    std::vector<int> parent;

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
        return parent[x];
    }

    bool union_(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;              // already connected: cycle
        parent[ry] = rx;
        return true;
    }

public:
    /**
     * @param edges tree + one extra edge
     * @return      the redundant edge
     */
    std::vector<int> findRedundantConnection(std::vector<std::vector<int>>& edges) {
        parent.resize(1001);
        for (int i = 0; i < 1001; i++) parent[i] = i;

        for (auto& e : edges) {
            if (!union_(e[0], e[1])) return e;  // the redundant edge
        }
        return {};
    }
};
```

```python
def find_redundant_connection(edges: list[list[int]]) -> list[int]:
    """
    @param edges: tree + one extra edge
    @return:      the redundant edge
    """
    parent = list(range(1001))

    def find(x: int) -> int:
        if parent[x] != x:
            parent[x] = find(parent[x])      # path compression
        return parent[x]

    def union(x: int, y: int) -> bool:
        rx, ry = find(x), find(y)
        if rx == ry:
            return False                     # already connected: cycle
        parent[ry] = rx
        return True

    for u, v in edges:
        if not union(u, v):
            return [u, v]                    # the redundant edge
    return []
```

```rust
impl Solution {
    /// @param edges tree + one extra edge
    /// @return      the redundant edge
    pub fn find_redundant_connection(edges: Vec<Vec<i32>>) -> Vec<i32> {
        let mut parent: Vec<usize> = (0..1001).collect();

        fn find(parent: &mut Vec<usize>, x: usize) -> usize {
            if parent[x] != x {
                let root = find(parent, parent[x]);
                parent[x] = root;            // path compression
            }
            parent[x]
        }

        for e in &edges {
            let (u, v) = (e[0] as usize, e[1] as usize);
            let (ru, rv) = (find(&mut parent, u), find(&mut parent, v));
            if ru == rv {
                return e.clone();            // the redundant edge
            }
            parent[rv] = ru;
        }
        vec![]
    }
}
```

## Dry run

**Input:** `edges = [[1,2],[1,3],[2,3]]`.

```
parent = [0,1,2,3,...]

edge [1,2]: find(1)=1, find(2)=2.  differ -> parent[2]=1.  ok.
edge [1,3]: find(1)=1, find(3)=3.  differ -> parent[3]=1.  ok.
edge [2,3]: find(2): parent[2]=1 -> root 1.  find(3): parent[3]=1 -> root 1.
            SAME root -> return [2,3] ✓
```

The last edge closes the triangle: nodes 1, 2, 3 were already one component after the first two edges, so `[2,3]`'s endpoints are connected — the cycle it completes makes it redundant. Note the path compression at work in `find(2)`/`find(3)` — both chains collapse to root 1 in one recursive pass each.

## Complexity

**Time.** Near-O(1) per edge with compression:

$$
T(E) = O(E \cdot \alpha(n))
$$

**Space.** The parent array:

$$
S = O(n)
$$

## Variants & follow-ups

- **Kruskal's MST** ([6.6](min-cost-to-connect-all-points.md)) — the same `union` returning false is how Kruskal skips cycle-forming edges; this page is that primitive in isolation.
- **Redundant Connection II (directed)** — the directed variant needs in-degree and parent tracking on top; a two-case fix instead of the pure cycle test.
- **Interview follow-up:** "Why does the *first* cycle-closing edge equal the *last* redundant edge?" Each processed edge either merges two components or finds them already merged. Exactly one edge finds them merged (the tree had one cycle) — and since edges are processed in input order, that edge is simultaneously the first cycle-closer and the redundant edge. The scan needs no backtracking.
