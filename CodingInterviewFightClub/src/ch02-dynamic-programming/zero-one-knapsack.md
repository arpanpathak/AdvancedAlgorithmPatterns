# 2.4 0/1 Knapsack

> **Source:** [`src/main/kotlin/dynamic_programming/01Knapsack.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/dynamic_programming/01Knapsack.kt)
> **Pattern:** capacity-state DP · **Core page — the most reusable DP in this book**

## The Problem

Given `n` items, each with a `weight` and a `value`, and a knapsack with `capacity`, pick a subset of items whose total weight ≤ `capacity` maximizing total value. "0/1" means each item is **taken whole or not at all** — no fractions, no repeats.

- Constraints: $n$ small (≤ 100 in interviews), `capacity` up to ~$10^4$ (the DP table must fit in memory).

## Examples

```
items = [(w=10, v=60), (w=20, v=100), (w=30, v=120)], capacity = 50
Output: 220
Explanation: take items 2 and 3: weight 20+30=50, value 100+120=220.
             Taking item 1+3 gives 60+120=180 (weight 40, worse); 1+2 gives 160. 220 wins.
```

## Intuition — the "take or skip" fork in the road

Walk through the items one at a time. When you meet item `i`, you face exactly two futures — take it or skip it:

$$
dp[i][c] = \max\!\big(\underbrace{dp[i-1][c]}_{\text{skip } i}, \underbrace{dp[i-1][c - w_i] + v_i}_{\text{take } i \text{ (if it fits)}}\big)
$$

State: $dp[i][c]$ = max value using the **first `i` items** with **capacity `c`**.

- **Skip:** the best value with the previous items and the same capacity.
- **Take:** pay `w_i` of capacity, pocket `v_i`, then solve the subproblem with the previous items and the *remaining* capacity `c - w_i`.

**Why optimal substructure holds:** any optimal selection either excludes item `i` (then it's an optimal selection of the first `i-1` items with capacity `c`) or includes it (then the rest is an optimal selection of the first `i-1` items with capacity `c - w_i`). There's no third case. This *exhaustive fork* is the entire algorithm.

**The 1D trick (the interview gem):** the recurrence only reads row `i-1`. If we keep a single array and iterate capacity **descending**, `dp[c-w]` still holds the *previous item's* value when we read it — because descending order means `c-w < c` was **not yet overwritten** this round. One array, exact same semantics.

## Approach 1 — Brute force

Enumerate all $2^n$ subsets. For $n = 100$: impossible. The "capacity" dimension of the state is what buys polynomial time — at the price of the table.

## Approach 2 — Bottom-up 2D (the reference implementation)

```kotlin
/**
 * @param items    each item has a weight and a value
 * @param capacity the total weight the knapsack can carry
 * @return         the maximum total value achievable with total weight <= capacity
 */
fun knapsack2D(items: List<Item>, capacity: Int): Int {
    val n = items.size
    // dp[i][c] = max value using the first i items with capacity c
    val dp = Array(n + 1) { IntArray(capacity + 1) }

    items.forEachIndexed { idx, (w, v) ->
        val i = idx + 1                // 1-indexed row
        for (c in 0..capacity) {
            dp[i][c] = if (w <= c) {
                maxOf(
                    dp[i - 1][c],          // skip item i
                    dp[i - 1][c - w] + v   // take item i (use remaining capacity c - w)
                )
            } else {
                dp[i - 1][c]               // too heavy: must skip
            }
        }
    }
    return dp[n][capacity]
}
```

## Approach 3 — 1D rolling array (space-optimal)

```kotlin
/**
 * @param items    each item has a weight and a value
 * @param capacity the total weight the knapsack can carry
 * @return         the maximum total value achievable with total weight <= capacity
 */
fun knapsack(items: List<Item>, capacity: Int): Int {
    val dp = IntArray(capacity + 1)

    items.forEach { (w, v) ->
        // DESCENDING: dp[c - w] is still the PREVIOUS item's row (not yet overwritten),
        // which enforces the 0/1 rule (each item used at most once).
        for (c in capacity downTo w) {
            dp[c] = maxOf(dp[c], dp[c - w] + v)
        }
    }
    return dp[capacity]
}
```

```java
public class ZeroOneKnapsack {
    /**
     * @param weights  the weight of each item
     * @param values   the value of each item
     * @param capacity the total weight the knapsack can carry
     * @return         the maximum total value achievable with total weight <= capacity
     */
    public int knapsack(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int c = capacity; c >= weights[i]; c--) {   // descending = 0/1 semantics
                dp[c] = Math.max(dp[c], dp[c - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class ZeroOneKnapsack {
public:
    /**
     * @param weights  the weight of each item
     * @param values   the value of each item
     * @param capacity the total weight the knapsack can carry
     * @return         the maximum total value achievable with total weight <= capacity
     */
    int knapsack(const std::vector<int>& weights, const std::vector<int>& values, int capacity) {
        std::vector<int> dp(capacity + 1, 0);
        for (int i = 0; i < (int)weights.size(); i++) {
            for (int c = capacity; c >= weights[i]; c--) {   // descending = 0/1 semantics
                dp[c] = std::max(dp[c], dp[c - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }
};
```

```python
def knapsack(weights: list[int], values: list[int], capacity: int) -> int:
    """
    @param weights:  the weight of each item
    @param values:   the value of each item
    @param capacity: the total weight the knapsack can carry
    @return:         the maximum total value achievable with total weight <= capacity
    """
    dp = [0] * (capacity + 1)
    for w, v in zip(weights, values):
        for c in range(capacity, w - 1, -1):     # descending = 0/1 semantics
            dp[c] = max(dp[c], dp[c - w] + v)
    return dp[capacity]
```

```rust
impl Solution {
    /// @param weights  the weight of each item
    /// @param values   the value of each item
    /// @param capacity the total weight the knapsack can carry
    /// @return         the maximum total value achievable with total weight <= capacity
    pub fn knapsack(weights: Vec<i32>, values: Vec<i32>, capacity: i32) -> i32 {
        let cap = capacity as usize;
        let mut dp = vec![0i32; cap + 1];
        for (w, v) in weights.iter().zip(values.iter()) {
            let mut c = cap;
            while c >= *w as usize {
                dp[c] = dp[c].max(dp[c - *w as usize] + v);   // descending = 0/1 semantics
                c -= 1;
            }
        }
        dp[cap]
    }
}
```

## Dry run

**Input:** `weights = [10, 20, 30]`, `values = [60, 100, 120]`, `capacity = 50`.

1D trace (row after each item; `-` means unreachable-or-unused capacity slot):

```
capacity:    0  10  20  30  40  50
initial:     0   0   0   0   0   0
after item1 (w10,v60):   0  60  60  60  60  60
after item2 (w20,v100):  0  60 100 160 160 160
after item3 (w30,v120):  0  60 100 160 180 220
                                            ^ answer = 220
```

Trace the interesting updates (item 3, `c = 50`):

```
c=50: dp[50] = max(dp[50]=160, dp[50-30]+120 = dp[20]+120 = 100+120 = 220) -> 220 ✓
c=40: dp[40] = max(dp[40]=160, dp[40-30]+120 = dp[10]+120 = 60+120 = 180) -> 180
c=30: dp[30] = max(dp[30]=160, dp[0]+120 = 120)                            -> 160 (skip wins)
```

The descending order is what keeps `dp[20]` at **100** (item-2 row) when item 3 reads it — if we'd gone ascending, item 3 would have overwritten `dp[20]` earlier and `dp[50]` would wrongly become `max(160, dp[20]+120)` with a *contaminated* `dp[20]`.

## Complexity

**Time.** One pass per item over the capacity axis:

$$
T(n, W) = O(nW)
$$

**Space.** 2D: $\Theta(nW)$. 1D rolling: $O(W)$.

## Variants & follow-ups

- **[2.5](unbounded-knapsack.md)** — the one-line change (ascending loop) that turns "each item once" into "each item unlimited times". Know **why** the loop direction flips the semantics.
- **[2.6](partition-equal-subset-sum.md)** — 0/1 knapsack with values == weights, asking "can we hit exactly sum/2?" (boolean DP).
- **Target Sum / Coin Change II** (`src/main/kotlin/array/dp/`) — counting versions of the same capacity state.
- **Interview follow-up:** "Can you reconstruct which items were taken?" Keep a *choice* table `take[i][c]` (or backtrack through the 2D table): at `dp[i][c]`, if `dp[i][c] != dp[i-1][c]`, item `i` was taken. $O(n)$ reconstruction after the table.
- **Interview follow-up:** "What if capacity is huge (10^9)?" $O(nW)$ dies; you'd switch to meet-in-the-middle over items ($O(2^{n/2})$) — exactly the tool from [1.22](../ch01-binary-search/closest-subsequence-sum.md). Naming the failure mode of DP = senior signal.
