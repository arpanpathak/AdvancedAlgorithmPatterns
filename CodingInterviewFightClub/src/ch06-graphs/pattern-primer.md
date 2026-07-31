# 6.0 Pattern Primer — The Seven Engines

A **graph** is a set of *vertices* $V$ and *edges* $E$ between them. A tree is a graph with no cycles and one component — so everything in [Chapter 5](../ch05-trees/index.md) is a special case. But graphs add three new freedoms that trees forbid:

1. **Cycles** — a node can be reached through many paths, so *visited* tracking is mandatory (a tree's structure made it redundant).
2. **Multiple components** — you must loop over every start vertex, not just one root.
3. **Weighted / directed edges** — "shortest" now has a real cost, and reachability is directional.

This chapter is organized around **seven engines**. Almost every graph interview problem is "pick the right engine, feed it the right representation."

| # | Engine | Answers "…" | Cost | Used by |
|---|--------|-------------|------|---------|
| 1 | BFS (unweighted) | shortest *hops*; level grouping | $O(V+E)$ | 6.1, 6.2 (BFS variant), 6.4 |
| 2 | DFS | existence, reachability, cloning, finish-order | $O(V+E)$ | 6.2, 6.7 |
| 3 | Topological sort | valid order of a DAG; cycle detection | $O(V+E)$ | 6.3 |
| 4 | 2-coloring (BFS/DFS) | bipartiteness | $O(V+E)$ | 6.4 |
| 5 | Dijkstra (+ variants) | cheapest path with edge weights | $O(E \log V)$ | 6.5 |
| 6 | Union-Find | dynamic connectivity; Kruskal MST | $O(\alpha(V))$ / op | 6.6 |
| 7 | SCC (Kosaraju / Tarjan) | strongly connected groups | $O(V+E)$ | 6.7 |

## Representation: adjacency list vs matrix

- **Adjacency list** — `Map<V, List<V>>` or `Array<MutableList<Int>>`: $O(V+E)$ memory, iterate a vertex's neighbors in $O(\deg)$ — **almost always the right default**. Every page in this chapter uses it.
- **Adjacency matrix** — $V \times V$ boolean/cost table: $O(V^2)$ memory, $O(1)$ edge lookup. Only wins for dense graphs where you repeatedly ask "is there an edge?" (e.g., Floyd-Warshall).

## BFS vs DFS — the one-decision fork

- **BFS** (queue) — explores in *layers*: gives shortest hop-distance, groups by level, and its "first time you see a vertex is its shortest distance" property is the engine behind 6.1.
- **DFS** (stack/recursion) — dives deep first: natural for cloning, path enumeration, and anything where you process children *before* the parent (the post-order from [5.0](../ch05-trees/pattern-primer.md) reappears in 6.7's finish-order).

Both need a **visited** set — but "visited" is often really a *distance* array or a *color* array, and choosing which is the second decision. A plain `Boolean` says "seen"; an `IntArray` of distances says "seen, and here's how far"; a color array says "seen, and here's which side I'm on" (6.4).

## Think in states, not nodes

The biggest upgrade this chapter teaches: vertices are often not what you push onto the queue. The unit of work is a **state** — `(vertex, extra_info)`:

- Word Ladder pushes `(word, level)` — the level is the answer.
- Cheapest Flights pushes `(city, cost, stops)` — two pieces of extra info.

The extra info is usually what the problem is *about* (distance, remaining budget). If your queue stores only nodes, the problem's answer is hiding somewhere you can't see — that's the signal you're missing a state.

## Implicit graphs

Sometimes the graph is never materialized: vertices are *things* (words in 6.1), and edges are generated on the fly ("differ by one letter"). Building the graph explicitly would cost $O(n^2)$; generating neighbors lazily costs a tiny factor per vertex and keeps memory at $O(n)$. Whenever you see "connection defined by a rule," suspect an implicit graph.

## Complexity intuition

Every traversal engine costs $O(V+E)$: each vertex starts a constant amount of work (queue/stack ops, neighbor iteration) that sums to $O(E)$ across all vertices. Weighted engines add a $\log$ for the priority queue; sorting-based engines (Kruskal) pay $O(E \log E)$ once. Union-Find amortizes to *nearly constant* — $\alpha(V)$ is the inverse Ackermann function, effectively $\le 4$ for any input you'll ever see. State these bounds per-problem — the "which $V$ and $E$ are we trading?" question is half the interview.
