# 6.6 Min Cost To Connect All Points

> **Source:** [`src/main/kotlin/tree/mst/MinCostToConnectAllPointsKruskal.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/tree/mst/MinCostToConnectAllPointsKruskal.kt)
> **Pattern:** Kruskal MST + Union-Find · **Core page**

## The Problem

Given `points[i] = [x, y]`, connect all points so the total **Manhattan distance** (`|x1 - x2| + |y1 - y2|`) of the connecting edges is minimized. Return the minimum cost.

- Constraints: $1 \le n \le 1000$; coordinates in $[-10^6, 10^6]$.

## Examples

```
Input:  points = [[0,0],[2,2],[3,10],[5,2],[7,0]]
Output: 20   (edges: (0,0)-(2,2)=4, (2,2)-(5,2)=3, (5,2)-(7,0)=4, (2,2)-(3,10)=9 -> 4+3+4+9=20)

Input:  points = [[0,0],[1,1],[1,0],[-1,1]]
Output: 4    (three edges of Manhattan length 1 each — the unit square minus one side)
```

## Intuition — a spanning tree problem wearing a geometry costume

"Connect all points with minimum total edge cost" is literally the definition of a **minimum spanning tree (MST)**. The Manhattan distance is just the edge weight formula. Two classic engines build an MST:

- **Kruskal:** sort all edges by weight, add them one by one, *skipping any edge whose endpoints are already connected*. Needs Union-Find to answer "already connected?" in near-constant time.
- **Prim:** grow one tree outward, always taking the cheapest edge touching it. No sorting needed.

Kruskal's correctness rests on the **cut property**: the cheapest edge crossing any cut of the graph belongs to some MST. Greedily taking the global cheapest edge that doesn't create a cycle is the cut property applied cut-by-cut — each accepted edge merges two components, and it's the cheapest edge between them.

**The Union-Find engine** answers two questions: `find(x)` — "which component is x in?" (with *path compression*: while climbing, point nodes straight at the root) and `union(x, y)` — "merge the components" (with *union by rank*: hang the shorter tree under the taller). Together they make each operation essentially $O(\alpha(n))$ — the inverse Ackermann function, effectively constant.

**The dense-graph twist:** $n = 1000$ means $n(n-1)/2 \approx 5 \times 10^5$ edges. That's perfectly fine to generate explicitly and sort ($O(n^2 \log n)$) — but it's worth *saying* that on a complete graph Prim runs in $O(n^2)$ without a heap, which is why the repo ships both.

## Approach 1 — Prim's on a dense graph

Maintain `dist[]` of each point to the growing tree; repeat $n$ times "pick the closest unclaimed point, add its edge". $O(n^2)$ time, $O(n)$ space — no heap, no edge list. For a complete graph this *beats* Kruskal.

## Approach 2 — Kruskal + Union-Find (the repo's version)

```kotlin
class MinCostToConnectAllPointsKruskal {
    data class Edge(val point1: Int, val point2: Int, val weight: Int)

    data class UnionFindNode(var parent: Int, var rank: Int)

    val manhattanDistance = { p1: IntArray, p2: IntArray -> abs(p1[0] - p2[0]) + abs(p1[1] - p2[1]) }

    class UnionFind(size: Int) {
        private val nodes = Array(size) { UnionFindNode(it, 0) }

        // find with path compression (tailrec — the loop is really a tail call)
        tailrec fun find(x: Int): Int {
            if (nodes[x].parent != x) {
                nodes[x].parent = find(nodes[x].parent)
            }
            return nodes[x].parent
        }

        fun union(x: Int, y: Int) {
            val rootX = find(x)
            val rootY = find(y)
            if (rootX != rootY) {
                when {
                    nodes[rootX].rank > nodes[rootY].rank -> nodes[rootY].parent = rootX
                    nodes[rootX].rank < nodes[rootY].rank -> nodes[rootX].parent = rootY
                    else -> {
                        nodes[rootY].parent = rootX
                        nodes[rootX].rank += 1
                    }
                }
            }
        }
    }

    /**
     * @param points points[i] = [x, y] coordinates
     * @return       minimum total Manhattan distance to connect all points
     */
    fun minCostConnectPoints(points: Array<IntArray>): Int {
        val edges = mutableListOf<Edge>()
        for (i in points.indices) {
            for (j in i + 1 until points.size) {
                edges.add(Edge(i, j, manhattanDistance(points[i], points[j])))
            }
        }
        edges.sortBy { it.weight }

        val uf = UnionFind(points.size)
        var cost = 0
        for (edge in edges) {
            if (uf.find(edge.point1) != uf.find(edge.point2)) {   // different components?
                uf.union(edge.point1, edge.point2)                 // safe to add — no cycle
                cost += edge.weight
            }
        }
        return cost
    }
}
```

```java
import java.util.*;

public class MinCostToConnectAllPoints {
    private int[] parent, rank;

    /**
     * @param points points[i] = [x, y] coordinates
     * @return       minimum total Manhattan distance to connect all points
     */
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        List<int[]> edges = new ArrayList<>();          // {weight, i, j}
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int w = Math.abs(points[i][0] - points[j][0]) + Math.abs(points[i][1] - points[j][1]);
                edges.add(new int[]{w, i, j});
            }
        }
        edges.sort(Comparator.comparingInt(e -> e[0]));

        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int cost = 0, taken = 0;
        for (int[] e : edges) {
            if (union(e[1], e[2])) {                    // returns false if already connected
                cost += e[0];
                if (++taken == n - 1) break;            // MST has exactly n-1 edges
            }
        }
        return cost;
    }

    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
        return parent[x];
    }

    private boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        if (rank[rx] < rank[ry]) parent[rx] = ry;          // union by rank
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }
        return true;
    }
}
```

```cpp
#include <algorithm>
#include <numeric>
#include <vector>

