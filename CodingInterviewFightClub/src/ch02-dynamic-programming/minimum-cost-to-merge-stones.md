# 2.11 Minimum Cost To Merge Stones

> **Source:** [`src/main/kotlin/dynamic_programming/MinimumCostToMergeStones.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/MinimumCostToMergeStones.kt) · [`MinimumCostToMergeStones_Intuition.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/MinimumCostToMergeStones_Intuition.kt)
> **Pattern:** interval DP with a pile-count dimension · **Gym boss**

## The Problem

You have `n` piles of stones in a row, `stones[i]` stones in pile `i`. You may merge **exactly `k` consecutive piles** into one pile, paying a cost equal to the **total stones in those k piles**. Repeat until one pile remains. Find the **minimum total cost**, or `-1` if impossible.

- Constraints: $1 \le n \le 30$, $1 \le k \le 30$, $n \le 1000$ stones per pile.

## Examples

```
stones = [3, 2, 4, 1], k = 2
Output: 20
Explanation: merge 3+2=5 (cost 5) → [5,4,1]; merge 5+4=9 (cost 9) → [9,1];
             merge 9+1=10 (cost 10) → [10]. Total = 5+9+10 = 24? Hmm — the optimal
             is 3+2=5 → [5,4,1]; 4+1=5 → [5,5]; 5+5=10 → total 5+5+10 = 20 ✓

stones = [3, 2, 4, 1], k = 3
Output: -1
Explanation: 4 piles, k=3: each merge reduces the count by 2. 4 → 2 → 0 piles... 
             can't reach 1. (n-1) % (k-1) = 3 % 2 = 1 ≠ 0 → impossible.
```

## Intuition — two layers of structure

Two ideas stack on top of each other:

**Idea 1 — when is it even possible?** Each merge turns `k` piles into 1, reducing the pile count by exactly `k-1`. To go from `n` piles to 1 we must reduce by `n-1`:

$$
(n - 1) \bmod (k - 1) = 0 \quad \Longleftrightarrow \quad \text{possible}
$$

If the parity is wrong, no merge sequence exists — return `-1` immediately.

**Idea 2 — the 3D state.** After merging inside a subarray `[i, j]`, the number of piles that range collapses to depends on the boundaries. Let:

$$
dp[i][j][m] = \text{min cost to reduce } stones[i..j] \text{ to exactly } m \text{ piles}
$$

Recurrences:

- **Reduce to 1 pile:** a range becomes 1 pile only after becoming `k` piles and merging them once. The final merge costs the whole range's total stones (prefix-sum lookup):

$$
dp[i][j][1] = dp[i][j][k] + (prefix[j+1] - prefix[i])
$$

- **Reduce to m piles (1 < m < k):** split `[i, j]` into `[i, p]` (reduced to 1 pile) and `[p+1, j]` (reduced to `m-1` piles). The merge of those `m` piles happens *later*; the cost here is just the sub-costs:

$$
dp[i][j][m] = \min_{p \in \{i, i+(k-1), i+2(k-1), \dots\}} \big(dp[i][p][1] + dp[p+1][j][m-1]\big)
$$

**Why step `p` by `k-1`?** `[i, p]` must be reducible to exactly 1 pile, which by Idea 1 requires `(p - i) % (k - 1) == 0`. Skipping impossible split points is both a correctness requirement and a speedup.

**Why the `m` dimension at all?** Because ranges don't always reduce to 1 pile — intermediate ranges hold `m` piles that will be merged with *neighboring* piles later. The dimension tracks exactly that "pending merge" state. This is what separates this problem from plain merge-cost DPs.

## Approach — 3D memoized interval DP (optimal)

