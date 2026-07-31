# Chapter 16 — Bit Manipulation

> **Source:** `src/main/kotlin/bitset/`
>
> **Master idea:** the bit level is where a few identities do the work of whole data structures: **XOR** cancels pairs (single-number problems), **`n & (n-1)`** clears the lowest set bit (popcount), a **binary trie** answers maximum-XOR in O(1) per bit, and shifts encode "multiply by a power of two" (subset-sum formulas).
>
> **Prerequisites:** binary representation, the trie from [Chapter 13](../ch13-tries/index.md) (16.5 reuses it with two children), and basic recursion.

## Problems at a glance (this chapter's core set)

| # | Problem | Pattern | Complexity | Page |
|---|---------|---------|------------|------|
| 16.1 | Number Of 1 Bits | `n & (n-1)` popcount | $O(\text{set bits})$ | [→](number-of-1-bits.md) |
| 16.2 | Reverse Bits | bit-by-bit rebuild | $O(32)$ | [→](reverse-bits.md) |
| 16.3 | Single Number | XOR cancellation | $O(n)$ | [→](single-number.md) |
| 16.4 | Single Number III | XOR + lowbit split | $O(n)$ | [→](single-number-iii.md) |
| 16.5 | Maximum XOR Of Two Numbers | binary trie | $O(32n)$ | [→](maximum-xor-of-two-numbers.md) |
| 16.6 | Sum Of All Subset XOR Totals | bit-count formula | $O(n)$ | [→](sum-of-all-subset-xor-totals.md) |
| 16.7 | Smallest Number With All Set Bits | msb → all-ones | $O(\log n)$ | [→](smallest-number-with-all-set-bits.md) |

| 16.8 | Pow(x, n) | binary exponentiation | $O(log n)$ | [→](pow-x-n.md) |
| 16.9 | Divide Two Integers | binary long division | $O(log^2)$ | [→](divide-two-integers.md) |
## The rest of the bitset/ directory

`src/main/kotlin/bitset/` also holds: First Letter To Appear Twice, Longest Nice Subarray, Number Of Steps To Reduce A Number In Binary Representation To One, and the `string/` folder's binary-string variants. The XOR-family ideas recur in the `hash-table/` and `string/` folders.

New pages are appended to the table above as they're written.
