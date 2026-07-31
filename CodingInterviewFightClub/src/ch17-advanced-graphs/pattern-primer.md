# 17.0 Pattern Primer — Flow, Matching, MST, and State-Space BFS

Chapter 6 solved *reachability*; this chapter solves *optimization on graphs*. Four engines:

## The augmenting-path idea (flow + matching)

Both max flow and bipartite matching run on one mechanism: **find a path that increases the result, push flow / reassign along it, repeat until none exists.**

- **Flow** ([17.1](max-flow-edmonds-karp.md)): the residual graph (forward edges = unused capacity, backward edges = "undo" capacity). BFS finds a shortest augmenting path; the bottleneck is subtracted forward and *added backward* — the backward edge is the undo that makes the greedy correct. Edmonds-Karp = BFS-chosen paths → $O(VE^2)$.
- **Matching** ([17.2](maximum-bipartite-matching.md)): for each left node, try to match it; if its worker is taken, recursively try to *reassign* the displaced job — the augmenting path of reassignments. No flow machinery needed for bipartite graphs; Kuhn's is the direct version.

## The MST cut property (Prim / Kruskal)

**"Take the cheapest edge crossing any cut"** — that's the MST. Prim grows one tree by always adding the cheapest edge from the tree to the outside (a min-heap of frontier edges, [17.3](min-cost-to-connect-all-points-prims.md)); Kruskal sorts all edges and unions components ([6.6](../ch06-graphs/min-cost-to-connect-all-points.md)). Both are the same theorem, two growth strategies. The proof is an exchange argument: any MST can be rearranged to include the cheapest crossing edge without increasing weight.

## Bitmask DP over subsets (TSP)

"Visit all nodes optimally" has $n!$ orders — but only $2^n$ subsets. **Held-Karp** ([17.4](travelling-salesman-held-karp.md)): `dp[mask][city]` = cheapest way to have visited exactly `mask` and end at `city`; transitions add one unvisited city. The mask is the [Chapter 16](../ch16-bit-manipulation/index.md) bitmask used as a *set*. Cost $O(n^2 2^n)$ vs $O(n!)$ — the subset structure beats the permutation structure.

## State-space BFS

Some problems hide a *bigger state* than "node": [17.5](shortest-path-visiting-all-nodes.md) tracks `(node, visited-mask)` — the "visited set" is part of the BFS state; [17.6](reorder-routes-to-make-all-paths-lead-to-city-zero.md) and [17.7](evaluate-division.md) add *labels* to edges (direction, weight) that the traversal must interpret. The tell: *"visit all / ratio / direction"* → the state carries more than the node id.

## Complexity intuition

- Flow: $O(VE^2)$ (Edmonds-Karp), each BFS $O(E)$ and at most $O(VE)$ augmentations.
- Matching: $O(VE)$ (each of V left nodes runs a DFS over E edges).
- MST: $O(E \log V)$ (heap) or $O(n^2)$ (dense Prim).
- TSP bitmask DP: $O(n^2 2^n)$ — exponential in $n$, but *the* polynomial-vs-factorial win.
- State BFS: $O(\text{states})$ — count states as `nodes × possible-masks`.