```kotlin
/**
 * @param stones the number of stones in each pile (in order)
 * @param k      exactly k consecutive piles must be merged at each step
 * @return       the minimum total merge cost, or -1 if impossible
 */
fun mergeStones(stones: IntArray, k: Int): Int {
    val n = stones.size

    // Parity check: each merge removes (k-1) piles; reaching 1 pile removes (n-1).
    if ((n - 1) % (k - 1) != 0) return -1

    val prefixSum = IntArray(n + 1)
    stones.forEachIndexed { index, stone -> prefixSum[index + 1] = prefixSum[index] + stone }

    // dp[i][j][m] = min cost to reduce stones[i..j] to exactly m piles; -1 = unknown
    val dp = Array(n) { Array(n) { IntArray(k + 1) { -1 } } }

    /**
     * @param i start index of the range
     * @param j end index of the range (inclusive)
     * @param m target pile count for this range
     * @return  min cost to reduce stones[i..j] to m piles
     */
    fun solve(i: Int, j: Int, m: Int): Int = when {
        dp[i][j][m] != -1 -> dp[i][j][m]

        i == j -> if (m == 1) 0 else Int.MAX_VALUE   // single pile: already 1 pile

        // To make 1 pile: first make k piles, then do ONE final merge of the whole range.
        m == 1 -> solve(i, j, k) + (prefixSum[j + 1] - prefixSum[i])

        // To make m piles: split [i, p] into 1 pile, [p+1, j] into (m-1) piles.
        else -> {
            var minCost = Int.MAX_VALUE
            // p must satisfy (p - i) % (k - 1) == 0 so [i, p] can collapse to 1 pile.
            for (p in i until j step k - 1) {
                val left = solve(i, p, 1)
                val right = solve(p + 1, j, m - 1)
                if (left != Int.MAX_VALUE && right != Int.MAX_VALUE) {
                    minCost = minOf(minCost, left + right)
                }
            }
            minCost
        }
    }.also { dp[i][j][m] = it }

    return solve(0, n - 1, 1)
}
```

```java
import java.util.Arrays;

public class MinimumCostToMergeStones {
    private int[] prefix;
    private int[][][] memo;
    private int k;

    /**
     * @param stones the number of stones in each pile (in order)
     * @param k      exactly k consecutive piles must be merged at each step
     * @return       the minimum total merge cost, or -1 if impossible
     */
    public int mergeStones(int[] stones, int k) {
        int n = stones.length;
        if ((n - 1) % (k - 1) != 0) return -1;            // parity check

        prefix = new int[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + stones[i];
        memo = new int[n][n][k + 1];
        for (int[][] a : memo) for (int[] b : a) Arrays.fill(b, -1);
        this.k = k;
        return solve(0, n - 1, 1);
    }

    /**
     * @param i start index of the range
     * @param j end index of the range (inclusive)
     * @param m target pile count for this range
     * @return  min cost to reduce stones[i..j] to m piles
     */
    private int solve(int i, int j, int m) {
        if (memo[i][j][m] != -1) return memo[i][j][m];
        if (i == j) return m == 1 ? 0 : Integer.MAX_VALUE;

        int best = Integer.MAX_VALUE;
        if (m == 1) {
            int sub = solve(i, j, k);
            if (sub != Integer.MAX_VALUE) best = sub + (prefix[j + 1] - prefix[i]);
        } else {
            for (int p = i; p < j; p += k - 1) {           // step by k-1: divisibility
                int left = solve(i, p, 1);
                int right = solve(p + 1, j, m - 1);
                if (left != Integer.MAX_VALUE && right != Integer.MAX_VALUE) {
                    best = Math.min(best, left + right);
                }
            }
        }
        return memo[i][j][m] = best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <climits>

class MinimumCostToMergeStones {
    std::vector<int> prefix;
    std::vector<std::vector<std::vector<int>>> memo;
    int k;

public:
    /**
     * @param stones the number of stones in each pile (in order)
     * @param k      exactly k consecutive piles must be merged at each step
     * @return       the minimum total merge cost, or -1 if impossible
     */
    int mergeStones(const std::vector<int>& stones, int k) {
        int n = (int)stones.size();
        if ((n - 1) % (k - 1) != 0) return -1;

        prefix.assign(n + 1, 0);
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + stones[i];
        memo.assign(n, std::vector<std::vector<int>>(n, std::vector<int>(k + 1, -1)));
        this->k = k;
        return solve(0, n - 1, 1);
    }

private:
    int solve(int i, int j, int m) {
        if (memo[i][j][m] != -1) return memo[i][j][m];
        if (i == j) return m == 1 ? 0 : INT_MAX;

        int best = INT_MAX;
        if (m == 1) {
            int sub = solve(i, j, k);
            if (sub != INT_MAX) best = sub + (prefix[j + 1] - prefix[i]);
        } else {
            for (int p = i; p < j; p += k - 1) {
                int left = solve(i, p, 1);
                int right = solve(p + 1, j, m - 1);
                if (left != INT_MAX && right != INT_MAX) best = std::min(best, left + right);
            }
        }
        return memo[i][j][m] = best;
    }
};
```

