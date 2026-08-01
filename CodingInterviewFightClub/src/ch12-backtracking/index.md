# Chapter 12 — Backtracking

> **Source:** `src/main/kotlin/backtracking/` and `src/main/kotlin/array/Combinatorics/` (plus `string/backtracking/`)
>
> **Master idea:** backtracking is **DFS with an undo button** — explore a choice, recurse, and *un-make* the choice before trying the next one. It's the right tool whenever the problem asks to *enumerate all* solutions of a combinatorial shape (subsets, permutations, partitions, placements).
>
> **Prerequisites:** recursion, the DFS from [Chapters 5](../ch05-trees/index.md)/[6](../ch06-graphs/index.md), and the validity-checking sets from [10.4](../ch10-hash-tables/valid-sudoku.md).

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 12.1 | Subsets | positional include/exclude | $O(2^n)$ | [→](subsets.md) |
| 12.2 | Permutations | swap-based | $O(n!)$ | [→](permutations.md) |
| 12.3 | Generate Parentheses | balance-constrained | $O(\binom{2n}{n})$ | [→](generate-parentheses.md) |
| 12.4 | N-Queens | row-by-row placement | $O(n!)$ | [→](n-queens.md) |
| 12.5 | Palindrome Partitioning | prefix pruning | $O(2^n)$ | [→](palindrome-partitioning.md) |
| 12.6 | Restore IP Addresses | segment-length pruning | $O(1)$ (fixed shape) | [→](restore-ip-addresses.md) |
| 12.7 | Sudoku Solver | cell-by-cell with validity | $O(9^{81})$ bound | [→](sudoku-solver.md) |

| 12.8 | Combination Sum | include/exclude with repetition | $O(2^t)$ | [→](combination-sum.md) |
| 12.9 | Partition To K Equal Sum Subsets | subset-building backtracking | $O(k 2^n)$ | [→](partition-to-k-equal-sum-subsets.md) |
| 12.10 | Next Permutation | Narayana-Pandita | $O(n)$ | [→](next-permutation.md) |
| 12.11 | Permutations II | per-frame seen-set dedupe | $O(n! n)$ | [→](permutations-ii.md) |
| 12.12 | Combinations | start-index backtracking | $O(C(n,k))$ | [→](combinations.md) |
| 12.13 | Combination Sum III | k + sum gates with pruning | $O(C(9,k))$ | [→](combination-sum-iii.md) |
| 12.12 | Subsets II | sorted skip-duplicates | $O(2^n)$ | [→](subsets-ii.md) |
| 12.13 | N-Queens II | count-only backtracking | $O(n!)$ | [→](n-queens-ii.md) |
| 12.14 | Word Search II | trie-pruned DFS | $O(mn4^L)$ | [→](word-search-ii.md) |
| 12.15 | Word Break II | memoized sentence enumeration | $O(2^n)$ | [→](word-break-ii.md) |
| 12.16 | Next Greater Element III | next-permutation digits | $O(log n)$ | [→](next-greater-element-iii.md) |
| 12.17 | Strobogrammatic Number II | mirrored digit pairing | $O(5^{n/2})$ | [→](strobogrammatic-number-ii.md) |
| 12.19 | Closest Subsequence Sum | meet-in-the-middle | $O(2^{n/2}log)$ | [→](closest-subsequence-sum.md) |
| 12.20 | Max Length Of Concatenated String | bitmask backtracking | $O(2^n)$ | [→](maximum-length-of-concatenated-string.md) |
## The rest of the backtracking/ directories

`src/main/kotlin/backtracking/` also holds: N-Queens II and N-Queens Optimized, Sudoku Solver (set-based variant), Partition To K Equal Sum Subsets, Path With Maximum Gold, Strobogrammatic Number II, Expression Add Operators (and optimized). `array/Combinatorics/` adds Combinations, Subsets II (with duplicates), Permutations II (duplicates, backtracking + Narayana-Pandita), Next Permutation and its follow-ups. `string/backtracking/` adds Word Break II and Word Square.

New pages are appended to the table above as they're written.
