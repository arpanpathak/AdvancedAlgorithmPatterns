# Chapter 17 — Advanced Graphs

> **Source:** `src/main/kotlin/graph/flow_network/`, `src/main/kotlin/graph/tsp/`, `src/main/kotlin/tree/mst/`, and the `graph/` root
>
> **Master idea:** beyond BFS/DFS (Chapter 6) lie the *optimization* graph problems: **flow networks** (how much can travel through a capacitated graph?), **bipartite matching** (assignments with conflicts), **minimum spanning trees** (connect everything cheaply), **TSP** (visit everything optimally), and the **state-space BFS** tricks (bitmask states, edge weights as graph labels).
>
> **Prerequisites:** BFS/DFS from [Chapter 6](../ch06-graphs/index.md), DP from [Chapter 2](../ch02-dynamic-programming/index.md) (Held-Karp), bitmasks from [Chapter 16](../ch16-bit-manipulation/index.md), and heaps from [Chapter 7](../ch07-heaps/index.md) (Prim's).

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 17.1 | Max Flow (Edmonds-Karp) | BFS augmenting paths | $O(VE^2)$ | [→](max-flow-edmonds-karp.md) |
| 17.2 | Maximum Bipartite Matching | Kuhn's augmenting path | $O(VE)$ | [→](maximum-bipartite-matching.md) |
| 17.3 | Min Cost To Connect All Points | Prim's MST | $O(n^2 \log n)$ | [→](min-cost-to-connect-all-points-prims.md) |
| 17.4 | Travelling Salesman (Held-Karp) | bitmask DP | $O(n^2 2^n)$ | [→](travelling-salesman-held-karp.md) |
| 17.5 | Shortest Path Visiting All Nodes | BFS over bitmask states | $O(n \cdot 2^n)$ | [→](shortest-path-visiting-all-nodes.md) |
| 17.6 | Reorder Routes To City Zero | directed-edge DFS | $O(n)$ | [→](reorder-routes-to-make-all-paths-lead-to-city-zero.md) |
| 17.7 | Evaluate Division | edge-labeled graph BFS | $O(Q \cdot E)$ | [→](evaluate-division.md) |

## The rest of the graph/ directories

`flow_network/` also holds several Edmonds-Karp variants and `BipartileMatching.kt` (the same Kuhn's algorithm as 17.2). `tsp/` adds `ShortestPathVisitingAllNodes.kt` (17.5), the brute-force and top-down TSP versions, and `TravellingSalesmanRecursiveDP.kt`. `tree/mst/` adds the Kruskal version of 17.3 ([6.6](../ch06-graphs/min-cost-to-connect-all-points.md) already covers it). `graph/` also has articulation points, SCC, topological sorts, chromatic number, and more.

New pages are appended to the table above as they're written.
