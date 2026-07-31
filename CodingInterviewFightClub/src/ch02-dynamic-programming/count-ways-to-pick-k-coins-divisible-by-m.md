# 2.14 Count Ways To Pick K Coins Divisible By M

> **Source:** [`src/main/kotlin/google/CountNumberOfWaysToPickKCoinsSumDivisibleByM.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/google/CountNumberOfWaysToPickKCoinsSumDivisibleByM.kt)
> **Pattern:** memoized (index, count, remainder) · **Core page**

## The Problem

Coins are numbered `0..n-1`. Count how many ways to pick **exactly k coins** such that their sum is **divisible by m** (result mod $10^9+7$).

- Constraints: $1 \le k \le n$; `m` fits in `Int`.

## Examples

```
Input:  n = 4, k = 2, m = 3   -> Output: 2   ({0,3} and {1,2} both sum to a multiple of 3)
Input:  n = 5, k = 3, m = 3   -> Output: 2
```

## Intuition — the state is (index, picks left, remainder); the remainder *is* the carry

The count-with-a-condition DP needs three axes:

- `idx` — which coin we're deciding next;
- `k` — how many picks remain;
- `rem` — the running sum **modulo m** (the only part of the sum that matters for divisibility).

The recurrence is the classic pick/skip:

```
solve(idx, k, rem):
    k == 0        -> 1 iff rem == 0
    (n - idx) < k -> 0               (not enough coins left — pruning)
    else          -> solve(idx+1, k, rem)                 # skip coin idx
                  +  solve(idx+1, k-1, (rem + idx) % m)   # pick coin idx
```

**Why does `rem` carry modulo instead of the raw sum?** Only `sum % m` decides divisibility, and `(a + b) % m` is computable from `a % m` — so the remainder is a complete summary of the sum, bounded by `m` instead of by `n·m`. That's what keeps the state space at $O(n \cdot k \cdot m)$ rather than exponential.

**Why the `(n - idx) < k` pruning?** If fewer coins remain than picks needed, no completion exists — the branch dies without recursion. The same "remaining resources vs remaining needs" cut as [11.2](../ch11-greedy/jump-game-ii.md)'s reachability frontier, in DP clothing.

**The coins are 0-indexed** (the repo's comment: `coins = [0, 1, 2, 3]`), so picking coin `idx` adds `idx` to the sum — `(rem + idx) % m`. Careful: not `idx + 1`.

## Approach 1 — Enumerate all C(n, k) combinations (exponential)

Generate every k-subset and check the sum: correct, dies at n = 20.

## Approach 2 — Memoized (idx, k, rem) (the repo's version, optimal)

```kotlin
fun countWays(n: Int, k: Int, m: Int): Int {
    val mod = 1_000_000_007
    data class State(val idx: Int, val k: Int, val rem: Int)

    val _cache = mutableMapOf<State, Int>()

    fun solve(idx: Int, k: Int, rem: Int): Int =
        _cache.getOrPut(State(idx, k, rem)) {
            when {
                k == 0 -> if (rem == 0) 1 else 0
                // Pruning: if coins remaining (n - idx) < coins needed (k), stop
                (n - idx) < k || idx == n -> 0
                else -> {
                    val skip = solve(idx + 1, k, rem)
                    val pick = solve(idx + 1, k - 1, (rem + (idx % m)) % m)
                    (skip + pick) % mod
                }
            }
        }

    return solve(0, k, 0)
}
```

```java
import java.util.*;

public class CountWaysToPickKCoinsDivisibleByM {
    private static final int MOD = 1_000_000_007;

    /**
     * @param n number of coins (0..n-1)
     * @param k coins to pick
     * @param m divisor
     * @return  ways to pick k coins with sum divisible by m
     */
    public int countWays(int n, int k, int m) {
        Map<String, Integer> memo = new HashMap<>();
        return solve(0, k, 0, n, m, memo);
    }

    private int solve(int idx, int k, int rem, int n, int m, Map<String, Integer> memo) {
        if (k == 0) return rem == 0 ? 1 : 0;
        if (n - idx < k || idx == n) return 0;

        String key = idx + "," + k + "," + rem;
        if (memo.containsKey(key)) return memo.get(key);

        int skip = solve(idx + 1, k, rem, n, m, memo);
        int pick = solve(idx + 1, k - 1, (rem + idx) % m, n, m, memo);
        int result = (skip + pick) % MOD;
        memo.put(key, result);
        return result;
    }
}
```

```cpp
#include <cstring>

class CountWaysToPickKCoinsDivisibleByM {
    long long memo[31][31][31];
    int n, m, k;
    const long long MOD = 1'000'000'007LL;

    long long solve(int idx, int left, int rem) {
        if (left == 0) return rem == 0 ? 1 : 0;
        if (n - idx < left || idx == n) return 0;
        if (memo[idx][left][rem] != -1) return memo[idx][left][rem];

        long long skip = solve(idx + 1, left, rem);
        long long pick = solve(idx + 1, left - 1, (rem + idx) % m);
        return memo[idx][left][rem] = (skip + pick) % MOD;
    }

public:
    /**
     * @param n number of coins (0..n-1)
     * @param k coins to pick
     * @param m divisor
     * @return  ways to pick k coins with sum divisible by m
     */
    int countWays(int n, int k, int m) {
        this->n = n; this->k = k; this->m = m;
        std::memset(memo, -1, sizeof memo);
        return (int)solve(0, k, 0);
    }
};
```

```python
from functools import lru_cache

MOD = 1_000_000_007

def count_ways(n: int, k: int, m: int) -> int:
    """
    @param n: number of coins (0..n-1)
    @param k: coins to pick
    @param m: divisor
    @return:  ways to pick k coins with sum divisible by m
    """
    @lru_cache(None)
    def solve(idx: int, left: int, rem: int) -> int:
        if left == 0:
            return 1 if rem == 0 else 0
        if n - idx < left or idx == n:
            return 0                        # not enough coins left — pruning

        skip = solve(idx + 1, left, rem)
        pick = solve(idx + 1, left - 1, (rem + idx) % m)
        return (skip + pick) % MOD

    return solve(0, k, 0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param n number of coins (0..n-1)
    /// @param k coins to pick
    /// @param m divisor
    /// @return  ways to pick k coins with sum divisible by m
    pub fn count_ways(n: i32, k: i32, m: i32) -> i32 {
        const MOD: i64 = 1_000_000_007;
        let mut memo: HashMap<(i32, i32, i32), i64> = HashMap::new();

        fn solve(idx: i32, left: i32, rem: i32, n: i32, m: i32,
                 memo: &mut HashMap<(i32, i32, i32), i64>) -> i64 {
            if left == 0 { return if rem == 0 { 1 } else { 0 }; }
            if n - idx < left || idx == n { return 0; }      // pruning

            if let Some(&v) = memo.get(&(idx, left, rem)) { return v; }
            let skip = solve(idx + 1, left, rem, n, m, memo);
            let pick = solve(idx + 1, left - 1, (rem + idx) % m, n, m, memo);
            let v = (skip + pick) % MOD;
            memo.insert((idx, left, rem), v);
            v
        }

        solve(0, k, 0, n, m, &mut memo) as i32
    }
}
```

## Dry run

**Input:** `n = 4, k = 2, m = 3` — coins {0,1,2,3}, pick 2 with `sum % 3 == 0`.

```
enumerate (idx strictly increasing): pairs {0,3} -> 0+3=3 ✓, {1,2} -> 3 ✓.
All other pairs: 0+1=1, 0+2=2, 1+3=4≡1, 2+3=5≡2 -> fail.  Answer: 2.

DP path (abridged): solve(0,2,0)
  skip -> solve(1,2,0): eventually counts pairs among {1,2,3}: {1,2} ✓ -> 1
  pick -> solve(1,1,0%3=0): counts pairs starting with coin 0:
            pick coin 1 -> solve(2,0,1): rem 1 != 0 -> 0
            pick coin 2 -> solve(3,0,2): 0
            pick coin 3 -> solve(4,0,3%3=0): 1  -> the {0,3} pair ✓
  total = 1 + 1 = 2 ✓
```

The remainder carry in action: picking coin 3 adds `3 % 3 = 0`, so the state `(4, 0, 0)` closes the `{0,3}` choice — the raw sum never appears, only its residue. The pruning `(n - idx) < left` kills branches like "pick 2 coins from only 1 remaining" instantly.

## Complexity

**Time.** States `n × k × m`, O(1) per state:

$$
T(n, k, m) = O(n \cdot k \cdot m)
$$

**Space.** The memo:

$$
S(n, k, m) = O(n \cdot k \cdot m)
$$

## Variants & follow-ups

- **Target Sum** (`array/dp/TargetSum.kt`) — the same (index, remainder-carry) counting, with a signed target instead of a modulo.
- **Partition Equal Subset Sum** ([2.6](partition-equal-subset-sum.md)) — divisibility reachability without the pick-count axis.
- **Interview follow-up:** "Why does `rem` make the state small?" Only `sum % m` determines divisibility, and it composes under addition — so the remainder is a lossless summary of the sum, bounded by `m` (≤ 30 here). Replace the remainder with the raw sum and the state space explodes to $n \cdot k \cdot (n \cdot m)$.
