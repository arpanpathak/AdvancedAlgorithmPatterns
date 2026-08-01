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
| 3.16 | Shuffle An Array | Fisher–Yates in place | $O(n)$ | [→](shuffle-an-array.md) |
| 3.17 | Convex Hull (Erect The Fence) | Andrew's monotone chain | $O(n log n)$ | [→](convex-hull.md) |
| 3.18 | Remove Duplicates From Sorted Array | write-pointer dedupe | $O(n)$ | [→](remove-duplicates-from-sorted-array.md) |
| 3.19 | Pascal's Triangle | build rows from the previous | $O(n^2)$ | [→](pascals-triangle.md) |
| 3.20 | Robot Bounded In Circle | direction-state simulation | $O(n)$ | [→](robot-bounded-in-circle.md) |
| 3.21 | Increasing Triplet Subsequence | two running minima | $O(n)$ | [→](increasing-triplet-subsequence.md) |
| 3.22 | Diagonal Traverse | direction-flipping walker | $O(mn)$ | [→](diagonal-traverse.md) |
| 3.23 | Find The Highest Altitude | running prefix max | $O(n)$ | [→](find-the-highest-altitude.md) |
| 3.24 | Interval List Intersections | two-pointer overlap | $O(n+m)$ | [→](interval-list-intersections.md) |
| 3.25 | Plus One | digit carry | $O(n)$ | [→](plus-one.md) |
| 3.26 | Reverse Integer | overflow pre-check | $O(log x)$ | [→](reverse-integer.md) |
| 3.27 | Toeplitz Matrix | diagonal neighbor check | $O(mn)$ | [→](toeplitz-matrix.md) |
| 3.28 | Transpose Matrix | index swap | $O(mn)$ | [→](transpose-matrix.md) |
| 3.29 | Missing Ranges | gap scanning | $O(n)$ | [→](missing-ranges.md) |
| 3.30 | Rectangle Area | inclusion-exclusion | $O(1)$ | [→](rectangle-area.md) |
| 3.31 | Rectangle Overlap | axis-separation | $O(1)$ | [→](rectangle-overlap.md) |
| 3.32 | Zero Array Transformation | difference array | $O(n+q)$ | [→](zero-array-transformation.md) |
| 3.33 | Rectangle Area II | coordinate compression sweep | $O(r^2 log r)$ | [→](rectangle-area-ii.md) |
| 3.34 | Spiral Matrix II | boundary-filling walk | $O(n^2)$ | [→](spiral-matrix-ii.md) |
| 3.35 | Remove Duplicates II | write pointer + run counter | $O(n)$ | [→](remove-duplicates-from-sorted-array-ii.md) |
| 3.36 | Remove Element | write-pointer filter | $O(n)$ | [→](remove-element.md) |
| 3.37 | 4Sum | k-sum recursion | $O(n^3)$ | [→](4-sum.md) |
| 3.38 | Three Sum Closest | two-pointer closest | $O(n^2)$ | [→](three-sum-closest.md) |
| 3.39 | Sign Of The Product | sign counting | $O(n)$ | [→](sign-of-the-product-of-an-array.md) |
| 3.41 | Longest Mountain In Array | peak expansion | $O(n)$ | [→](longest-mountain-in-array.md) |
| 3.42 | Check If Array Is Sorted And Rotated | descent count | $O(n)$ | [→](check-if-array-is-sorted-and-rotated.md) |
| 3.43 | Degree Of An Array | first/count/last maps | $O(n)$ | [→](degree-of-an-array.md) |
| 3.44 | Find Difference Of Two Arrays | set subtraction | $O(n+m)$ | [→](find-difference-of-two-arrays.md) |
| 3.45 | Number Of Good Pairs | running-frequency sum | $O(n)$ | [→](number-of-good-pairs.md) |
| 3.46 | Unique Number Of Occurrences | frequency-set | $O(n)$ | [→](unique-number-of-occurrences.md) |
| 3.47 | Divide Array Into Equal Pairs | even-frequency test | $O(n)$ | [→](divide-array-into-equal-pairs.md) |
| 3.48 | Maximum Distance In Arrays | running extremes | $O(k)$ | [→](maximum-distance-in-arrays.md) |
| 3.49 | K Items With Maximum Sum | greedy pick order | $O(1)$ | [→](k-items-with-maximum-sum.md) |
| 3.50 | Minimum Operations To Move All Balls | two-pass cost | $O(n)$ | [→](minimum-operations-to-move-all-balls.md) |
| 3.51 | Maximum Population Year | difference sweep | $O(1)$ | [→](maximum-population-year.md) |
## The rest of the array/ directory

`src/main/kotlin/array/` holds 100+ more problems. The full index lives in the repository; every subdirectory is a pattern family:

- `twopointer/` — Two Sum II, 4Sum, Trapping Rain Water, Remove Duplicates, Rotate Array, Longest Mountain…
- `hashtable/` — First Missing Positive, Longest Consecutive Sequence, Valid Sudoku, Degree of an Array…
- `dp/` — House Robber, Kadane, Coin Change, Maximal Square, Stone Game… (see [Chapter 2](../ch02-dynamic-programming/index.md))
- `greedy/` — Split Array Largest Sum, Minimum Number of Taps…
- `prefixsum/`, `sweepline/`, `sorting/`, `random/`, `backtracking/` (see [Chapter 11](../ch12-backtracking/index.md))
- top-level matrix files — Spiral Matrix, Diagonal Traverse, Set Matrix Zeroes, Toeplitz, Transpose…

New pages are added to this chapter as they're written — the tree grows from the "Problems at a glance" table.
