# Chapter 9 — Strings

> **Source:** `src/main/kotlin/string/` (plus its `dynamic_programming/`, `sliding_window/`, `backtracking/`, `pattern_matching/` subfolders)
>
> **Master idea:** a string is an *immutable array of characters*, and most string problems are really one of three lenses: **counts** (anagrams — what matters is multiset equality), **patterns** (isomorphism — what matters is the shape of the mapping), or **structure** (prefixes, palindromes, word boundaries — what matters is position).
>
> **Prerequisites:** hash maps from [Chapter 6](../ch06-graphs/index.md), two pointers from [Chapter 3](../ch03-arrays/index.md), and the DP lens from [Chapter 2](../ch02-dynamic-programming/index.md) for the harder variants.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 9.1 | Valid Anagram | counter array (+1 / -1) | $O(n)$ | [→](valid-anagram.md) |
| 9.2 | Group Anagrams | frequency-vector key | $O(n \cdot L)$ | [→](group-anagrams.md) |
| 9.3 | Isomorphic Strings | pattern encoding | $O(n)$ | [→](isomorphic-strings.md) |
| 9.4 | Longest Palindromic Substring | expand around center | $O(n^2)$ | [→](longest-palindromic-substring.md) |
| 9.5 | Longest Common Prefix | vertical scan | $O(n \cdot L)$ | [→](longest-common-prefix.md) |
| 9.6 | Reverse Words In A String | split + two pointers | $O(n)$ | [→](reverse-words-in-a-string.md) |
| 9.7 | Validate IP Address | segment validation | $O(n)$ | [→](validate-ip-address.md) |

| 9.8 | Find The Index Of The First Occurrence | Rabin-Karp rolling hash | $O(n+m)$ | [→](find-the-index-of-the-first-occurrence.md) |
| 9.9 | Number Of Matching Subsequences | 26 buckets of word-states | $O(n + W)$ | [→](number-of-matching-subsequences.md) |
| 9.10 | Find The Index Of The First Occurrence (KMP) | LPS array | $O(n+m)$ | [→](find-the-index-of-the-first-occurrence-kmp.md) |
| 9.11 | Valid Palindrome | two pointers with skip | $O(n)$ | [→](valid-palindrome.md) |
| 9.12 | Count And Say | run-length iteration | $O(\text{term})$ | [→](count-and-say.md) |
| 9.13 | Add Strings | digit-wise carry | $O(n)$ | [→](add-strings.md) |
| 9.14 | String To Integer (atoi) | phase scanner + overflow pre-check | $O(n)$ | [→](string-to-integer-atoi.md) |
| 9.15 | Text Justification | greedy pack + space split | $O(nw)$ | [→](text-justification.md) |
| 9.16 | Length Of Last Word | backward scan | $O(n)$ | [→](length-of-last-word.md) |
| 9.17 | Merge Strings Alternately | max-length loop | $O(n+m)$ | [→](merge-strings-alternately.md) |
| 9.18 | Goat Latin | word transform | $O(n)$ | [→](goat-latin.md) |
| 9.19 | Detect Capital | capital-count rules | $O(n)$ | [→](detect-capital.md) |
| 9.20 | Is Subsequence | two-pointer match | $O(|t|)$ | [→](is-subsequence.md) |
| 9.21 | String Compression | in-place run-length | $O(n)$ | [→](string-compression.md) |
| 9.22 | Custom Sort String | rank-map sort | $O(n log n)$ | [→](custom-sort-string.md) |
| 9.23 | Rank Teams By Votes | position-frequency sort | $O(vn)$ | [→](rank-teams-by-votes.md) |
| 9.24 | Valid Palindrome II | skip-one palindrome check | $O(n)$ | [→](valid-palindrome-ii.md) |
| 9.25 | String Compression III | 9-capped run-length | $O(n)$ | [→](string-compression-iii.md) |
## The rest of the string/ directory

`src/main/kotlin/string/` is huge: more counting/pattern problems (Detect Capital, Isomorphic variants, IsSubsequence, Count Words With A Given Prefix), parsing & validation (Valid Number, Validate IP Address (better implementation), Excel Sheet To Column Number, Count And Say, Goat Latin, String Compression), DP-heavy classics in `dynamic_programming/` (Edit Distance, Regular Expression Matching, Interleaving String, Palindrome Partitioning II, Longest Palindromic Subsequence), `sliding_window/` (Longest Substring Without Repeating Characters and friends), `pattern_matching/`, and `backtracking/` (Generate Parentheses, Word Break II, Word Square).

New pages are appended to the table above as they're written.
