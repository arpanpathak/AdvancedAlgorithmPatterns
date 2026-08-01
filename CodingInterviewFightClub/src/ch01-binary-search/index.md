# Chapter 1 — Binary Search

> **Source:** `src/main/kotlin/binarysearch/` (22 problems)
>
> **Master idea:** every problem in this chapter is a different face of the same theorem — *if a predicate on a range is monotone, the boundary where it flips can be found in $O(\log n)$.*
>
> **Prerequisites:** the halving math in [Reference: Big-O & Complexity Math](../reference/complexity.md).

## Problems at a glance

| # | Problem | Core pattern | Complexity | Page |
|---|---------|--------------|------------|------|
| 1.1 | Koko Eating Bananas | binary search on the answer | $O(n \log R)$ | [→](koko-eating-bananas.md) |
| 1.2 | Capacity To Ship Packages Within D Days | binary search on the answer | $O(n \log S)$ | [→](capacity-to-ship-packages.md) |
| 1.3 | Find First And Last Position Of Target | lower/upper bound | $O(\log n)$ | [→](find-first-and-last-position.md) |
| 1.4 | Find K Closest Elements | binary search on the *start* index | $O(\log(n-k))$ | [→](find-k-closest-elements.md) |
| 1.5 | Find Minimum In Rotated Sorted Array | rotated-array pivot | $O(\log n)$ | [→](find-minimum-in-rotated-sorted-array.md) |
| 1.6 | Find Peak Element | monotone slope descent | $O(\log n)$ | [→](find-peak-element.md) |
| 1.7 | Find Peak Element (Safe Boundaries) | same, boundary-safe | $O(\log n)$ | [→](find-peak-element-safe.md) |
| 1.8 | First Bad Version | lower bound (API predicate) | $O(\log n)$ | [→](first-bad-version.md) |
| 1.9 | Guess Number Higher Or Lower | exact-match ternary response | $O(\log n)$ | [→](guess-number-higher-or-lower.md) |
| 1.10 | House Robber IV | binary search on the answer | $O(n \log V)$ | [→](house-robber-iv.md) |
| 1.11 | Kth Missing Positive Number | index-space counting | $O(\log n)$ | [→](kth-missing-positive-number.md) |
| 1.12 | Median Of Two Sorted Arrays | partition-based search | $O(\log \min(m,n))$ | [→](median-of-two-sorted-arrays.md) |
| 1.13 | Peak Index In A Mountain Array | monotone slope descent | $O(\log n)$ | [→](peak-index-in-mountain-array.md) |
| 1.14 | Random Pick With Weight | prefix sums + binary search | $O(\log n)$ pick | [→](random-pick-with-weight.md) |
| 1.15 | Search A 2D Matrix | index unrolling | $O(\log(mn))$ | [→](search-a-2d-matrix.md) |
| 1.16 | Search In Rotated Sorted Array II | rotated search + duplicate dedup | $O(\log n)$ avg | [→](search-in-rotated-sorted-array-ii.md) |
| 1.17 | Search In Rotated Sorted Array | rotated search | $O(\log n)$ | [→](search-in-rotated-sorted-array.md) |
| 1.18 | Search Insert Position | lower bound | $O(\log n)$ | [→](search-insert-position.md) |
| 1.19 | Single Element In A Sorted Array | parity-based search | $O(\log n)$ | [→](single-element-in-a-sorted-array.md) |
| 1.20 | Valley Element | monotone slope descent (mirror) | $O(\log n)$ | [→](valley-element.md) |
| 1.21 | Apartment Hunting | binary search + nearest neighbor | $O(BR \log K)$ | [→](apartment-hunting.md) |
| 1.22 | Closest Subsequence Sum | meet-in-the-middle + binary search | $O(2^{n/2} \log 2^{n/2})$ | [→](../ch12-backtracking/closest-subsequence-sum.md) |

## Reading order

Work through the sections in order *the first time*: 1.0 → 1.1 → 1.2 → 1.8 → 1.5 → 1.17 → 1.6 → 1.12. That path covers every sub-pattern (answer-space search, lower bound, rotated arrays, slope descent, partition search) with the minimum number of pages. The rest are variants and gyms that cement the same five moves.

Afterwards, close the book and try to derive 1.12's partition condition from scratch — if you can, you own this chapter.
