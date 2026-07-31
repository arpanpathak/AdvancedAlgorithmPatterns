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
## The rest of the stack/ directory

`src/main/kotlin/stack/` is deep: more monotonic-stack classics (Next Greater Element I, Sum Of Subarray Minimums/Ranges, Online Stock Span, Buildings With An Ocean View, Number Of Visible People In A Queue), string-stack hybrids (Minimum Remove To Make Valid Parentheses, Remove Duplicate Letters, Smallest Subsequence Of Distinct Characters, Remove Stars From String, Longest Valid Parentheses), and design puzzles (MinStack variants, Flatten Nested List Iterator, Design A Stack With Increment Operations, Exclusive Time Of Functions). The repo also has `queues/` with FIFO implementations used by the BFS pages in earlier chapters.

New pages are appended to the table above as they're written.
