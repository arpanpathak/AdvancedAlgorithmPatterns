# 19.12 The One-Expression DP Gallery

> **Sources:** [`FrogJumpTopDown.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/FrogJumpTopDown.kt) · `StoneGame.kt` · `CoinChangeBottomUp.kt` · `google/CountNumberOfWaysToPickKCoinsSumDivisibleByM.kt`
> **Pattern:** variant gallery — whole DPs compressed into single expressions

## The gallery thesis

Some DPs are so clean that the **entire algorithm fits in one `getOrPut` expression**. These files are the "I've internalized this recurrence" versions — and they're superb study material because the *expression* is the recurrence, with zero scaffolding between you and the math.

## 1. `FrogJumpTopDown.kt` — the whole DP in a `getOrPut` + `any`

[2.8](../ch02-dynamic-programming/frog-jump.md) documents the canonical version (set of reachable jumps per stone). This file compresses it to a two-line recurrence:

```kotlin
class FrogJumpTopDown {
    data class State(val pos: Int, val k: Int)

    fun canCross(stones: IntArray): Boolean {
        val stoneSet = stones.toSet()
        val cache = mutableMapOf<State, Boolean>()

        fun isValidJump(pos: Int, nextJump: Int) =
            nextJump > 0 && (pos + nextJump) in stoneSet

        fun solve(pos: Int, k: Int): Boolean =
            cache.getOrPut(State(pos, k)) {
                pos == stones.last() || (k - 1..k + 1).any { nextJump ->
                    isValidJump(pos, nextJump) && solve(pos + nextJump, nextJump)
                }
            }

        return solve(0, 0)
    }
}
```

**What's cool:**

- **`(k - 1..k + 1).any { ... }`** — the three candidate jump lengths (`k-1, k, k+1`) are a *range expression*, not a loop. `any` short-circuits on the first successful jump.
- **`isValidJump` as a named lambda-expression** — `nextJump > 0 && (pos + nextJump) in stoneSet`; the stone-set membership IS the boundary check (no bounds arithmetic).
- **`pos == stones.last()`** — the base case is a boolean OR'd into the recurrence, not a separate branch.
- **`data class State`** — `(pos, k)` hashed by the map; the [19.1](travelling-salesman-top-down.md) state-value idiom.

The imperative version's two loops (outer stones, inner jumps) are gone — the recurrence is the whole file.

## 2. `StoneGame.kt` — the zero-sum relative score

The "Current Player's Score − Opponent's Score" trick lets the DP avoid tracking turns:

```kotlin
fun stoneGame(piles: IntArray): Boolean {
    // Cache stores (i to j) -> Max relative score difference for that range
    val cache = mutableMapOf<Pair<Int, Int>, Int>()

    /**
     * Returns (Current Player's Score - Opponent's Score) for the range [i, j].
     * This "Relative Score" approach avoids needing to track whose turn it is.
     */
    fun pick(i: Int, j: Int): Int = cache.getOrPut(i to j) {
        when (i) {
            j -> piles[i]                        // one pile left: take it all

            // Subtract the opponent's result: the recursive call returns the
            // advantage for the NEXT player.
            else -> maxOf(
                piles[i] - pick(i + 1, j),       // take left, subtract opponent's net gain
                piles[j] - pick(i, j - 1)        // take right, subtract opponent's net gain
            )
        }
    }

    return pick(0, piles.lastIndex) >= 0
}
```

**What's cool:** `piles[i] - pick(i+1, j)` — the *relative* score means the recursion never asks "whose turn?"; the sign flips encode it. The `when (i) { j -> ... }` base case is the single-pile boundary. This is the [2.x](../ch02-dynamic-programming/pattern-primer.md) interval-DP family ([2.10](../ch02-dynamic-programming/minimum-cost-to-cut-a-stick.md) style) with the zero-sum trick as the differentiator.

## 3. `CoinChangeBottomUp.kt` — the functional table fill

The bottom-up [2.16](../ch02-dynamic-programming/coin-change.md) winner, with the inner loop as `filter` + `minOfOrNull`:

```kotlin
fun coinChange(coins: IntArray, amount: Int): Int {
    val maxVal = amount + 1
    val dp = IntArray(amount + 1) { maxVal }
    dp[0] = 0

    (1..amount).forEach { i ->
        dp[i] = coins
            .filter { it <= i }                 // usable coins only
            .minOfOrNull { coin -> dp[i - coin] + 1 }
            ?: maxVal                           // no usable coin: keep the sentinel
    }

    return dp[amount].takeIf { it < maxVal } ?: -1
}
```

**What's cool:** the `filter { it <= i }` is the bounds guard as a predicate; `minOfOrNull ?: maxVal` is the "impossible" branch; `takeIf { it < maxVal } ?: -1` is the final normalization. The imperative double loop and this are the same computation — this one reads like its own recurrence.

## 4. The count twin: `CountNumberOfWaysToPickKCoinsSumDivisibleByM.kt` (FP form)

The [2.14](../ch02-dynamic-programming/count-ways-to-pick-k-coins-divisible-by-m.md) page documents the memoized `when`; the repo's file is the same `getOrPut` shape with the skip/pick sum as the return expression — the K-coins remainder-carry DP in its most compressed form (already shown in full on 2.14; the FP signature is `cache.getOrPut(State(idx, k, rem)) { ... }` with `(skip + pick) % mod` as the last line).

## The meta-lesson

These four files are the answer to "can you write it more cleanly?" — each shows that a DP's *essence* is one recurrence, and Kotlin's `getOrPut`/`any`/`minOfOrNull`/`filter` let the code *be* the recurrence. When an interviewer asks for the memoized version, writing it in this shape is both correct and impressive.

## Complexity

All four keep their canonical bounds — `getOrPut` memoization doesn't change the state space:

| File | Time | Space |
|---|---|---|
| FrogJump | $O(n^2)$ states | $O(n^2)$ |
| StoneGame | $O(n^2)$ intervals | $O(n^2)$ |
| CoinChange | $O(A \cdot C)$ | $O(A)$ |
| K-Coins | $O(n \cdot k \cdot m)$ | $O(n \cdot k \cdot m)$ |

## Variants & follow-ups

- **Frog Jump** ([2.8](../ch02-dynamic-programming/frog-jump.md)) — the canonical bottom-up page; this file is its top-down twin.
- **Minimum Cost To Cut A Stick** ([2.10](../ch02-dynamic-programming/minimum-cost-to-cut-a-stick.md)) — StoneGame's interval-DP sibling.
- **Coin Change** ([2.16](../ch02-dynamic-programming/coin-change.md)) — the full four-engine family ([19.3](coin-change-family.md) captures the rest).
- **Interview follow-up:** "When is the one-expression DP a liability?" When the recurrence needs *reconstruction* (paths, not just values) — that's when you need the imperative loop with parent-pointers. These files are for the decision problems; add bookkeeping for the answer problems.
