# Chapter 13 — Tries

> **Source:** `src/main/kotlin/trie/`
>
> **Master idea:** a trie (prefix tree) stores *shared prefixes once*: each node is one character of a path, and a word is the path from root to a node marked as a word end. It turns "does any word start with this prefix?" into a $O(L)$ walk — the data structure *for* prefixes, autocomplete, and word-search.
>
> **Prerequisites:** tree recursion from [Chapter 5](../ch05-trees/index.md), hash maps from [Chapter 10](../ch10-hash-tables/index.md) (the children maps), and the [Backtracking](../ch12-backtracking/index.md) template for the search-heavy pages.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 13.1 | Implement Trie (Prefix Tree) | insert / search / startsWith | $O(L)$ / op | [→](implement-trie.md) |
| 13.2 | Word Break I | trie + memoized DFS | $O(n^2)$ | [→](word-break.md) |
| 13.3 | Design Add And Search Words | wildcard `.` search | $O(26^L)$ worst | [→](design-add-and-search-words.md) |
| 13.4 | Count Words With A Given Prefix | prefix-count per node | $O(L)$ query | [→](count-words-with-a-given-prefix.md) |
| 13.5 | Search Suggestion System | trie + subtree collection | $O(L + k)$ / prefix | [→](search-suggestion-system.md) |
| 13.6 | Word Squares | trie-indexed backtracking | exponential | [→](word-squares.md) |
| 13.7 | Design Auto Complete System | hotness-ranked suggestions | $O(L + k)$ / input | [→](design-autocomplete-system.md) |

| 13.8 | Word Break II | prefix backtracking | $O(\text{sentences})$ | [→](word-break-ii.md) |
## The rest of the trie/ directory

`src/main/kotlin/trie/` also holds: `AutoCompleteSystemWithHeap.kt` (the heap-ranked variant of 13.7), `CountWordsWithAGivenPrefix_Trie_FP.kt` (the functional flavor of 13.4), `LongestCommonPrefix.kt` (the trie answer to [9.5](../ch09-strings/longest-common-prefix.md) — the longest single-child path from the root), `EqualRowAndColumnPairs.kt`, and `WordSquaresShorter.kt`. Tries also appear in `src/main/kotlin/string/` (Count Words With A Given Prefix) and `src/main/kotlin/design/`.

New pages are appended to the table above as they're written.
