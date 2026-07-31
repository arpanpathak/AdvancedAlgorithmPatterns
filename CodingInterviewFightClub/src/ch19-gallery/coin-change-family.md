# 19.3 Coin Change — The Four Implementations, Captured

> **Sources:** [`src/main/kotlin/array/dp/`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/tree/main/src/main/kotlin/array/dp) — `CoinChange.kt`, `CoinChangeBottomUp.kt`, `CoinChangeBFS.kt`, `CoinChange_II.kt`, `CoinChange_II_BottomUp.kt`
> **Pattern:** variant gallery — one problem, four engines ([2.16](../ch02-dynamic-programming/coin-change.md) covers the bottom-up winner)

## The family map

| File | Engine | What it proves |
|---|---|---|
| `CoinChange.kt` | memoized top-down + `coinChangeCleanAf` bottom-up | both spellings, one file |
| `CoinChangeBottomUp.kt` | bottom-up + a functional `forEach` flavor | the table fill in filter-min style |
| `CoinChangeBFS.kt` | **BFS over amounts** | "fewest coins" is a shortest-path problem |
| `CoinChange_II.kt` / `_BottomUp.kt` | ways-counting DP | the *counting* twin ([2.16](../ch02-dynamic-programming/coin-change.md) variants) |

## The BFS surprise: `CoinChangeBFS.kt`

The coolest of the four: "fewest coins to reach `amount`" is exactly "shortest path from `0` to `amount` in a graph where each coin is an edge `x → x + coin`". BFS finds it in `O(amount · coins)`:

```kotlin
fun coinChangeBFS(coins: IntArray, amount: Int): Int {
    if (amount == 0) return 0

    val queue = ArrayDeque<Int>().apply { add(0) }
    val visited = mutableSetOf(0)
    var steps = 0

    while (queue.isNotEmpty()) {
        steps++
        repeat(queue.size) {                  // one level = one coin
            val current = queue.removeFirst()
            for (coin in coins) {
                val next = current + coin
                if (next == amount) return steps
                if (next < amount && visited.add(next)) {
                    queue.add(next)
                }
            }
        }
    }
    return -1
}
```

**Why it's correct:** every path from 0 to `amount` uses `k` edges = `k` coins, and BFS finds the minimum hop count. **Why `visited`?** Amounts are re-reachable many ways (`0+1+1` vs `0+2`); the first visit is the fewest coins, so revisits are pruned — same as [6.14](../ch06-graphs/rotting-oranges.md)'s `fresh == INF` guard. The `steps` counter with `repeat(queue.size)` is the [5.2](../ch05-trees/binary-tree-level-order-traversal.md) level fence.

**When to reach for it in an interview:** when the problem is *phrased* as reachability ("can you make the amount? what's the minimum number of coins?") — the graph framing is a different intuition that some interviewers love, and it pairs beautifully with the DP as "two views of the same recurrence".

## The counting twin: `CoinChange_II.kt`

"Number of ways" flips the recurrence from `min` to `sum`, and the **loop order matters** — coins outer, amounts inner makes each combination counted once:

```kotlin
// CoinChange_II.kt (bottom-up counting)
fun change(amount: Int, coins: IntArray): Int {
    val dp = IntArray(amount + 1)
    dp[0] = 1                                    // one way to make 0: take nothing

    for (coin in coins) {                        // coin loop OUTSIDE
        for (a in coin..amount) {
            dp[a] += dp[a - coin]                // order matters: combinations, not permutations
        }
    }
    return dp[amount]
}
```

The coin-outer loop is the classic "count combinations vs permutations" distinction ([2.6](../ch02-dynamic-programming/partition-equal-subset-sum.md)'s ascending-vs-descending discussion is this exact subtlety). `dp[0] = 1` seeds the empty combination.

## The top-down in the same file: `CoinChange.kt`

```kotlin
class CoinChange {
    fun coinChange(coins: IntArray, amount: Int): Int {
        val dp = IntArray(amount + 1) { -1 }
        return coinChange(coins, amount, dp).let { if (it != Int.MAX_VALUE) it else -1 }
    }

    private fun coinChange(coins: IntArray, amount: Int, dp: IntArray): Int {
        return when {
            amount == 0 -> 0
            dp[amount] != -1 -> dp[amount]
            else -> {
                var minCoins = Int.MAX_VALUE
                for (coin in coins) {
                    if (coin <= amount) {
                        val result = coinChange(coins, amount - coin, dp)
                        if (result != Int.MAX_VALUE) {
                            minCoins = minOf(minCoins, 1 + result)
                        }
                    }
                }
                minCoins
            }
        }.also { dp[amount] = it }
    }
}
```

The `Int.MAX_VALUE` sentinel marks "unreachable"; the `.also { dp[amount] = it }` memoizes on every return path (including the base cases — harmless). The `-1` sentinel in the public wrapper distinguishes "impossible" from "0 coins".

## Dry run (BFS)

**Input:** `coins = [1,2,5]`, `amount = 11`.

```
queue=[0], visited={0}, steps=0
steps=1: 0 -> 1, 2, 5.  queue=[1,2,5]
steps=2: 1 -> 2(seen),3,6.  2 -> 3(seen),4,7.  5 -> 6(seen),7(seen),10.  queue=[3,6,4,7,10]
steps=3: 3 -> 4(seen),5(seen),8.  6 -> 7(seen),8(seen),11 == amount -> return 3 ✓
```

BFS finds 11 at depth 3 (5+5+1) — the `visited` set keeps the frontier small (amounts reached cheaply are never re-expanded). The DP and BFS agree: fewest coins = 3.

## Complexity

**Time.** BFS: `O(amount · coins)` (each amount expanded once); top-down: `O(amount · coins)`; counting: same.

**Space.** BFS: `O(amount)` visited + queue; DP: `O(amount)`.

## Variants & follow-ups

- **Coin Change** ([2.16](../ch02-dynamic-programming/coin-change.md)) — the full page with the bottom-up winner.
- **Minimum Number Of Refueling Stops** ([11.7](../ch11-greedy/minimum-number-of-refueling-stops.md)) — reachability-as-graph again, with a greedy heap instead of BFS.
- **Interview follow-up:** "Which implementation do you prefer?" DP bottom-up — O(amount·coins), no queue, deterministic. The BFS is the *explanation* tool (shortest-path framing); the top-down is the *derivation* tool (brute-force recursion, memoized). Knowing all three is the "I understand this problem" signal.