class MinCostToConnectAllPoints {
    std::vector<int> parent, rank;

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
        return parent[x];
    }

    bool unite(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        if (rank[rx] < rank[ry]) parent[rx] = ry;          // union by rank
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }
        return true;
    }

public:
    /**
     * @param points points[i] = [x, y] coordinates
     * @return       minimum total Manhattan distance to connect all points
     */
    int minCostConnectPoints(std::vector<std::vector<int>>& points) {
        int n = points.size();
        std::vector<std::array<int,3>> edges;              // {weight, i, j}
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                int w = std::abs(points[i][0] - points[j][0]) + std::abs(points[i][1] - points[j][1]);
                edges.push_back({w, i, j});
            }
        std::sort(edges.begin(), edges.end());

        parent.resize(n);
        rank.assign(n, 0);
        std::iota(parent.begin(), parent.end(), 0);

        int cost = 0, taken = 0;
        for (auto& [w, i, j] : edges) {
            if (unite(i, j)) {
                cost += w;
                if (++taken == n - 1) break;
            }
        }
        return cost;
    }
};
```

```python
def min_cost_connect_points(points: list[list[int]]) -> int:
    """
    @param points: points[i] = [x, y] coordinates
    @return:       minimum total Manhattan distance to connect all points
    """
    n = len(points)
    edges = []
    for i in range(n):
        for j in range(i + 1, n):
            w = abs(points[i][0] - points[j][0]) + abs(points[i][1] - points[j][1])
            edges.append((w, i, j))
    edges.sort()

    parent = list(range(n))
    rank = [0] * n

    def find(x: int) -> int:
        if parent[x] != x:
            parent[x] = find(parent[x])        # path compression
        return parent[x]

    def unite(x: int, y: int) -> bool:
        rx, ry = find(x), find(y)
        if rx == ry:
            return False
        if rank[rx] < rank[ry]:                # union by rank
            parent[rx] = ry
        elif rank[rx] > rank[ry]:
            parent[ry] = rx
        else:
            parent[ry] = rx
            rank[rx] += 1
        return True

    cost = taken = 0
    for w, i, j in edges:
        if unite(i, j):
            cost += w
            taken += 1
            if taken == n - 1:                 # MST has exactly n-1 edges
                break
    return cost
