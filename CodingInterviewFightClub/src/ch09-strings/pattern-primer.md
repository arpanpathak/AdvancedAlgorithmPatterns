# 9.0 Pattern Primer — The Three Lenses

A string is a *sequence of characters* — but which properties of that sequence matter depends on the question. Nearly every string problem in this chapter (and most in the folder) is one of three lenses:

## Lens 1 — Counts: "does the multiset match?"

Anagram questions (["Valid Anagram"](valid-anagram.md), ["Group Anagrams"](group-anagrams.md)) ignore *order* entirely: two strings are anagrams iff every character appears the same number of times. The tool is a **frequency count**, and the classic trick is the **+1 / -1 counter**: increment for one string, decrement for the other, and check that all counts land on zero — no comparison of two separate maps needed.

Two counting representations to know:

- **`int[26]`** — lowercase letters map to indices by `c - 'a'`. $O(1)$ space (fixed 26), no hashing. The default.
- **`Map<Char, Int>`** — arbitrary alphabets. More general, more overhead.

When a frequency vector is used as a *key* (["Group Anagrams"](group-anagrams.md)), the same `int[26]` becomes a 26-element vector that two anagrams share exactly.

## Lens 2 — Patterns: "what is the shape of the mapping?"

Some problems care about the *structure* of the string, not its content: ["Isomorphic Strings"](isomorphic-strings.md) asks whether two strings have the same *substitution pattern*. The tool is a **first-occurrence encoding**: replace each character with the position of its first occurrence. Two strings are isomorphic iff their encodings are equal. This "encode to a canonical form" move — mapping an equivalence question to a *string equality* question — is one of the most reusable ideas in string problems (it also powers "find all anagrams" via sorted or counted canonical forms).

## Lens 3 — Structure: prefixes, palindromes, boundaries

The geometry of the string itself:

- **Prefixes** (["Longest Common Prefix"](longest-common-prefix.md)) — compare *position by position* across all strings; the vertical scan is the natural fit.
- **Palindromes** (["Longest Palindromic Substring"](longest-palindromic-substring.md)) — symmetry around a *center*. There are $2n - 1$ centers (each character, plus each gap); expanding each takes $O(n)$, for $O(n^2)$ total — usually the sweet spot, since the DP alternative is also $O(n^2)$ but with $O(n^2)$ memory.
- **Word boundaries** (["Reverse Words In A String"](reverse-words-in-a-string.md)) — tokenize on whitespace, then reorder the tokens; two pointers over the token list.

## The language reflexes

- **Immutability**: strings don't mutate in place — building output via repeated `+` is $O(n^2)$; use a `StringBuilder`/`StringBuffer` and join. (Every repo version in this chapter does.)
- **Characters are small integers**: `c - 'a'`, `'0'..'9'`, `isDigit()`, `lowercaseChar()` — the int-ness of chars is what makes `int[26]` counting possible.
- **Splitting traps**: `" ".split(" ")` yields empty strings for repeated spaces — the repo's ["Reverse Words"](reverse-words-in-a-string.md) filters them; "Validate IP" uses the empty-segment behavior of `split` to catch malformed input.

## Complexity intuition

Counting/pattern passes are $O(n)$ with $O(1)$ (int[26]) or $O(\Sigma)$ (map) space. Anything that processes every character of every word is $O(n \cdot L)$. Palindrome expansion is $O(n^2)$ time, $O(1)$ space — the rare case where the "obvious" DP is beaten on memory by the geometric insight.
