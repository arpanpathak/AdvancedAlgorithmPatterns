# Chapter 15 — Sliding Window

> **Source:** `src/main/kotlin/sliding_window/`
>
> **Master idea:** a sliding window is a *contiguous subarray/substring view* that moves one step at a time — instead of recomputing the answer for every subarray ($O(n^2)$), the window's state is *updated incrementally* as its two ends advance ($O(n)$). Two flavors: **fixed-size** (the window is always k long) and **variable-size** (the window grows/shrinks to satisfy a condition).
>
> **Prerequisites:** two pointers from [Chapter 3](../ch03-arrays/index.md), hash maps from [Chapter 10](../ch10-hash-tables/index.md) (the window's character counts), and the deque from [Chapter 8](../ch08-stacks/index.md) for the maximum-window variant.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 15.1 | Longest Substring Without Repeating Characters | last-index map | $O(n)$ | [→](longest-substring-without-repeating-characters.md) |
| 15.2 | Minimum Window Substring | two maps + formed-count | $O(n)$ | [→](minimum-window-substring.md) |
| 15.3 | Longest Repeating Character Replacement | max-count window | $O(n)$ | [→](longest-repeating-character-replacement.md) |
| 15.4 | Maximum Average Subarray I | fixed-size running sum | $O(n)$ | [→](maximum-average-subarray.md) |
| 15.5 | Minimum Size Subarray Sum | shrink-until-valid | $O(n)$ | [→](minimum-size-subarray-sum.md) |
| 15.6 | Sliding Window Maximum | monotonic deque | $O(n)$ | [→](sliding-window-maximum.md) |
| 15.7 | Max Consecutive Ones III | flip-budget window | $O(n)$ | [→](max-consecutive-ones-iii.md) |

| 15.8 | Permutation In String | fixed-size anagram window | $O(n)$ | [→](permutation-in-string.md) |
| 15.9 | Maximum Erasure Value | all-unique window + sum | $O(n)$ | [→](maximum-erasure-value.md) |
| 15.10 | Longest Subarray Of 1s After Deleting One | zero-count window | $O(n)$ | [→](longest-subarray-of-ones-after-deleting-one.md) |
## The rest of the sliding_window/ directory

`src/main/kotlin/sliding_window/` also holds: Maximum Erasure Value, Maximum Sum Of Distinct Subarrays With Length K, Longest Continuous Subarray With Absolute Difference ≤ Limit, Longest Subarrays Of Ones After Deleting One Element, Minimum Swaps To Group All Ones Together, Partition Labels, and Programmer String. The repo's `string/sliding_window/` subfolder carries the string-flavored variants.

New pages are appended to the table above as they're written.
