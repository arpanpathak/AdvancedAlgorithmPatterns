# Appendix: The Roadmap — Complete

> The repo once held **91 files** without a dedicated page. This edition closed that gap: **every file** in [`src/main/kotlin/`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/tree/main/src/main/kotlin) is now either covered by a full page, documented as a variant/alternative implementation of one, or marked as support/non-problem — see the [coverage index](appendix-repo-coverage-index.md) for the one-to-one mapping.

## What this edition added

The previous roadmap listed the 91 uncovered files by family. Here is where each family landed:

### New full pages (6)

- **Dynamic Connectivity** ([6.35](ch06-graphs/dynamic-connectivity.md)) — `DynamicConnectivity.kt`; the reverse-time DSU ("undo removals by running time backwards") from the Union-Find family.
- **Apply Substitutions** ([9.39](ch09-strings/apply-substitutions.md)) — `graph/topological_sort/ApplySubstitutions.kt` (+ the `string/` recursive sibling); placeholder resolution as a dependency graph + Kahn's algorithm.
- **KD-Tree** ([14.10](ch14-sorting/kd-tree.md)) — `geo/kdtree/KDTreeExample.kt`; axis-alternating BST with nearest-neighbor pruning.
- **How Many Rectangles Overlap** ([17.19](ch17-advanced-graphs/how-many-rectangles-overlap.md)) — `HowManyRectanglesOverlapSweepLine.kt` + `HowManyRectangleOverlapsIntervalTree.kt` + `RectangeOverlapCountTreeSet.kt`; sweep line + BST range query.
- **Streamer Leaderboard** ([18.25](ch18-design-caches/streamer-leaderboard.md)) — `tree/bst/StreamerRanking.kt`; score-keyed TreeMap of ID sets.
- **Serialize And Deserialize N-ary Tree** ([5.36](ch05-trees/serialize-and-deserialize-n-ary-tree.md)) — `tree/SerializeAndDeserializeNArrayTree.kt`; preorder with child-count encoding.

### Folded in as variants of existing pages (the biggest batch)

The remaining ~85 files turned out to be **alternative/duplicate implementations** of problems the book already teaches. Highlights:

- **`NQueen*.kt`, `Permutation_II_*.kt`, `Combinations.kt`** → [12.4](ch12-backtracking/n-queens.md), [12.11](ch12-backtracking/permutations-ii.md), [12.12](ch12-backtracking/combinations.md) — the backtracking chapter now documents the Narayana Pandita and backtracking spellings side by side.
- **`LFUCache.kt`, `LFUCacheGigaCHAD.kt`, `LRUCache.kt`, `LRUCacheBetter.kt`, `LRUCleanAf.kt`, `LruCacheBruceLee.kt`, …** → [18.1](ch18-design-caches/lru-cache.md) / [18.2](ch18-design-caches/lfu-cache.md) / [18.9](ch18-design-caches/lru-cache-variants.md) — the seven-implementations page already tells that story.
- **`Kosaraju.kt`, `Tarjans.kt`** → [6.7](ch06-graphs/strongly-connected-components.md) — both SCC algorithms documented on one page.
- **`EvalualteDivisions.kt`, `SurroundedRegion.kt`, `AestroidCollisions.kt`, `Racecar.kt`, `Sqrt.kt`, `KadensAlgorithm.kt`, `PascalsTriangle.kt`, `HIndex.kt`, `FindingMKAverage.kt`, `IsBipartileBFSFunctional.kt`, `NColoringGreedy.kt`, …** → each maps to its existing page (see the coverage index).
- **`TravellingSales*` trio, `EdmondsKarp*` quartet, `CherryPickup_II.kt`, `MakingALargeIsland_AnotherApproach.kt`, `SurroundedRegionDfs.kt`, `BurstBallonsClean.kt`** → variants of the corresponding held-karp / max-flow / DP / grid pages.

### Marked as support / non-problem

`GenerateReadme.kt`, `Main.kt`, `practice.kt`, `Theory.kt` (euler circuit), `ProteinFolding.kt` (unfinished scratch), the `GoogleCheatSheet*` files, `SelfDoubtSimulation.kt`, `DuckworthLewisStern.kt`, the `real_word_projects/` samples, etc. — these are notes or helpers, not interview problems, and the coverage index says so.

## If you keep expanding

The book's natural next chapters, should the repo grow:

1. **A dedicated Range Query chapter** — `SegmentTree.kt`, `IterativeSegmentTree.kt`, `DynamicSegmentTree.kt`, `FenwickTree.kt`, `OrderedStatisticsTree.kt` deserve more than [14.8](ch14-sorting/segment-tree-and-fenwick.md)'s survey page.
2. **Expression machinery** — `BasicCalculator_III.kt`, `StringToIntegerAtoi.kt`, `ValidNumber.kt` share a parsing state-machine engine worth a joint treatment.
3. **Geometry** — `CountNumberOfTrapizoids_I.kt`, `FindingNumberOfVisibleMountains.kt`, `SeperateSquares_I.kt` are the least-covered corner of the repo.

> The promise of the coverage index stands: every file in the repo is accounted for — by a page, as a variant, or as support.
