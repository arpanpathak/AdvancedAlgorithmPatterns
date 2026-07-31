# Chapter 3 — Arrays, Two Pointers & Matrices

> **Source:** `src/main/kotlin/array/`, `src/main/kotlin/sliding_window/`, `src/main/kotlin/grid/`
>
> **Master idea:** arrays reward *structure*. Two pointers exploit sortedness; matrices reward treating them as layered sequences. Almost every problem here is a loop with a **clever index dance**.
>
> **Prerequisites:** nothing but loops — this chapter is where the fundamentals get sharpened.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 3.1 | Two Sum II (sorted) | two pointers converge | $O(n)$ | [→](two-sum-ii.md) |
| 3.2 | Three Sum | sort + two pointers | $O(n^2)$ | [→](three-sum.md) |
| 3.3 | Move Zeroes | partition (write pointer) | $O(n)$ | [→](move-zeroes.md) |
| 3.4 | Merge Intervals | sort + linear sweep | $O(n \log n)$ | [→](merge-intervals.md) |
| 3.5 | Insert Interval | three-phase sweep | $O(n)$ | [→](insert-interval.md) |
| 3.6 | Rotate Image | transpose + reverse | $O(n^2)$ | [→](rotate-image.md) |
| 3.7 | Spiral Matrix | boundary peeling | $O(mn)$ | [→](spiral-matrix.md) |

| 3.8 | Trapping Rain Water | two pointers + running maxima | $O(n)$ | [→](trapping-rain-water.md) |
| 3.9 | Container With Most Water | two pointers, move the shorter | $O(n)$ | [→](container-with-most-water.md) |
| 3.10 | Product Of Array Except Self | prefix/suffix products | $O(n)$ | [→](product-of-array-except-self.md) |
| 3.11 | Merge Sorted Array | reverse two-pointer merge | $O(m+n)$ | [→](merge-sorted-array.md) |
| 3.12 | Set Matrix Zeroes | first-row/col markers | $O(mn)$ | [→](set-matrix-zeroes.md) |
| 3.13 | Rotate Array | triple reverse | $O(n)$ | [→](rotate-array.md) |
| 3.14 | Squares Of A Sorted Array | two pointers from the ends | $O(n)$ | [→](squares-of-a-sorted-array.md) |
| 3.15 | Find Pivot Index | running prefix vs total | $O(n)$ | [→](find-pivot-index.md) |
## The rest of the array/ directory

`src/main/kotlin/array/` holds 100+ more problems. The full index lives in the repository; every subdirectory is a pattern family:

- `twopointer/` — Two Sum II, 4Sum, Trapping Rain Water, Remove Duplicates, Rotate Array, Longest Mountain…
- `hashtable/` — First Missing Positive, Longest Consecutive Sequence, Valid Sudoku, Degree of an Array…
- `dp/` — House Robber, Kadane, Coin Change, Maximal Square, Stone Game… (see [Chapter 2](../ch02-dynamic-programming/index.md))
- `greedy/` — Split Array Largest Sum, Minimum Number of Taps…
- `prefixsum/`, `sweepline/`, `sorting/`, `random/`, `backtracking/` (see [Chapter 11](../ch12-backtracking/index.md))
- top-level matrix files — Spiral Matrix, Diagonal Traverse, Set Matrix Zeroes, Toeplitz, Transpose…

New pages are added to this chapter as they're written — the tree grows from the "Problems at a glance" table.
