# 2.0 Pattern Primer — Organized Recursion

Dynamic programming is **recursion with a memo pad**. That sentence is the entire chapter. The hard part is never the memoization — it's *finding the right state*. This page gives you the recipe used on every page of this chapter.

## The recipe

Every DP problem is four answers:

1. **State** — what does `dp[...]` represent? (The dimensions of the table ARE the state.)
2. **Recurrence** — how does `dp[i][j]` depend on *smaller* states?
3. **Base cases** — which cells are known without computing?
4. **Answer** — which cell (or max over cells) is the answer?

If you can write those four things in words, the code writes itself. Most interviewers are actually grading your ability to *narrate* these four, not to type the loop.

## The two implementation styles

**Top-down (memoized recursion).** Write the recurrence as a recursive function; on entry, check a memo table; on exit, store the result.

```kotlin
fun solve(i: Int, j: Int): Int {
    memo[i][j]?.let { return it }          // already computed?
    // base cases, then:
    val result = ...                       // recurrence
    memo[i][j] = result
    return result
}
```

**Bottom-up (table filling).** Fill the table in *dependency order* — every cell's dependencies must already be filled.

Top-down is easier to get right (it mirrors the recurrence literally) and only computes reachable states; bottom-up is faster per cell (no function-call overhead, better cache locality) and avoids stack-depth issues on long recurrences. **Interview answer: write top-down first for correctness, then offer bottom-up as the optimization.** Both appear in this chapter.

## The math: why DP beats brute force

Brute force solves every subproblem *independently* — the total work is the number of leaves of the recursion tree. DP solves each distinct subproblem **once**. If the state space has $S$ states and each takes $O(T)$ to combine, then:

$$
T_{\text{DP}} = O(S \cdot T) \quad \text{vs} \quad T_{\text{brute}} = O(\text{number of leaves})
$$

The classic example (Fibonacci): brute force is $T(n) = T(n-1) + T(n-2) + O(1)$, solved by the recurrence to $O(\phi^n)$ (exponential); DP collapses the state space $\{0..n\}$ to $O(n)$ total work. The *overlapping subproblems* property is exactly what makes the memo table pay for itself: see [Reference §4](../reference/complexity.md) for the Master Theorem and how to recognize which recurrences are polynomial.

## The two properties (name them out loud)

- **Optimal substructure:** the optimal solution contains optimal solutions to subproblems. (If you can't state this, you can't prove the recurrence.)
- **Overlapping subproblems:** the same subproblem is reached through many different paths. (If not true, memoization is wasted effort and you should just recurse.)

Both are needed. Knapsack, edit distance, and LCS have both; quicksort-style divide-and-conquer has the first but not the second — which is why those get recursion, not DP.

## Common state shapes in this chapter

| State shape | Meaning | Sections |
|---|---|---|
| `dp[i]` on a sequence | "best result considering the first i elements" | 2.7, 2.13 |
| `dp[i][j]` on two sequences | prefixes of two strings | 2.1, 2.2, 2.3 |
| `dp[c]` on capacity | "best value with remaining capacity c" | 2.4, 2.5, 2.6 |
| `dp[i][j]` on an interval | subarray/range `[i, j]` | 2.10, 2.11 |
| exotic states | sets of jumps, (eggs, moves) pairs | 2.8, 2.9 |

## The optimization ladder (say this in interviews)

1. Write brute force recursion.
2. Add a memo → top-down DP.
3. Convert to bottom-up → iterative DP.
4. Notice you only need the last row / two rows → **rolling array** (space $O(1)$–$O(\min)$).
5. Only if asked: formalize with matrices / convex hull / other exotic tricks.

Every page in this chapter shows at least steps 1–2, most show 3–4, and several show 5.

## When NOT to use DP

If the subproblems don't overlap, or the state space is astronomically large (e.g. Closest Subsequence Sum in [1.22](../ch01-binary-search/closest-subsequence-sum.md)), DP is wrong tool. Meeting-in-the-middle, greedy, or divide-and-conquer take its place. Knowing the boundary is a senior signal.
