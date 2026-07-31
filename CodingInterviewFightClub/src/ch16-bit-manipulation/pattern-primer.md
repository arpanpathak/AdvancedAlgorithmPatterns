# 16.0 Pattern Primer — The Bit Identities

Bit manipulation is a small toolbox of identities that replace loops, maps, and even whole data structures. Master these four:

## The XOR cancellation

$$
x \oplus x = 0, \qquad x \oplus 0 = x, \qquad x \oplus y = y \oplus x
$$

**Pairs cancel, singletons survive.** XORing a whole array leaves exactly the elements with odd multiplicity — [16.3](single-number.md) and [16.4](single-number-iii.md) are pure applications. XOR is also addition without carries, which makes it the "checksum" tool.

## The lowest-set-bit trick

```
n & (n - 1)   clears the lowest set bit   (used for popcount, [16.1])
n & -n        isolates the lowest set bit  (used for the split in [16.4])
```

`n & -n` (two's complement negation) is the "lowbit" — the standard way to pick *one* distinguishing bit. It's the tiny idiom that turns "I need to split the numbers by a bit where they differ" into one line.

## The binary trie

A trie with **two children per node** (bit 0 / bit 1) stores numbers by their binary representation. Walking it greedily — at each bit, take the *opposite* child if it exists — maximizes the XOR digit by digit: that's [16.5](maximum-xor-of-two-numbers.md), and it's the [Chapter 13](../ch13-tries/index.md) machinery with a 2-slot alphabet.

## Shift arithmetic

- `x shl 1` = `2x`, `x ushr 1` = floor divide by 2 (unsigned, for bits).
- `(1 shl k) - 1` = the number with the lowest k bits all set — [16.7](smallest-number-with-all-set-bits.md).
- `x shl k` multiplies by $2^k$ — used to scale a bit-sum by subset counts in [16.6](sum-of-all-subset-xor-totals.md).

## The counting trick

For "sum over all subsets of X" problems: each set bit of any element contributes to **exactly $2^{n-1}$** subset XOR totals (the bit is set or unset independently per element; fixing one element's contribution, the other $n-1$ elements' bits vary freely). So the answer is `OR of all elements << (n - 1)` — one pass, no enumeration.

## Complexity intuition

Bit operations are O(1) per bit and O(32) per number (fixed-width words). The identities collapse what look like $O(n \cdot 2^n)$ or $O(n^2)$ problems into $O(n)$. The interview tell: *"all numbers... pairs... XOR... bit"* in the problem statement → the identity toolbox, not loops.
