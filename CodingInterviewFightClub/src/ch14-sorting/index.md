# Chapter 14 — Sorting & QuickSelect

> **Source:** `src/main/kotlin/sorting/` and `src/main/kotlin/quicksort/`
>
> **Master idea:** sorting is *preprocessing that buys structure* — after a sort, adjacency means "next in order", and every comparison-based algorithm's cost is set by it. This chapter pairs the classic **merge sort** with **quickselect** (the "sort only enough" answer) and the **custom-comparator** problems where "sorted" is redefined.
>
> **Prerequisites:** recursion, arrays, and the heaps from [Chapter 7](../ch07-heaps/index.md) — the top-k problems have both a heap answer and a quickselect answer.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 14.1 | Merge Sort | divide + merge | $O(n \log n)$ | [→](merge-sort.md) |
| 14.2 | Kth Largest Element | randomized quickselect | $O(n)$ avg | [→](kth-largest-element.md) |
| 14.3 | K Closest Points To Origin | quickselect on distance | $O(n)$ avg | [→](k-closest-points-to-origin.md) |
| 14.4 | Largest Number | custom comparator | $O(n \log n)$ | [→](largest-number.md) |
| 14.5 | H-Index | sort + scan | $O(n \log n)$ | [→](h-index.md) |
| 14.6 | Russian Doll Envelopes | sort + LIS | $O(n \log n)$ | [→](russian-doll-envelopes.md) |
| 14.7 | Top K Frequent (QuickSelect) | quickselect on frequency | $O(n)$ avg | [→](top-k-frequent-elements-quickselect.md) |

| 14.8 | Sort Colors | Dutch National Flag | $O(n)$ | [→](sort-colors.md) |
| 14.8 | Segment Tree & Fenwick | range-query engines | $O(log n)$ | [→](segment-tree-and-fenwick.md) |
| 14.9 | Count Of Smaller Numbers After Self | compression + Fenwick | $O(n log n)$ | [→](count-of-smaller-numbers-after-self.md) |
## The rest of the sorting/ and quicksort/ directories

`sorting/` also holds `EmployeeFreeTime.kt` and `RankTeamsByVote.kt`; `quicksort/` adds `DualPivotQuickSelect.kt` and `GenericRanrmoizedQuickSelect.kt` (generalized quickselect). The quickselect problems cross-reference the heap versions in [Chapter 7](../ch07-heaps/index.md) — the two answers to the same "top k" question, contrasted.

New pages are appended to the table above as they're written.
