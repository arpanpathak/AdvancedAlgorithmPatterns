# 2.9 Super Egg Drop

> **Source:** [`src/main/kotlin/dynamic_programming/SuperEggDropping.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/SuperEggDropping.kt)
> **Pattern:** state-inversion DP · **Gym boss — the inverted table trick**

## The Problem

You have `k` eggs and a building with `n` floors. An egg dropped from floor `f` **breaks** if `f >= F` (the unknown critical floor) and survives otherwise. Once broken, an egg is gone. Find the **minimum number of drops** (in the worst case) needed to determine `F` with certainty.

- Constraints: $1 \le k \le 100$, $1 \le n \le 10^4$.

## Examples

```
k = 1, n = 2  -> 2   (with one egg you must scan: floor 1, then floor 2)
k = 2, n = 6  -> 3   (drop from 3: break → scan 1..2 with 1 egg (2 more); survive → floors 4..6 with 2 eggs...)
k = 3, n = 14 -> 4   (the classic "2 eggs 14 floors"... with 3 eggs, 4 drops cover 15 floors)
k = 2, n = 100 -> 14 (the classic "2 eggs, 100 floors" answer)
```

## Intuition — flip the question upside down

The *direct* DP ("minimum drops for k eggs and n floors") has the recurrence:

$$
\text{drops}(k, n) = 1 + \min_{1 \le f \le n} \max\!\big(\underbrace{\text{drops}(k-1, f-1)}_{\text{breaks: k-1 eggs, f-1 floors below}}, \underbrace{\text{drops}(k, n-f)}_{\text{survives: k eggs, n-f floors above}}\big)
$$

That's correct but $O(kn^2)$ — with $k = 100$ and $n = 10^4$, that's $100 \times 10^8 = 10^{10}$ operations — far too slow. The repo uses the **inverted state**, which is the interview-gold trick:

$$
dp[\text{eggs}][\text{moves}] = \text{maximum number of floors that can be decisively tested with } \text{eggs} \text{ eggs and } \text{moves} \text{ moves}
$$

The inversion: instead of asking "how many moves do k eggs need for n floors?", ask "**how many floors can k eggs cover in m moves?**" The answer to the original problem is the smallest `m` with `dp[k][m] >= n`.

**The recurrence (the beautiful part).** With `m` moves and `e` eggs, drop once:

- If the egg **breaks**: you have `e-1` eggs and `m-1` moves left → can cover `dp[e-1][m-1]` floors *below*.
- If it **survives**: you have `e` eggs and `m-1` moves → can cover `dp[e][m-1]` floors *above*.

So the drop at floor `dp[e-1][m-1] + 1` is optimal, and the total coverage is:

$$
dp[e][m] = dp[e-1][m-1] + 1 + dp[e][m-1]
$$

(one floor for the drop itself + what you can cover below + what you can cover above). With one move, one egg covers 1 floor (`dp[1][1] = 1`); with no floors to test, `dp[e][0] = 0`. The recurrence builds the whole "coverage" table, and the answer is the first `m` where `dp[k][m] >= n`.

**Why this beats the direct DP:** computing a single column `m` costs $O(k)$ (one pass over eggs), and the loop runs until coverage ≥ n — at most $\lceil \log_2 n \rceil$... no, actually up to `n` moves in the worst case with 1 egg, but with k ≥ 2 it's $O(\sqrt n)$-ish; more precisely the loop runs `m` times and each column is $O(k)$, giving $O(k \cdot m)$ where `m` is the answer — tiny in practice (e.g. 14 for k=2, n=100).

## Approach 1 — Direct DP (the "obvious" recurrence)

The $\min\max$ recurrence above, computed over all floors: $O(k n^2)$ time, $O(kn)$ space. Works for small inputs, and a good *first* answer to narrate — then the interviewer says "n is 10^4" and you invert.

## Approach 2 — Inverted-state DP (optimal)

```kotlin
/**
 * @param k the number of eggs available
 * @param n the number of floors in the building
 * @return  the minimum number of drops (worst case) to determine the critical floor
 */
fun superEggDrop(k: Int, n: Int): Int {
    // dp[eggs][moves] = max floors decidable with `eggs` eggs and `moves` moves.
    val dp = Array(k + 1) { IntArray(n + 1) }
    var moves = 0

    // Grow the number of moves until k eggs can cover all n floors.
    while (dp[k][moves] < n) {
        moves++
        for (eggs in 1..k) {
            dp[eggs][moves] = dp[eggs][moves - 1] + dp[eggs - 1][moves - 1] + 1
        }
    }
    return moves
}
```

```java
public class SuperEggDrop {
    /**
     * @param k the number of eggs available
     * @param n the number of floors in the building
     * @return  the minimum number of drops (worst case) to determine the critical floor
     */
    public int superEggDrop(int k, int n) {
        int[][] dp = new int[k + 1][n + 1];   // dp[eggs][moves] = floors coverable
        int moves = 0;
        while (dp[k][moves] < n) {
            moves++;
            for (int eggs = 1; eggs <= k; eggs++) {
                dp[eggs][moves] = dp[eggs][moves - 1] + dp[eggs - 1][moves - 1] + 1;
            }
        }
        return moves;
    }
}
```

```cpp
#include <vector>

class SuperEggDrop {
public:
    /**
     * @param k the number of eggs available
     * @param n the number of floors in the building
     * @return  the minimum number of drops (worst case) to determine the critical floor
     */
    int superEggDrop(int k, int n) {
        std::vector<std::vector<int>> dp(k + 1, std::vector<int>(n + 1, 0));
        int moves = 0;
        while (dp[k][moves] < n) {
            moves++;
            for (int eggs = 1; eggs <= k; eggs++) {
                dp[eggs][moves] = dp[eggs][moves - 1] + dp[eggs - 1][moves - 1] + 1;
            }
        }
        return moves;
    }
};
```

```python
def super_egg_drop(k: int, n: int) -> int:
    """
    @param k: the number of eggs available
    @param n: the number of floors in the building
    @return:  the minimum number of drops (worst case) to determine the critical floor
    """
    # dp[eggs][moves] = max floors decidable with `eggs` eggs and `moves` moves
    dp = [[0] * (n + 1) for _ in range(k + 1)]
    moves = 0
    while dp[k][moves] < n:
        moves += 1
        for eggs in range(1, k + 1):
            dp[eggs][moves] = dp[eggs][moves - 1] + dp[eggs - 1][moves - 1] + 1
    return moves
```

```rust
impl Solution {
    /// @param k the number of eggs available
    /// @param n the number of floors in the building
    /// @return  the minimum number of drops (worst case) to determine the critical floor
    pub fn super_egg_drop(k: i32, n: i32) -> i32 {
        let k = k as usize;
        let n = n as usize;
        let mut dp = vec![vec![0usize; n + 1]; k + 1];   // dp[eggs][moves] = floors coverable
        let mut moves = 0;
        while dp[k][moves] < n {
            moves += 1;
            for eggs in 1..=k {
                dp[eggs][moves] = dp[eggs][moves - 1] + dp[eggs - 1][moves - 1] + 1;
            }
        }
        moves as i32
    }
}
```

## Dry run — watch the coverage table grow

**Input:** `k = 2`, `n = 6`. Table (rows = eggs, columns = moves):

```
        moves:   0   1   2   3
eggs=0          0   0   0   0
eggs=1          0   1   2   3        (1 egg, m moves covers m floors: linear scan)
eggs=2          0   1   3   6        (2 eggs, 3 moves covers 6 floors!)
```

Trace the cells:

```
moves=1: dp[1][1] = dp[1][0] + dp[0][0] + 1 = 1
         dp[2][1] = dp[2][0] + dp[1][0] + 1 = 1
moves=2: dp[1][2] = 0 + 1 + 1 = 2          (1 egg: floors 1, 2)
         dp[2][2] = dp[2][1] + dp[1][1] + 1 = 1 + 1 + 1 = 3
moves=3: dp[2][3] = dp[2][2] + dp[1][2] + 1 = 3 + 2 + 1 = 6
```

Loop check: after moves=1, `dp[2][1] = 1 < 6` → keep going. After moves=2, `dp[2][2] = 3 < 6` → keep going. After moves=3, `dp[2][3] = 6 >= 6` → **return 3**. ✓ (matches the example)

**The strategy behind 6 floors in 3 drops with 2 eggs:** drop from floor `dp[1][2] + 1 = 3`. If it breaks → 1 egg, 2 moves left, floors 1–2 (linear scan). If it survives → 2 eggs, 2 moves left, floors 4–6, and by symmetry those 3 floors are coverable in 2 moves (drop from 5, etc.). Each move "spends" the current drop plus recursively covers both branches — exactly what the recurrence adds up.

## Complexity

**Time.** The loop runs `m` times (m = answer), each pass is $O(k)$:

$$
T(k, n) = O(k \cdot m), \qquad m = \text{the answer}
$$

The answer grows slowly: for $k \ge 2$ it's $O(\sqrt{n})$ in the worst egg-constrained case and $O(\log n)$ when $k$ is large (the binary-search-with-many-eggs regime). With $n = 10^4$ and $k \le 100$: at most a few hundred column updates. (The direct DP would need $k \cdot n^2 = 10^{10}$ cells of work.)

**Space.** The table is $(k+1) \times (m+1)$ — roughly $O(k \cdot m)$, and since each cell reads only the *previous column*, it can be rolled to $O(k)$.

## Variants & follow-ups

- **Interview follow-up:** "Why does 1 egg take exactly n drops?" With one egg you cannot afford a break (no egg left to continue), so you must scan linearly from floor 1 — the coverage table's `dp[1][m] = m` is that scan.
- **Interview follow-up:** "Where does the $+1$ in the recurrence come from?" The current drop itself tests *one* specific floor; everything else is what the two outcomes let you explore. The recurrence is the formal way of saying "one drop buys you a floor plus two subproblems."
- **Interview follow-up:** "Relate this to binary search." With unlimited eggs (`k ≥ log n`), the strategy is pure binary search — `dp[k][m] = 2^m - 1` (the recurrence telescopes to a geometric series). The table smoothly interpolates between linear scan (1 egg) and binary search (many eggs).
