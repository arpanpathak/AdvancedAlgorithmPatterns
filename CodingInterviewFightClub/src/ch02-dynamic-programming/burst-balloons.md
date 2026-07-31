# 2.20 Burst Balloons

> **Source:** [`src/main/kotlin/array/dp/BurstBallonsClean.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/BurstBallonsClean.kt) (+ `BurstBaloons.kt`)
> **Pattern:** interval DP with sentinels · **Core page**

## The Problem

Given `nums[i]` (balloon values), burst them one by one; bursting balloon `i` scores `nums[left] * nums[i] * nums[right]` (neighbors at that moment). Maximize total score.

- Constraints: $1 \le n \le 300$; values fit in `Int`.

## Examples

```
Input:  nums = [3,1,5,8]   -> Output: 167   (burst order 1, 5, 3, 8)
Input:  nums = [1,5]       -> Output: 10    (5 + 1·5·1... the padded view: burst 5 then 1)
```

## Intuition — think in terms of the *last* balloon, padded with sentinel 1s

The classic trick: **work backwards**. Define `solve(left, right)` = max coins from bursting all balloons *between* `left` and `right` (exclusive), assuming `left` and `right` are **already-burst sentinels** that stay. Then for the *last* balloon `k` burst in that range, its neighbors are exactly `balloons[left]` and `balloons[right]` — the pad `[1] + nums + [1]` makes the boundary scoring uniform:

$$
solve(left, right) = \max_{k \in (left, right)} \Big( A_{left} \cdot A_k \cdot A_{right} + solve(left, k) + solve(k, right) \Big)
$$

**Why "last balloon" and not "first"?** If `k` is burst *last*, the sub-ranges `(left, k)` and `(k, right)` are independent — their balloons are gone before `k` pops, and `k`'s neighbors are the fixed sentinels. Burst-first thinking entangles the ranges; burst-last decomposes them. This is the [2.10](minimum-cost-to-cut-a-stick.md) interval-DP structure with the direction flipped.

**The repo's one-expression form** (`BurstBallonsClean.kt`):

```kotlin
typealias State = Pair<Int, Int>

fun maxCoins(nums: IntArray): Int {
    val ballons = intArrayOf(1) + nums + 1        // sentinels
    val cache = mutableMapOf<State, Int>()

    fun solve(left: Int, right: Int): Int = cache.getOrPut(left to right) {
        when {
            left > right -> 0                     // empty range
            else -> (left..right).maxOf { k ->
                ballons[left - 1] * ballons[k] * ballons[right + 1] +
                        solve(left, k - 1) + solve(k + 1, right)
            }
        }
    }
    return solve(1, ballons.size - 2)
}
```

**What's cool:** the whole recurrence is a `getOrPut` + `maxOf` (the [19.12-era](../ch02-dynamic-programming/pattern-primer.md) one-expression DP style, now in the main chapter). `solve(1, size - 2)` is "all real balloons, sentinels outside".

## Approach 1 — Brute force burst orders (n!)

Try every permutation: correct, factorial — the baseline.

## Approach 2 — Interval DP with sentinels (the repo's clean version, optimal)

```kotlin
class BurstBallonsClean {
    fun maxCoins(nums: IntArray): Int {
        val ballons = intArrayOf(1) + nums + 1
        val cache = mutableMapOf<Pair<Int, Int>, Int>()

        fun solve(left: Int, right: Int): Int = cache.getOrPut(left to right) {
            when {
                left > right -> 0
                else -> (left..right).maxOf { k ->
                    ballons[left - 1] * ballons[k] * ballons[right + 1] +
                            solve(left, k - 1) + solve(k + 1, right)
                }
            }
        }
        return solve(1, ballons.size - 2)
    }
}
```

```java
import java.util.*;

public class BurstBalloons {
    /**
     * @param nums balloon values
     * @return     max coins from bursting all balloons
     */
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] a = new int[n + 2];
        a[0] = a[n + 1] = 1;                       // sentinels
        for (int i = 0; i < n; i++) a[i + 1] = nums[i];

        int[][] dp = new int[n + 2][n + 2];

        for (int len = 1; len <= n; len++) {       // window length
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;
                for (int k = left; k <= right; k++) {
                    int score = a[left - 1] * a[k] * a[right + 1]
                            + dp[left][k - 1] + dp[k + 1][right];
                    dp[left][right] = Math.max(dp[left][right], score);
                }
            }
        }
        return dp[1][n];
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class BurstBalloons {
public:
    /**
     * @param nums balloon values
     * @return     max coins from bursting all balloons
     */
    int maxCoins(std::vector<int>& nums) {
        int n = nums.size();
        std::vector<int> a(n + 2, 1);              // sentinels
        for (int i = 0; i < n; i++) a[i + 1] = nums[i];

        std::vector<std::vector<int>> dp(n + 2, std::vector<int>(n + 2, 0));

        for (int len = 1; len <= n; len++) {
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;
                for (int k = left; k <= right; k++) {
                    dp[left][right] = std::max(dp[left][right],
                        a[left - 1] * a[k] * a[right + 1] + dp[left][k - 1] + dp[k + 1][right]);
                }
            }
        }
        return dp[1][n];
    }
};
```

```python
def max_coins(nums: list[int]) -> int:
    """
    @param nums: balloon values
    @return:     max coins from bursting all balloons
    """
    a = [1] + nums + [1]                     # sentinels
    n = len(nums)
    dp = [[0] * (n + 2) for _ in range(n + 2)]

    for length in range(1, n + 1):           # window length
        for left in range(1, n - length + 2):
            right = left + length - 1
            for k in range(left, right + 1):
                score = a[left - 1] * a[k] * a[right + 1] + dp[left][k - 1] + dp[k + 1][right]
                dp[left][right] = max(dp[left][right], score)
    return dp[1][n]
