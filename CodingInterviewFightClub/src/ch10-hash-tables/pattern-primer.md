# 10.0 Pattern Primer — O(1) Lookup, Three Moves

A hash table (or hash set) stores keys with **amortized $O(1)$** insert, lookup, and delete — by hashing the key to a bucket. The cost: **no ordering**. Sorted order is gone; iteration order is (effectively) arbitrary. The recurring interview trade-off is exactly this: *"I could keep things sorted ($O(\log n)$ per op), or I could keep them hashable ($O(1)$ per op) — which does the question need?"*

The three moves:

## Move 1 — Complement lookup ("who completes me?")

Two Sum ([10.1](two-sum.md)) is the template: instead of searching for a *partner* for each element (nested loops, $O(n^2)$), store what you've *already seen* keyed by value, and for each element ask *"is my complement here?"* in $O(1)$. The map turns a search problem into a membership problem. Any "find a pair/triple with a given sum/property" question starts here.

## Move 2 — Value-to-state maps ("what do I remember about this value?")

Sometimes the map's *value* is the point, not just its existence:

- **Last seen position** — Contains Duplicate II ([10.2](contains-duplicate-ii.md)) stores `value -> index`, the state needed to answer "how far apart?"
- **Frequency** — First Unique Character ([10.5](first-unique-character.md)) stores `char -> count`, then scans for the first `count == 1`.
- **Counter for grouping** — the [Chapter 9](../ch09-strings/index.md) anagram pages are this move on characters.

The design reflex: *if you need to answer a question about a value "later", precompute the answer as the map's value now.*

## Move 3 — Membership + canonicalization ("what's the set, and what's the same?")

A **set** (a map with only keys) answers "have I seen this?" in $O(1)$:

- Longest Consecutive Sequence ([10.3](longest-consecutive-sequence.md)) — membership probing: "is `num - 1` present?" decides whether a run starts here.
- Valid Sudoku ([10.4](valid-sudoku.md)) — three sets per cell, one `add` that fails on duplicates.
- Grouping problems — sets and maps as *canonical-form buckets* (the [9.2](../ch09-strings/group-anagrams.md) frequency-vector key).

## The design questions

The Design HashMap page ([10.6](design-hash-map.md)) drills what "hash table" actually means under the hood:

- **Hash function** — `key % capacity` for integer keys; the table size is usually prime to spread buckets.
- **Collision handling** — *open addressing* (probe forward to the next empty slot — the repo's version) vs *chaining* (each bucket holds a linked list).
- **Load factor** — when the table fills, lookups degrade; resize (double + rehash) keeps amortized $O(1)$.

## Complexity intuition

$O(1)$ amortized for single ops — but the constant is real (hashing cost), the worst case is $O(n)$ (all keys colliding), and the *space* is $O(n)$ regardless. When the interviewer says "no hash maps", they usually mean "the problem has a structure hash tables can't see" — the sign you should be looking for a different pattern (two pointers, monotonic stacks, sorting).
