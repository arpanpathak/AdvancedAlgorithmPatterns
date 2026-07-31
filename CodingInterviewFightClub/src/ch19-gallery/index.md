# Chapter 19 — Variant Gallery

> The repo's **duplicate implementations are the syllabus**. Every "second take" on a problem is a different *costume* of the same algorithm — this chapter captures the coolest ones with their actual code, so the coverage index's "variant of" entries become readable pages.

| # | Problem | The variant captured | Complexity | Page |
|---|---|---|---|---|
| 19.1 | Travelling Salesman | top-down functional bitmask DP (vs Held-Karp) | $O(n^2 2^n)$ | [→](travelling-salesman-top-down.md) |
| 19.2 | LRU Cache | the hand-rolled doubly-linked list family | $O(1)$ | [→](lru-cache-linked-list-family.md) |
| 19.3 | Coin Change | BFS + top-down + counting (four engines) | $O(AC)$ | [→](coin-change-family.md) |
| 19.4 | N-Queens | set-based diagonal marking (O(1) safety) | $O(n!)$ | [→](n-queen-family.md) |
| 19.5 | Shorter/Better gallery | next-permutation ext, calc II, IP validate, peak, bridges | mixed | [→](short-code-gallery.md) |
| 19.6 | Eulerian family | circuit / arrangement / de Bruijn safe | $O(V+E)$ | [→](eulerian-family.md) |
| 19.7 | Functional style | Kahn, bipartite, trie, word squares, bitmask DP | mixed | [→](functional-programming-gallery.md) |
| 19.8 | Word Ladder II | forward BFS with same-distance admission | $O(26Ln)$ | [→](word-ladder-ii-family.md) |
| 19.9 | LFU Cache | three-map lean + canonical buckets | $O(1)$ | [→](lfu-cache-family.md) |
| 19.10 | Sliding Window Median | index-based TreeSet (duplicate-proof) | $O(n log k)$ | [→](sliding-window-median-treeset.md) |

| 19.11 | Kahn's Filter-Seed & DFS-Expression | queue seed via `filter`, cycle check as `when` | $O(V+E)$ | [→](kahns-filter-seed.md) |
| 19.12 | One-Expression DP Gallery | FrogJump/StoneGame/CoinChange as `getOrPut` | mixed | [→](one-expression-dp-gallery.md) |
| 19.13 | Map-Key Gallery | `List<Int>` anagram key, getOrPut counts, quickselect | mixed | [→](map-key-gallery.md) |
| 19.14 | Structure Gallery | coordinate compression, histogram stack, `buildList` | mixed | [→](structure-gallery.md) |
## The rest of the variant story

The [Repo Coverage Index](../appendix-repo-coverage-index.md) maps every file in `src/main/kotlin` — the entries labeled "variant of" point here or at their main chapter page. The [Roadmap](../appendix-roadmap.md) lists what still deserves full pages.

New pages are appended to the table above as they're written.
