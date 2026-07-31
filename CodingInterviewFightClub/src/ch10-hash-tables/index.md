# Chapter 10 — Hash Tables & Sets

> **Source:** `src/main/kotlin/hashtable/` and `src/main/kotlin/array/hashtable/`
>
> **Master idea:** a hash table trades *ordering* for *speed* — it answers "is this key present?" and "what value is stored under this key?" in **amortized $O(1)$**, at the price of losing sorted order. Nearly every problem in this chapter is one of three moves: *complement lookup*, *value-to-state maps*, or *membership + canonicalization*.
>
> **Prerequisites:** arrays, and the frequency/grouping ideas from [Chapter 9](../ch09-strings/index.md) — many string problems *are* hash-table problems wearing characters.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 10.1 | Two Sum | complement lookup | $O(n)$ | [→](two-sum.md) |
| 10.2 | Contains Duplicate II | value -> last index | $O(n)$ | [→](contains-duplicate-ii.md) |
| 10.3 | Longest Consecutive Sequence | set + run-start detection | $O(n)$ | [→](longest-consecutive-sequence.md) |
| 10.4 | Valid Sudoku | per-row/col/box sets | $O(81)$ | [→](valid-sudoku.md) |
| 10.5 | First Unique Character | frequency map | $O(n)$ | [→](first-unique-character.md) |
| 10.6 | Design HashMap | open addressing | $O(1)$ amortized | [→](design-hash-map.md) |
| 10.7 | Roman To Integer | right-to-left accumulation | $O(n)$ | [→](roman-to-integer.md) |

## The rest of the hashtable/ directories

`src/main/kotlin/hashtable/` adds: Integer To Roman, H-Index, Intersection Of Two Arrays, Maximum Frequency Stack, Design A Number Container System, Design File System, Count Number Of Bad Pairs, Word Break variants. `src/main/kotlin/array/hashtable/` adds: First Missing Positive, Degree Of An Array, Valid Sudoku variants, Set Mismatch, Rank Transform Of An Array, Unique Number Of Occurrences, Integer To English Words, Equal Row And Column Pairs, and more. The LRU/LFU cache designs live in `src/main/kotlin/cache/` and are the "map + structure" capstone of this chapter's design problems.

New pages are appended to the table above as they're written.
