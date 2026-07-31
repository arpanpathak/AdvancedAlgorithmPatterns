# 2.5 Unbounded Knapsack

> **Source:** [`src/main/kotlin/dynamic_programming/UnboundedKnapsack.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/dynamic_programming/UnboundedKnapsack.kt)
> **Pattern:** capacity-state DP (forward pass) · **Variant page — the one-line difference**

## The Problem

Same knapsack as [2.4](zero-one-knapsack.md), except each item can be taken **any number of times**. Maximize total value with total weight ≤ `capacity`.

## Examples

```
items = [(w=5, v=10), (w=10, v=30), (w=15, v=20)], capacity = 100
Output: 300
Explanation: take item 2 (w=10, v=30) ten times: value 300, weight exactly 100.

items = [(w=1, v=1), (w=50, v=30)], capacity = 100
Output: 100
Explanation: 100 × item 1 = 100 value beats 2 × item 2 = 60.
```

## Intuition — forwards instead of backwards

Compare the two recurrences side by side:

$$
\text{0/1: } dp[c] = \max(dp[c],\; dp[c - w] + v) \quad \text{with } c \text{ descending}
$$

$$
\text{unbounded: } dp[c] = \max(dp[c],\; dp[c - w] + v) \quad \text{with } c \text{ ascending}
$$

Same formula — **only the loop direction changes**. Here's the reasoning in one sentence:

- **Descending** means `dp[c - w]` was computed *before* the current item touched it → it reflects "previous items only" → the current item can appear **at most once** in any combination (0/1).
- **Ascending** means `dp[c - w]` may already include the *current* item (since `c - w < c` was visited earlier in this same loop) → the current item can appear **multiple times** (unbounded).

In the unbounded loop, when we compute `dp[15]` for an item of weight 5, the `dp[10]` it reads may itself have been built from two copies of this item — so `dp[15]` can represent three copies. The forward pass literally lets items "stack on themselves," which is exactly the unbounded semantics.

**Why this is the right formulation (not "loop items inside capacities"):** a naive unbounded DP might try "for each item, for each count k, ..." — that's $O(nW \cdot W/k)$ and wrong-headed. The forward capacity loop is the *same* $O(nW)$ as 0/1. The only cost is mental: you must remember which direction means which.

## Approach — forward-pass capacity DP (optimal)

```kotlin
/**
 * @param items    each item has a weight and a value; items can be reused any number of times
 * @param capacity the total weight the knapsack can carry
 * @return         the maximum total value achievable with total weight <= capacity
 */
fun unboundedKnapsack(items: List<Item>, capacity: Int): Int {
    val dp = IntArray(capacity + 1)

    items.forEach { (w, v) ->
        // ASCENDING: dp[c - w] was already updated by the CURRENT item earlier in this loop,
        // so the item can "stack" on itself -> unlimited copies allowed.
        for (c in w..capacity) {
            dp[c] = maxOf(dp[c], dp[c - w] + v)
        }
    }
    return dp[capacity]
}
```

```java
public class UnboundedKnapsack {
    /**
     * @param weights  the weight of each item
     * @param values   the value of each item
     * @param capacity the total weight the knapsack can carry
     * @return         the maximum total value achievable with total weight <= capacity
     */
    public int unboundedKnapsack(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int c = weights[i]; c <= capacity; c++) {   // ascending = unbounded semantics
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

class UnboundedKnapsack {
public:
    /**
     * @param weights  the weight of each item
     * @param values   the value of each item
     * @param capacity the total weight the knapsack can carry
     * @return         the maximum total value achievable with total weight <= capacity
     */
    int unboundedKnapsack(const std::vector<int>& weights, const std::vector<int>& values, int capacity) {
        std::vector<int> dp(capacity + 1, 0);
        for (int i = 0; i < (int)weights.size(); i++) {
            for (int c = weights[i]; c <= capacity; c++) {   // ascending = unbounded semantics
                dp[c] = std::max(dp[c], dp[c - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }
};
```

```python
def unbounded_knapsack(weights: list[int], values: list[int], capacity: int) -> int:
    """
    @param weights:  the weight of each item
    @param values:   the value of each item
    @param capacity: the total weight the knapsack can carry
    @return:         the maximum total value achievable with total weight <= capacity
    """
    dp = [0] * (capacity + 1)
    for w, v in zip(weights, values):
        for c in range(w, capacity + 1):       # ascending = unbounded semantics
            dp[c] = max(dp[c], dp[c - w] + v)
    return dp[capacity]
```

```rust
impl Solution {
    /// @param weights  the weight of each item
    /// @param values   the value of each item
    /// @param capacity the total weight the knapsack can carry
    /// @return         the maximum total value achievable with total weight <= capacity
    pub fn unbounded_knapsack(weights: Vec<i32>, values: Vec<i32>, capacity: i32) -> i32 {
        let cap = capacity as usize;
        let mut dp = vec![0i32; cap + 1];
        for (w, v) in weights.iter().zip(values.iter()) {
            let mut c = *w as usize;
            while c <= cap {                     // ascending = unbounded semantics
                dp[c] = dp[c].max(dp[c - *w as usize] + v);
                c += 1;
            }
        }
        dp[cap]
    }
}
```

## Dry run — watch an item stack on itself

**Input:** `weights = [5, 10, 15]`, `values = [10, 30, 20]`, `capacity = 30`.

Trace just item 2 (`w=10, v=30`) in ascending order:

```
dp before item2: capacity 0..30  ->  all from item1 (w=5,v=10): dp = [0,0,0,0,0,10,10,10,10,10,20,20,...]
c=10: dp[10] = max(20, dp[0]+30 = 30)                 = 30
c=15: dp[15] = max(10, dp[5]+30 = 10+30 = 40)         = 40
c=20: dp[20] = max(20, dp[10]+30 = 30+30 = 60)        = 60   <- TWO copies of item2!
c=25: dp[25] = max(10, dp[15]+30 = 40+30 = 70)        = 70   <- item2 + item2-ish mix
c=30: dp[30] = max(20, dp[20]+30 = 60+30 = 90)        = 90   <- THREE copies: 30+30+30
```

The `dp[20]` read at `c=30` already contains two copies of item 2 (it was computed earlier in *this same loop* at `c=20`). That's the stacking. In the 0/1 descending version, `dp[20]` would still be the item-1 value, and `dp[30]` would cap at `max(20, 30+20)=50`-ish — no stacking.

Full table after all items: `dp[30] = 90` (three copies of item 2). With `capacity = 100`: `dp[100] = 300` (ten copies), matching the example.

## Complexity

**Time.** Same shape as 0/1:

$$
T(n, W) = O(nW)
$$

**Space.** $O(W)$.

## Variants & follow-ups

- **[2.4](zero-one-knapsack.md)** — the twin; the loop-direction contrast is the single most asked "do you actually understand it?" question in knapsack interviews.
- **Coin Change** (`src/main/kotlin/array/dp/CoinChange.kt`) — unbounded knapsack with value = 1 per coin and "minimize coins to hit exact sum" instead of maximize.
- **Coin Change II** (`src/main/kotlin/array/dp/CoinChange_II.kt`) — the *counting* version: `dp[c] += dp[c - coin]`, same forward loop.
- **Interview follow-up:** "What if we also cap copies of item i at K_i?" That's *bounded* knapsack — binary-split the copies into $\log K_i$ pseudo-items, each with 0/1 semantics, giving $O(nW \log K)$. A great "where would you even start" question.
