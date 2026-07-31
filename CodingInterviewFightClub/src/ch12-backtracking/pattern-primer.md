# 12.0 Pattern Primer — DFS With an Undo Button

Backtracking is the algorithm for one specific question: **"list *all* ways to build X."** It's DFS over the *space of partial constructions* — at every node you try each legal extension, recurse, and when the recursion returns you **undo** the extension before trying the next one. The undo is what makes it backtracking rather than plain enumeration: the state is shared, not copied.

## The template

```kotlin
fun backtrack(state) {
    if (isComplete(state)) { result.add(snapshot(state)); return }
    for (choice in legalChoices(state)) {
        apply(state, choice)          // choose
        backtrack(state)              // explore
        undo(state, choice)           // un-choose  <-- the backtrack
    }
}
```

Three decisions define each problem:

1. **What is a "state"?** — a partial subset, a partial permutation, a queen configuration, a board.
2. **What is "complete"?** — `start == n` for subsets; `row == n` for queens; all cells filled for Sudoku.
3. **Which choices are legal?** — *pruning*: the include/exclude boundary, the balance invariant, `isSafe`/`isValid`.

## Two structural flavors

**Positional (choose/skip at each index)** — [Subsets](subsets.md), [Generate Parentheses](generate-parentheses.md), [Palindrome Partitioning](palindrome-partitioning.md), [Restore IP Addresses](restore-ip-addresses.md). The recursion advances a *position* (`start`); at each position there are a few candidate *lengths/values*; "complete" is "position reached the end". The tree is wide but shallow-ish.

**Swap-based** — [Permutations](permutations.md). Instead of building a path, you *permute in place*: fix a position by swapping, recurse on the rest, swap back. The undo is the second swap. Subtle but the classic trick for orderings.

**Placement** — [N-Queens](n-queens.md), [Sudoku Solver](sudoku-solver.md). The recursion advances a *row/cell*; the choices are placements; pruning is the constraint check (which the [10.4](../ch10-hash-tables/valid-sudoku.md) sets can make O(1)).

## The pruning reflex

Backtracking without pruning is brute-force DFS — exponential and often hopeless. The art is killing branches *early*:

- Subsets prune by only choosing elements *after* `start` (no reordering → no duplicates).
- Parentheses prune with the invariant `close > open` (a `)` is only legal while more `(` are open).
- Queens prune with the diagonal check before placing.
- Sudoku prunes with the row/col/box validity check before trying a digit.

The earlier and cheaper the check, the smaller the tree. Mentioning "the pruning is what makes this feasible" is the depth signal.

## Complexity intuition

The output itself is exponential, so the complexity is *output-sized*: $2^n$ subsets, $n!$ permutations, $\binom{2n}{n}$ balanced parentheses. Time = $\sum$ over all nodes of the work per node (copying the path at each leaf dominates: $O(n \cdot 2^n)$ for subsets). The tree shape — not the input — is what you analyze. When the output is large, backtracking is *inherently* expensive; the question is whether the pruning keeps the tree near the output size.