```python
def merge_stones(stones: list[int], k: int) -> int:
    """
    @param stones: the number of stones in each pile (in order)
    @param k:      exactly k consecutive piles must be merged at each step
    @return:       the minimum total merge cost, or -1 if impossible
    """
    n = len(stones)
    if (n - 1) % (k - 1) != 0:
        return -1                                  # parity check

    prefix = [0] * (n + 1)
    for i, s in enumerate(stones):
        prefix[i + 1] = prefix[i] + s

    from functools import lru_cache

    @lru_cache(None)
    def solve(i: int, j: int, m: int) -> int:
        """
        @param i: start index of the range
        @param j: end index of the range (inclusive)
        @param m: target pile count for this range
        @return:  min cost to reduce stones[i..j] to m piles
        """
        if i == j:
            return 0 if m == 1 else float("inf")
        if m == 1:                                 # first make k piles, then one final merge
            sub = solve(i, j, k)
            return sub + (prefix[j + 1] - prefix[i]) if sub != float("inf") else float("inf")
        best = float("inf")
        for p in range(i, j, k - 1):               # step by k-1: [i,p] must collapse to 1 pile
            left, right = solve(i, p, 1), solve(p + 1, j, m - 1)
            if left != float("inf") and right != float("inf"):
                best = min(best, left + right)
        return best

    return solve(0, n - 1, 1)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param stones the number of stones in each pile (in order)
    /// @param k      exactly k consecutive piles must be merged at each step
    /// @return       the minimum total merge cost, or -1 if impossible
    pub fn merge_stones(stones: Vec<i32>, k: i32) -> i32 {
        let n = stones.len();
        let k = k as usize;
        if (n - 1) % (k - 1) != 0 {
            return -1;
        }
        let mut prefix = vec![0i32; n + 1];
        for i in 0..n {
            prefix[i + 1] = prefix[i] + stones[i];
        }

        let mut memo: HashMap<(usize, usize, usize), i32> = HashMap::new();
        fn solve(
            memo: &mut HashMap<(usize, usize, usize), i32>,
            prefix: &[i32],
            k: usize,
            i: usize,
            j: usize,
            m: usize,
        ) -> i32 {
            if let Some(&v) = memo.get(&(i, j, m)) {
                return v;
            }
            let result = if i == j {
                if m == 1 { 0 } else { i32::MAX }
            } else if m == 1 {
                let sub = solve(memo, prefix, k, i, j, k);
                if sub == i32::MAX {
                    i32::MAX
                } else {
                    sub + (prefix[j + 1] - prefix[i])
                }
            } else {
                let mut best = i32::MAX;
                let mut p = i;
                while p < j {
                    let left = solve(memo, prefix, k, i, p, 1);
                    let right = solve(memo, prefix, k, p + 1, j, m - 1);
                    if left != i32::MAX && right != i32::MAX {
                        best = best.min(left + right);
                    }
                    p += k - 1;                      // step by k-1: divisibility
                }
                best
            };
            memo.insert((i, j, m), result);
            result
        }
        solve(&mut memo, &prefix, k, 0, n - 1, 1)
    }
}
```

