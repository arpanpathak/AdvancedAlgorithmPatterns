# Chapter 8 — Stacks & Queues

> **Source:** `src/main/kotlin/stack/` (plus `queues/` and `queueu/`)
>
> **Master idea:** a stack is *"the most recent thing first"* (LIFO) and a queue is *"the oldest thing first"* (FIFO). Stack problems are almost always one of three moves: *match pairs*, *carry state down*, or — the big one — *maintain a monotonic sequence*.
>
> **Prerequisites:** arrays, and the queue/BFS intuition from [Chapter 5](../ch05-trees/index.md) — the queue half of this chapter is where BFS gets its engine.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 8.1 | Valid Parentheses | stack matching | $O(n)$ | [→](valid-parentheses.md) |
| 8.2 | Min Stack | dual-stack design | $O(1)$ / op | [→](min-stack.md) |
| 8.3 | Daily Temperatures | monotonic stack | $O(n)$ | [→](daily-temperatures.md) |
| 8.4 | Next Greater Element II | circular monotonic stack | $O(n)$ | [→](next-greater-element-ii.md) |
| 8.5 | Largest Rectangle In Histogram | monotonic stack + sentinel | $O(n)$ | [→](largest-rectangle-in-histogram.md) |
| 8.6 | Evaluate Reverse Polish Notation | stack arithmetic | $O(n)$ | [→](evaluate-reverse-polish-notation.md) |
| 8.7 | Remove K Digits | monotonic stack + greedy | $O(n)$ | [→](remove-k-digits.md) |

| 8.8 | Decode String | recursion with a shared index | $O(len)$ | [→](decode-string.md) |
| 8.9 | Longest Valid Parentheses | stack of indices + base | $O(n)$ | [→](longest-valid-parentheses.md) |
| 8.10 | Basic Calculator II | pending-term scan | $O(n)$ | [→](basic-calculator-ii.md) |
| 8.11 | Basic Calculator | sign stack | $O(n)$ | [→](basic-calculator.md) |
| 8.12 | Basic Calculator III | recursive descent | $O(n)$ | [→](basic-calculator-iii.md) |
| 8.13 | Asteroid Collision | survivor stack | $O(n)$ | [→](asteroid-collision.md) |
| 8.14 | Online Stock Span | monotonic stack + span | $O(1)$ amortized | [→](online-stock-span.md) |
| 8.15 | Exclusive Time Of Functions | interval accounting stack | $O(L)$ | [→](exclusive-time-of-functions.md) |
| 8.16 | Remove All Adjacent Duplicates | stack-as-builder | $O(n)$ | [→](remove-all-adjacent-duplicates.md) |
| 8.17 | Minimum Add To Make Valid | unmatched counters | $O(n)$ | [→](minimum-add-to-make-parentheses-valid.md) |
| 8.18 | Minimum Remove To Make Valid | mark-then-filter | $O(n)$ | [→](minimum-remove-to-make-valid-parentheses.md) |
| 8.19 | Remove Duplicate Letters | monotonic + lastIndex | $O(n)$ | [→](remove-duplicate-letters.md) |
| 8.20 | One Three Two Pattern | decreasing stack + third | $O(n)$ | [→](one-three-two-pattern.md) |
| 8.21 | Maximal Rectangle | histogram stack per row | $O(mn)$ | [→](maximal-rectangle.md) |
| 8.22 | Check If Parentheses String Valid | balance-range sweep | $O(n)$ | [→](check-if-a-parentheses-string-can-be-valid.md) |
| 8.23 | Simplify Path | token-stack | $O(n)$ | [→](simplify-path.md) |
| 8.24 | Remove Stars From String | stack erasure | $O(n)$ | [→](remove-stars-from-string.md) |
| 8.25 | Sum Of Subarray Minimums | monotonic contributions | $O(n)$ | [→](sum-of-subarray-minimums.md) |
| 8.26 | Sum Of Subarray Ranges | max-sum minus min-sum | $O(n)$ | [→](sum-of-subarray-ranges.md) |
| 8.27 | Buildings With An Ocean View | right-to-left max | $O(n)$ | [→](buildings-with-an-ocean-view.md) |
| 8.28 | Minimum Operations To Convert All Elements To Zero | monotonic difference | $O(n)$ | [→](minimum-operations-to-convert-all-elements-to-zero.md) |
## The rest of the stack/ directory

`src/main/kotlin/stack/` is deep: more monotonic-stack classics (Next Greater Element I, Sum Of Subarray Minimums/Ranges, Online Stock Span, Buildings With An Ocean View, Number Of Visible People In A Queue), string-stack hybrids (Minimum Remove To Make Valid Parentheses, Remove Duplicate Letters, Smallest Subsequence Of Distinct Characters, Remove Stars From String, Longest Valid Parentheses), and design puzzles (MinStack variants, Flatten Nested List Iterator, Design A Stack With Increment Operations, Exclusive Time Of Functions). The repo also has `queues/` with FIFO implementations used by the BFS pages in earlier chapters.

New pages are appended to the table above as they're written.
