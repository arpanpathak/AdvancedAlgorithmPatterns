# Chapter 6 — Graphs

> **Source:** `src/main/kotlin/graph/` (the biggest folder in the codebase)
>
> **Master idea:** a graph is *vertices + edges* — and every problem is one of a handful of engines (BFS, DFS, topological sort, Union-Find, Dijkstra, MST, SCC) started with the right fuel. Trees from [Chapter 5](../ch05-trees/index.md) are just graphs with no cycles and one connected component.
>
> **Prerequisites:** recursion, the level-fencing BFS from [5.2](../ch05-trees/binary-tree-level-order-traversal.md), and a willingness to think in *states*, not nodes.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 6.1 | Word Ladder | BFS on an implicit graph | $O(n \cdot L \cdot 26)$ | [→](word-ladder.md) |
| 6.2 | Clone Graph | DFS + memo map | $O(V+E)$ | [→](clone-graph.md) |
| 6.3 | Course Schedule II | Kahn's topological sort | $O(V+E)$ | [→](course-schedule-ii.md) |
| 6.4 | Is Graph Bipartite | BFS 2-coloring | $O(V+E)$ | [→](is-graph-bipartite.md) |
| 6.5 | Cheapest Flights With K Stops | Dijkstra + stop budget | $O(E \log E)$ | [→](cheapest-flights-with-k-stops.md) |
| 6.6 | Min Cost To Connect All Points | Kruskal MST + Union-Find | $O(n^2 \log n)$ | [→](min-cost-to-connect-all-points.md) |
| 6.7 | Strongly Connected Components | Kosaraju (two DFS passes) | $O(V+E)$ | [→](strongly-connected-components.md) |

| 6.8 | Alien Dictionary | DFS topo with cycle detection | $O(V+E)$ | [→](alien-dictionary.md) |
| 6.9 | Redundant Connection | Union-Find cycle detection | $O(E α(n))$ | [→](redundant-connection.md) |
| 6.10 | Flood Fill | grid DFS/BFS | $O(mn)$ | [→](flood-fill.md) |
| 6.11 | The Earliest Moment Everyone Became Friends | DSU with a components counter | $O(E α(n))$ | [→](the-earliest-moment-everyone-became-friends.md) |
| 6.12 | Network Delay Time | pure Dijkstra | $O(E log V)$ | [→](network-delay-time.md) |
| 6.13 | Word Ladder II | BFS distances + DFS paths | $O(26Ln)$ | [→](word-ladder-ii.md) |
| 6.14 | Rotting Oranges | multi-source BFS | $O(mn)$ | [→](rotting-oranges.md) |
| 6.15 | Accounts Merge | Union-Find over emails | $O(Eα)$ | [→](accounts-merge.md) |
| 6.16 | Surrounded Regions | border BFS marking | $O(mn)$ | [→](surrounded-regions.md) |
| 6.17 | Max Area Of Island | sink-and-count DFS | $O(mn)$ | [→](max-area-of-island.md) |
| 6.18 | Pacific Atlantic Water Flow | reverse-flow BFS | $O(mn)$ | [→](pacific-atlantic-water-flow.md) |
| 6.19 | Find Length Of Longest Cycle | 3-color DFS + distances | $O(n)$ | [→](find-length-of-longest-cycle.md) |
| 6.20 | Making A Large Island | island IDs + neighbor sum | $O(n^2)$ | [→](making-a-large-island.md) |
| 6.21 | Number Of Islands II | online Union-Find | $O(kα)$ | [→](number-of-islands-ii.md) |
| 6.22 | Island Perimeter | exposed-edge count | $O(mn)$ | [→](island-perimeter.md) |
| 6.23 | N-Coloring Greedy | greedy vertex coloring | $O(V+E)$ | [→](n-coloring-greedy.md) |
| 6.25 | Sliding Puzzle | board-state BFS | $O(6!)$ | [→](sliding-puzzle.md) |
| 6.26 | Shortest Bridge | DFS + multi-source BFS | $O(n^2)$ | [→](shortest-bridge.md) |
| 6.27 | The Maze III | Dijkstra with lexicographic paths | $O(mn log mn)$ | [→](the-maze-iii.md) |
| 6.28 | Optimize Water Distribution | MST + virtual node | $O((n+e)log n)$ | [→](optimize-water-distribution.md) |
| 6.29 | Cracking The Safe | de Bruijn / Hierholzer | $O(k^n)$ | [→](cracking-the-safe.md) |
| 6.30 | Shortest Distance From All Buildings | multi-source BFS | $O(BRC)$ | [→](shortest-distance-from-all-buildings.md) |
| 6.31 | Shortest Path With Obstacles Elimination | BFS over (r,c,k) | $O(RCk)$ | [→](shortest-path-in-grid-with-obstacles-elimination.md) |
| 6.32 | Maximum Path Quality | budgeted DFS | $O(2^T)$ | [→](maximum-path-quality-of-a-graph.md) |
| 6.33 | Path With Maximum Probability | max-Dijkstra | $O(e log n)$ | [→](path-with-maximum-probability.md) |
| 6.34 | Longest Increasing Path In A Matrix | memoized grid DFS | $O(mn)$ | [→](longest-increasing-path-in-a-matrix.md) |
## The rest of the graph/ directory

`src/main/kotlin/graph/` is enormous: `topological_sort/` (Course Schedule I/II, Parallel Courses), `scc/` (Kosaraju), `mst/` (Kruskal & Prim on points), `flow_network/` (Edmonds-Karp max flow), `tsp/` (Travelling Salesman via Held-Karp), `euler/` (Cracking The Safe), `articulation_point/`, `cycle/`, `components/`, `dag/`, `dp/`, `greedy/`, plus standalone classics — Word Ladder II, Clone Graph, Bipartite (BFS/DFS variants), Bus Routes, Evaluate Division, Reorder Routes, Minimum Genetic Mutations, Maximum Path Quality, N-Coloring, Chromatic Number, Graph Diameter, House Robber III, and more.

New pages are appended to the table above as they're written.