```

```rust
impl Solution {
    /// @param points points[i] = [x, y] coordinates
    /// @return       minimum total Manhattan distance to connect all points
    pub fn min_cost_connect_points(points: Vec<Vec<i32>>) -> i32 {
        let n = points.len();
        let mut edges = Vec::new();
        for i in 0..n {
            for j in (i + 1)..n {
                let w = (points[i][0] - points[j][0]).abs() + (points[i][1] - points[j][1]).abs();
                edges.push((w, i, j));
            }
        }
        edges.sort_unstable();

        let mut parent: Vec<usize> = (0..n).collect();
        let mut rank = vec![0u32; n];

        fn find(parent: &mut Vec<usize>, x: usize) -> usize {
            if parent[x] != x {
                parent[x] = find(parent, parent[x]);      // path compression
            }
            parent[x]
        }

        let mut cost = 0;
        let mut taken = 0;
        for (w, i, j) in edges {
            let (ri, rj) = (find(&mut parent, i), find(&mut parent, j));
            if ri != rj {
                if rank[ri] < rank[rj] {                  // union by rank
                    parent[ri] = rj;
                } else if rank[ri] > rank[rj] {
                    parent[rj] = ri;
                } else {
                    parent[rj] = ri;
                    rank[ri] += 1;
                }
                cost += w;
                taken += 1;
                if taken == n - 1 {
                    break;                                // MST has exactly n-1 edges
                }
            }
        }
        cost
    }
}
```

## Dry run

**Input:** `points = [[0,0],[2,2],[3,10],[5,2],[7,0]]` (call them A, B, C, D, E).

```
All 10 edges with Manhattan weights:
A-B=4, A-C=13, A-D=7, A-E=7, B-C=9, B-D=3, B-E=7, C-D=10, C-E=14, D-E=4
Sorted: (B,D,3) (A,B,4) (D,E,4) (A,D,7) (A,E,7) (B,E,7) (B,C,9) (C,D,10) (A,C,13) (C,E,14)

(B,D,3): find(B)!=find(D) -> union, cost=3.   components: {B,D} {A} {C} {E}
(A,B,4): different -> union, cost=7.          components: {A,B,D} {C} {E}
(D,E,4): different -> union, cost=11.         components: {A,B,D,E} {C}
(A,D,7): same component (A and D both in {A,B,D,E}) -> skip (would form a cycle)
(A,E,7): same component -> skip
(B,E,7): same component -> skip
(B,C,9): different -> union, cost=20.         components: {A,B,C,D,E}  (all connected!)
(C,D,10): same component -> skip  (and every remaining edge skips)
taken = 4 = n-1 -> cost = 3 + 4 + 4 + 9 = 20 ✓
```

The accepted edges — `B-D`, `A-B`, `D-E`, `B-C` — are exactly the official example's edge set, and the two "skip" lines are the moment where a naive "take everything cheap" would have created a cycle. That's the whole point of the `find != find` check.

## Complexity

**Time.** Generating all edges is $O(n^2)$; sorting dominates; each union-find op is $\alpha(n)$:

$$
T(n) = O(n^2 \log n)
$$

**Space.** The edge list:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Prim's variant** (`src/main/kotlin/tree/mst/`) — the repo ships both; on a *complete* graph Prim is $O(n^2)$ with $O(n)$ space, beating Kruskal's sort. Say this when asked "can we do better?"
- **Union-Find alone** — the engine appears in dynamic-connectivity problems (`src/main/kotlin/graph/dynamic_connectivity/`, `src/main/kotlin/disjointset/`): Number of Provinces, redundant connection, number of islands via union.
- **Minimum Cost To Reach Destination With Special Roads / short paths on point sets** — same "complete graph of points" trick with a different objective.
- **Interview follow-up:** "Why does skipping same-component edges keep the result minimal?" Because any edge within a component would complete a cycle, and removing it (keeping the component's tree edges) never increases cost — so optimal solutions never need it. The cut property guarantees the *cheapest* cross-component edge is always safe to take, which is exactly what the sorted scan does.
