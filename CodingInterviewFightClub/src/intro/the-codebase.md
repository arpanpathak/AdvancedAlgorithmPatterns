# The 5-Language Codebase

The engine of this book is the Kotlin repository at `src/main/kotlin/`. Understanding how it is organized makes every page of this book more useful.

## Repository layout

```
src/main/kotlin/
├── array/            # arrays, two pointers, combinatorics, grid-adjacent array tricks
├── backtracking/     # permutations, combinations, subsets, constraint search
├── binarysearch/     # classic + exotic binary search (this book's Chapter 1)
├── bitset/           # bit manipulation
├── cache/            # LRU, LFU, thread-safe caches
├── disjointset/      # union-find / DSU
├── dynamic_programming/
├── graph/            # BFS, DFS, topological sort, SCC, flow, TSP, MST...
├── greedy/
├── grid/             # matrices, islands, A*
├── heap/
├── linkedlist/
├── math/
├── probability/      # reservoir sampling, randomized structures
├── quicksort/        # quickselect, top-k
├── sliding_window/
├── stack/
├── string/           # pattern matching, palindrome, trie-adjacent string tricks
├── tree/             # BST, segment tree, Fenwick tree, interval tree, N-ary
├── trie/
└── ...
```

## Chapter mapping

Every book chapter cites its source files at the top. For example, Chapter 1 cites `src/main/kotlin/binarysearch/KokoEatingBanana.kt` for the Koko section. This lets you:

- open the exact file the book is explaining,
- run it with your own test harness,
- diff your own solution against the repo's.

## How the multi-language code was produced

1. **Kotlin** — copied from the repository, verbatim (minor cosmetic cleanup only, e.g. removing the `package` line so each listing is self-contained).
2. **Java / C++ / Python / Rust** — fresh translations written for this book with the same algorithm, same complexity, same `@param`/`@return` contract, idiomatic to each language.

Where the repository file contains a second, alternative solution (e.g. `FindKClosestElements` ships both a binary-search and a heap solution), the book presents **both**, since interviews love follow-ups of exactly this shape.

## Typed Python

"Typed Python" means annotations are always present:

```python
def minEatingSpeed(piles: list[int], h: int) -> int:
```

This is what the book shows everywhere. It costs nothing at runtime and it documents the contract the same way the Kotlin/Java/C++/Rust signatures do.

## A note on the Kotlin sources

The repository is a living training log, not a release artifact — some files contain scratch notes, TODOs, or experiments. The book's Kotlin listings present the *essence* of each file (the solution + its comments), and the *Dry Run* sections always describe the exact code shown, not a hypothetical version.