## Dry run

**Input:** `stones = [3, 2, 4, 1]`, `k = 2`. Parity: `(4-1) % (2-1) = 0` ✓.

`k = 2` makes every split point valid (step 1), and `m ∈ {1, 2}`. Trace the key cells:

```
i == j cases: solve(i,i,1) = 0; solve(i,i,2) = inf

Length-2 ranges (m=1): solve(0,1,1) = solve(0,1,2) + (3+2) = (left+right)+5
   solve(0,1,2): split p=0: solve(0,0,1)=0 + solve(1,1,1)=0 = 0
   solve(0,1,1) = 0 + 5 = 5        (merge 3,2 → cost 5)
   solve(1,2,1) = 0 + (2+4) = 6    (merge 2,4)
   solve(2,3,1) = 0 + (4+1) = 5    (merge 4,1)

Length-3 (m=1): solve(0,2,1): needs solve(0,2,2)
   solve(0,2,2): p=0: solve(0,0,1)=0 + solve(1,2,1)=6 = 6
                 p=1: solve(0,1,1)=5 + solve(2,2,1)=0 = 5  → 5
   solve(0,2,1) = 5 + (3+2+4)=9 = 14
   solve(1,3,1): solve(1,3,2): p=1: 0 + solve(2,3,1)=5 = 5
                               p=2: solve(1,2,1)=6 + 0 = 6 → 5
   solve(1,3,1) = 5 + (2+4+1)=7 = 12

Length-4 (the answer): solve(0,3,1): needs solve(0,3,2)
   solve(0,3,2): p=0: solve(0,0,1)=0 + solve(1,3,1)=12 = 12
                 p=1: solve(0,1,1)=5 + solve(2,3,1)=5  = 10   ← best
                 p=2: solve(0,2,1)=14 + solve(3,3,1)=0 = 14   → 10
   solve(0,3,1) = 10 + (3+2+4+1)=10 = 20
Answer: 20 ✓
```

The trace reveals the optimal plan: merge `[0,1]` (3+2=5, cost 5), merge `[2,3]` (4+1=5, cost 5) — now two piles `[5, 5]` — then the final merge costs 10. Total 20, matching the example. Note the two *independent* merges happen first, and the final merge of the whole range is exactly the `+ (prefix[j+1] - prefix[i])` term.

**Impossible case:** `stones = [3,2,4,1]`, `k = 3` → `(4-1) % (3-1) = 3 % 2 = 1 ≠ 0` → return `-1` before any DP work.

## Complexity

**Time.** $O(n^3)$ states-ish: $n^2$ ranges × $k$ pile-counts × $n/k$ split points:

$$
T(n) = O\!\left(\frac{n^3}{k}\right) \cdot O(1) \approx O(n^3)
$$

With `n ≤ 30`: a few thousand cells — instant.

**Space.** $O(n^2 k)$ for the memo table.

## Variants & follow-ups

- **[2.10](minimum-cost-to-cut-a-stick.md)** — the simpler interval DP; same "last operation" reasoning, no `m` dimension.
- **Burst Balloons** (`src/main/kotlin/array/dp/BurstBaloons.kt`) — same family; fix the *last* balloon and the subintervals become independent.
- **Stone Game** (`src/main/kotlin/array/dp/StoneGame.kt`) — a different game on the same stones, solved by minimax DP; the contrast is a great "which DP is this?" drill.
- **Interview follow-up:** "Why does the `m == 1` case need `solve(i, j, k)` *before* adding the total?" Because a range can only become 1 pile by first becoming `k` piles and then merging them all at once — the `+total` is that one final merge, and it must happen *after* the range is reduced to k piles.
- **Interview follow-up:** "What changes for `k = 2`?" The problem degenerates to the classic matrix-chain/merge cost (every split valid, `m` only ever 1 or 2) — a good sanity check that the general solution reduces correctly.
