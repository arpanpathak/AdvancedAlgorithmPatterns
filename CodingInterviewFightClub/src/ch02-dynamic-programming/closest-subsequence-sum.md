# 2.12 Closest Subsequence Sum

This problem is covered in full in [Chapter 1, section 1.22](../ch01-binary-search/closest-subsequence-sum.md), because its core weapon is **meet-in-the-middle + binary search** — a Chapter 1 pattern — rather than a DP table.

It earns a mention in the DP chapter precisely because it is the **anti-DP**: enumerating all $2^{n/2}$ subset sums per half beats any $O(n \cdot \text{range})$ table when values are huge. Read the full treatment there, then come back and ask yourself: *"when does DP stop being the right tool?"*
