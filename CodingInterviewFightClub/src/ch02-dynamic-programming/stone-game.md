# 2.36 Stone Game

> **Source**: [`src/main/kotlin/array/dp/StoneGame.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/StoneGame.kt)
> **Pattern**: relative-score range DP · **Core page**

## The Problem

Two players take stones from either end; the max total wins. Can the first player win?

- Constraints: n even; piles[i] ≥ 1.

## Examples

```
Input:  piles = [5,3,4,5]   -> Output: true
```

## Intuition — the score *difference* from each range, memoized

`dp[i][j]` = current player's score minus opponent's over piles[i..j]. Take left or right:

```kotlin
fun solve(i: Int, j: Int): Int {
    if (i > j) return 0

    val key = i to j
    if (key in cache) return cache[key]!!

    return maxOf(
        piles[i] - solve(i + 1, j),   // take left: opponent gets the rest
        piles[j] - solve(i, j - 1)    // take right
    ).also { cache[key] = it }
}
return solve(0, n - 1) > 0
```

**Why the difference?** No turn tracking — `current − opponent` flips sign each move; the max over both ends is the best relative advantage. The [2.0](../ch02-dynamic-programming/pattern-primer.md) range DP.

## Approach 1 — Memoized difference (the repo's version, optimal)

## Approach 2 — The parity shortcut

With n even and piles[i] ≥ 1, the first player always wins (odd/even-index sums differ) — but the DP is the honest proof.

```kotlin
class StoneGame {
    /**
     * @param piles stone piles
     * @return     true iff the first player can win
     */
    fun stoneGame(piles: IntArray): Boolean {
        val cache = mutableMapOf<Pair<Int, Int>, Int>()

        fun solve(i: Int, j: Int): Int {
            if (i > j) return 0

            val key = i to j
            if (key in cache) return cache[key]!!

            return maxOf(
                piles[i] - solve(i + 1, j),
                piles[j] - solve(i, j - 1)
            ).also { cache[key] = it }
        }

        return solve(0, piles.size - 1) > 0
    }
}
```

```java
import java.util.*;

public class StoneGame {
    private int[] piles;
    private int[][] memo;

    private int solve(int i, int j) {
        if (i > j) return 0;
        if (memo[i][j] != 0) return memo[i][j];

        return memo[i][j] = Math.max(
            piles[i] - solve(i + 1, j),
            piles[j] - solve(i, j - 1));
    }

    /**
     * @param piles stone piles
     * @return     true iff the first player can win
     */
    public boolean stoneGame(int[] piles) {
        this.piles = piles;
        memo = new int[piles.length][piles.length];
        return solve(0, piles.length - 1) > 0;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class StoneGame {
    int solve(std::vector<int>& piles, int i, int j, std::vector<std::vector<int>>& memo) {
        if (i > j) return 0;
        if (memo[i][j]) return memo[i][j];

        return memo[i][j] = std::max(
            piles[i] - solve(piles, i + 1, j, memo),
            piles[j] - solve(piles, i, j - 1, memo));
    }

public:
    /**
     * @param piles stone piles
     * @return     true iff the first player can win
     */
    bool stoneGame(std::vector<int>& piles) {
        std::vector<std::vector<int>> memo(piles.size(), std::vector<int>(piles.size(), 0));
        return solve(piles, 0, piles.size() - 1, memo) > 0;
    }
};
```

```python
def stone_game(piles: list[int]) -> bool:
    """
    @param piles: stone piles
    @return:      true iff the first player can win
    """
    from functools import lru_cache

    @lru_cache(None)
    def solve(i: int, j: int) -> int:
        if i > j:
            return 0
        return max(piles[i] - solve(i + 1, j), piles[j] - solve(i, j - 1))

    return solve(0, len(piles) - 1) > 0
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param piles stone piles
    /// @return     true iff the first player can win
    pub fn stone_game(piles: Vec<i32>) -> bool {
        let n = piles.len();
        let mut memo = HashMap::new();

        fn solve(piles: &Vec<i32>, i: usize, j: usize, memo: &mut HashMap<(usize, usize), i32>) -> i32 {
            if i > j { return 0; }
            if let Some(&v) = memo.get(&(i, j)) { return v; }

            let result = (piles[i] - solve(piles, i + 1, j, memo))
                .max(piles[j] - solve(piles, i, j.wrapping_sub(1), memo));
            memo.insert((i, j), result);
            result
        }

        solve(&piles, 0, n - 1, &mut memo) > 0
    }
}
```

## Dry run

**Input:** `piles = [5,3,4,5]`.

```
solve(0,3): max(5 - solve(1,3), 5 - solve(0,2)).
solve(1,3): max(3 - solve(2,3), 5 - solve(1,2)) = max(3-1, 5-3) = 2.
solve(0,2): max(5 - solve(1,2), 4 - solve(0,1)) = max(5-3, 4-2) = 2.
solve(0,3) = max(5-2, 5-2) = 3 > 0 -> true ✓
```

## Complexity

**Time.** O(n²) states:

$$
T(n) = O(n^2)
$$

**Space.** The memo:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Predict The Winner** — the generic version (odd lengths allowed).
- **Interview follow-up:** "Why the difference and not two scores?" The single value encodes both players' optimal play — each move subtracts the opponent's future advantage, and the sign at the root decides the winner.