```

```rust
impl Solution {
    /// @param nums balloon values
    /// @return     max coins from bursting all balloons
    pub fn max_coins(nums: Vec<i32>) -> i32 {
        let n = nums.len();
        let mut a = vec![1; n + 2];          // sentinels
        for (i, &v) in nums.iter().enumerate() { a[i + 1] = v; }

        let mut dp = vec![vec![0i32; n + 2]; n + 2];

        for len in 1..=n {
            for left in 1..=(n - len + 1) {
                let right = left + len - 1;
                for k in left..=right {
                    dp[left][right] = dp[left][right].max(
                        a[left - 1] * a[k] * a[right + 1] + dp[left][k - 1] + dp[k + 1][right]);
                }
            }
        }
        dp[1][n]
    }
}
```

## Dry run

**Input:** `nums = [3,1,5,8]`. Padded: `a = [1,3,1,5,8,1]`.

```
length-1 windows: dp[1][1] = a[0]*a[1]*a[2] = 1*3*1 = 3.   (burst 3 alone: 3)
                 dp[2][2] = a[1]*a[2]*a[3] = 3*1*5 = 15.  dp[3][3] = 1*5*8 = 40.  dp[4][4] = 5*8*1 = 40.
length-2: dp[1][2] = max(k=1: a0*a1*a3 + dp[2][2] = 1*3*5+15 = 30,
                         k=2: a0*a2*a3 + dp[1][1] = 1*1*5+3 = 8)  = 30.
          dp[2][3] = max(k=2: a1*a2*a4 + dp[3][3] = 3*1*8+40 = 64,
                         k=3: a1*a3*a4 + dp[2][2] = 3*5*8+15 = 135) = 135.
          dp[3][4] = max(k=3: a2*a3*a5 + dp[4][4] = 1*5*1+40 = 45,
                         k=4: a2*a4*a5 + dp[3][3] = 1*8*1+40 = 48) = 48.
length-3: dp[1][3] = max(k=1: a0*a1*a4 + dp[2][3] = 1*3*8+135 = 159,
                         k=2: a0*a2*a4 + dp[1][1]+dp[3][3] = 1*1*8+3+40 = 51,
                         k=3: a0*a3*a4 + dp[1][2] = 1*5*8+30 = 70) = 159.
length-4: dp[1][4] = max(k=1: a0*a1*a5 + dp[2][4] = 1*3*1+48 = 51,
                         k=2: a0*a2*a5 + dp[1][1]+dp[3][4] = 1*1*1+3+48 = 52,
                         k=3: a0*a3*a5 + dp[1][2]+dp[4][4] = 1*5*1+30+40 = 75,
                         k=4: a0*a4*a5 + dp[1][3] = 1*8*1+159 = 167) = 167.

Output: 167 ✓
```

The last-balloon reading of the winning `k=4` (burst 8 last): the 8's neighbors are the sentinels `a[0]` and `a[5]` (both 1) → 8 points, plus the optimal `dp[1][3] = 159` from the remaining three — the ranges decompose exactly because 8 goes last.

## Complexity

**Time.** All (left, k, right) triples:

$$
T(n) = O(n^3)
$$

**Space.** The interval table:

$$
S(n) = O(n^2)
$$

## Variants & follow-ups

- **Minimum Cost To Cut A Stick** ([2.10](minimum-cost-to-cut-a-stick.md)) — the interval-DP sibling (min instead of max, cut-first instead of burst-last).
- **Stone Game** — the zero-sum interval DP (now a section on the [2.10](minimum-cost-to-cut-a-stick.md) page).
- **Interview follow-up:** "Why pad with 1s?" Without sentinels, the *first* and *last* bursts have only one neighbor — a special case. The `[1] + nums + [1]` pad makes every burst uniformly `left × k × right`, so the recurrence needs no boundary branches.
