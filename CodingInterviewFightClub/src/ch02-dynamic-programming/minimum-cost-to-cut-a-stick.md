# 2.10 Minimum Cost To Cut A Stick

> **Source:** [`src/main/kotlin/dynamic_programming/MinimumCostToCutAStick.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/MinimumCostToCutAStick.kt)
> **Pattern:** interval DP · **Core page — the interval-DP template**

## The Problem

You have a wooden stick of length `n`. You must make all the cuts at the positions given in `cuts` (you can cut in any order). **The cost of a cut is the length of the stick segment you are cutting.** Minimize the total cost.

- Constraints: $2 \le n \le 10^6$, $1 \le cuts.length \le 100$.

## Examples

```
n = 7, cuts = [1, 3, 4, 5]
Output: 16
Explanation: cutting at 3 first (cost 7), then 1 (cost 3), then 4 (cost 3), then 5 (cost 3)? 
             Let's find the optimal order: cut 3 → cost 7 (whole stick).
             Left [0,3]: cut 1 → cost 3. Right [3,7]: cut 5 → cost 4, then 4 → cost 2.
             Total = 7+3+4+2 = 16.

n = 9, cuts = [5, 6, 1, 4, 2]
Output: 22
```

## Intuition — "the last cut" is the anchor

This is the canonical **interval DP**. The trap is thinking about the *first* cut; the insight is to think about the **last** cut.

Imagine a segment `[left, right]` (bounded by two *already-made* cuts). Whatever order we cut inside it, consider the **last cut made inside this segment**, at position `k`:

- Cutting the segment into `[left, k]` and `[k, right]` — *but by the time this is the last cut, both halves are already fully cut*.
- Total cost for `[left, right]` = (cost to fully cut `[left, k]`) + (cost to fully cut `[k, right]`) + **the cost of the last cut itself**, which is the current segment length `points[right] - points[left]` (because at that moment the two halves are still attached, so the segment being cut is the whole `[left, right]`).

That gives the recurrence:

$$
dp[left][right] = \min_{k \in (left, right)} \Big( dp[left][k] + dp[k][right] + \underbrace{(points[right] - points[left])}_{\text{cost of the last cut}} \Big)
$$

with `dp[left][left+1] = 0` (no cuts between two adjacent cut-points). The answer is `dp[0][m-1]` where `points = [0] + sorted(cuts) + [n]` — the cut positions plus the two ends of the stick.

**Why the last cut, not the first?** The cost of a cut depends on the *current* segment length, which is determined by which cuts have already been made. Reasoning forward, the first cut's cost is always `n` (fixed) but it splits into subproblems of *unknown* length relationships. Reasoning backward, when the last cut at `k` happens, the segment `[left, right]` is still whole (both halves uncut), so its cost is *deterministic*: `points[right] - points[left]`. Backward reasoning turns an unknown future into a known present.

**Why sort the cuts?** The interval `[left, right]` must be a *contiguous* range of cut positions; only with sorted cuts do the subintervals partition cleanly.

## Approach 1 — Try all permutations

There are `m!` orders of the `m` cuts. For `m = 100`: astronomically impossible. The DP collapses this to $O(m^3)$.

## Approach 2 — Interval DP (optimal)

```kotlin
/**
 * @param n    the length of the stick
 * @param cuts the positions where cuts must be made (any order)
 * @return     the minimum total cost to make all cuts
 */
fun minCost(n: Int, cuts: IntArray): Int {
    val points = (intArrayOf(0) + cuts.sortedArray() + n)   // [0, ...sorted cuts..., n]
    val m = points.size
    val cache = Array(m) { IntArray(m) { -1 } }

    /**
     * @param left  index of the left boundary cut-point
     * @param right index of the right boundary cut-point
     * @return      minimum cost to fully cut the segment (points[left], points[right])
     */
    fun solve(left: Int, right: Int): Int = when {
        cache[left][right] != -1 -> cache[left][right]
        left + 1 >= right        -> 0                     // no cuts inside: free
        else -> (left + 1 until right).minOf { k ->
            // Last cut at points[k]; the two halves are solved first, then this
            // whole segment is cut once, costing its full length.
            (points[right] - points[left]) + solve(left, k) + solve(k, right)
        }.also { cache[left][right] = it }
    }

    return solve(0, m - 1)
}
```

```java
import java.util.Arrays;

public class MinimumCostToCutAStick {
    private int[] points;
    private int[][] memo;

    /**
     * @param n    the length of the stick
     * @param cuts the positions where cuts must be made (any order)
     * @return     the minimum total cost to make all cuts
     */
    public int minCost(int n, int[] cuts) {
        int[] sorted = cuts.clone();
        Arrays.sort(sorted);
        points = new int[sorted.length + 2];
        points[0] = 0;
        System.arraycopy(sorted, 0, points, 1, sorted.length);
        points[points.length - 1] = n;

        memo = new int[points.length][points.length];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solve(0, points.length - 1);
    }

    /**
     * @param left  index of the left boundary cut-point
     * @param right index of the right boundary cut-point
     * @return      minimum cost to fully cut the segment (points[left], points[right])
     */
    private int solve(int left, int right) {
        if (memo[left][right] != -1) return memo[left][right];
        if (left + 1 >= right) return 0;
        int best = Integer.MAX_VALUE;
        for (int k = left + 1; k < right; k++) {
            best = Math.min(best,
                (points[right] - points[left]) + solve(left, k) + solve(k, right));
        }
        return memo[left][right] = best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <climits>

class MinimumCostToCutAStick {
    std::vector<int> points;
    std::vector<std::vector<int>> memo;

public:
    /**
     * @param n    the length of the stick
     * @param cuts the positions where cuts must be made (any order)
     * @return     the minimum total cost to make all cuts
     */
    int minCost(int n, std::vector<int> cuts) {
        std::sort(cuts.begin(), cuts.end());
        points = {0};
        points.insert(points.end(), cuts.begin(), cuts.end());
        points.push_back(n);

        int m = (int)points.size();
        memo.assign(m, std::vector<int>(m, -1));
        return solve(0, m - 1);
    }

private:
    /**
     * @param left  index of the left boundary cut-point
     * @param right index of the right boundary cut-point
     * @return      minimum cost to fully cut the segment (points[left], points[right])
     */
    int solve(int left, int right) {
        if (memo[left][right] != -1) return memo[left][right];
        if (left + 1 >= right) return 0;
        int best = INT_MAX;
        for (int k = left + 1; k < right; k++) {
            best = std::min(best,
                (points[right] - points[left]) + solve(left, k) + solve(k, right));
        }
        return memo[left][right] = best;
    }
};
```

```python
def min_cost(n: int, cuts: list[int]) -> int:
    """
    @param n:    the length of the stick
    @param cuts: the positions where cuts must be made (any order)
    @return:     the minimum total cost to make all cuts
    """
    points = [0] + sorted(cuts) + [n]
    m = len(points)
    from functools import lru_cache

    @lru_cache(None)
    def solve(left: int, right: int) -> int:
        """
        @param left:  index of the left boundary cut-point
        @param right: index of the right boundary cut-point
        @return:      minimum cost to fully cut the segment (points[left], points[right])
        """
        if left + 1 >= right:
            return 0                                   # no cuts inside: free
        return min(
            (points[right] - points[left]) + solve(left, k) + solve(k, right)
            for k in range(left + 1, right)
        )

    return solve(0, m - 1)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param n    the length of the stick
    /// @param cuts the positions where cuts must be made (any order)
    /// @return     the minimum total cost to make all cuts
    pub fn min_cost(n: i32, cuts: Vec<i32>) -> i32 {
        let mut cuts = cuts;
        cuts.sort_unstable();
        let mut points = vec![0];
        points.extend(cuts);
        points.push(n);

        let mut memo: HashMap<(usize, usize), i32> = HashMap::new();
        fn solve(points: &[i32], memo: &mut HashMap<(usize, usize), i32>, left: usize, right: usize) -> i32 {
            if left + 1 >= right {
                return 0;
            }
            if let Some(&v) = memo.get(&(left, right)) {
                return v;
            }
            let mut best = i32::MAX;
            for k in (left + 1)..right {
                best = best.min(
                    (points[right] - points[left]) + solve(points, memo, left, k) + solve(points, memo, k, right),
                );
            }
            memo.insert((left, right), best);
            best
        }
        solve(&points, &mut memo, 0, points.len() - 1)
    }
}
```

## Dry run

**Input:** `n = 7`, `cuts = [1, 3, 4, 5]` → `points = [0, 1, 3, 4, 5, 7]`.

Compute bottom-up-ish by increasing interval width (trace the key cells):

```
Width 1 (adjacent): solve(0,1)=solve(1,2)=...=0      (no cuts inside)
Width 2:
  solve(0,2): segment [0,3], cut at 1: cost (3-0) + 0 + 0 = 3
  solve(1,3): segment [1,4], cut at 3: cost (4-1) + 0 + 0 = 3
  solve(2,4): segment [3,5], cut at 4: cost (5-3) + 0 + 0 = 2
  solve(3,5): segment [4,7], cut at 5: cost (7-4) + 0 + 0 = 3
Width 3:
  solve(0,3): segment [0,4], cuts at {1,3}:
      last cut at 1 -> 4 + solve(0,1)=0 + solve(1,3)=3  = 7
      last cut at 3 -> 4 + solve(0,2)=3 + solve(2,3)=0  = 7   => 7
  solve(1,4): segment [1,5], cuts at {3,4}:
      last at 3 -> 4 + 0 + solve(3,4)=0? wait solve(1,3)=3 ... recompute:
      last at 3 -> (5-1)=4 + solve(1,3)=3 + solve(3,4)=0 = 7
      last at 4 -> 4 + solve(1,4)... hmm (1..4) excludes 4? k in (1,4) = {2,3}
      k=2 (cut 3): 4 + solve(1,2)=0 + solve(2,4)=2 = 6
      k=3 (cut 4): 4 + solve(1,3)=3 + solve(3,4)=0 = 7   => 6
  solve(2,5): segment [3,7], cuts at {4,5}:
      k=3 (cut 4): (7-3)=4 + solve(2,3)=0 + solve(3,5)=3 = 7
      k=4 (cut 5): 4 + solve(2,4)=2 + solve(4,5)=0 = 6      => 6
Width 4:
  solve(0,4): segment [0,5], cuts at {1,3,4}:
      k=1: 5 + solve(0,1)=0 + solve(1,4)=6  = 11
      k=2: 5 + solve(0,2)=3 + solve(2,4)=2  = 10
      k=3: 5 + solve(0,3)=7 + solve(3,4)=0  = 12           => 10
  solve(1,5): segment [1,7], cuts at {3,4,5}:
      k=2: 6 + solve(1,2)=0 + solve(2,5)=6  = 12
      k=3: 6 + solve(1,3)=3 + solve(3,5)=3  = 12
      k=4: 6 + solve(1,4)=6 + solve(4,5)=0  = 12           => 12
Width 5 (the answer):
  solve(0,5): segment [0,7], cuts at {1,3,4,5}:
      k=1: 7 + solve(0,1)=0 + solve(1,5)=12 = 19
      k=2: 7 + solve(0,2)=3 + solve(2,5)=6  = 16   ← best
      k=3: 7 + solve(0,3)=7 + solve(3,5)=3  = 17
      k=4: 7 + solve(0,4)=10 + solve(4,5)=0 = 17
Answer: 16 ✓   (last cut at 3: halves [0,3] cost 3 and [3,7] cost 6, plus the final 7)
```

The winning strategy reads off the trace: solve `[0,3]` and `[3,7]` fully (costs 3 and 6), then cut at 3 (cost 7) — total 16, matching the problem statement's optimal order.

## Complexity

**Time.** The state space is $O(m^2)$ intervals, each scanning $O(m)$ possible last cuts:

$$
T(n) = O(m^3), \qquad m = \#cuts
$$

With `m ≤ 100`: $\approx 10^6$ operations — trivial. (The length `n` never appears in the DP itself, only through `points`.)

**Space.** $O(m^2)$ memo table.

## Variants & follow-ups

- **[2.11](minimum-cost-to-merge-stones.md)** — the same interval shape with a different "last operation" story.
- **Burst Balloons** (`src/main/kotlin/array/dp/BurstBaloons.kt`) — the same "think about the last one" trick: fix the *last* balloon popped, then the subintervals are independent.
- **Interview follow-up:** "Why is the cost `points[right] - points[left]` and not `points[k+1] - points[k-1]`?" Because the *last* cut inside `[left, right]` happens when both halves are still attached — the segment being cut is the entire `[left, right]`. If we reasoned about the *first* cut, the future segment lengths would be unknown; backward reasoning makes them known.
- **Interview follow-up:** "Bottom-up version?" Fill by increasing interval width: `for (len in 2..m) for (left in 0..m-len) { right = left+len; ... }` — every cell reads strictly smaller intervals, so the order is safe.
