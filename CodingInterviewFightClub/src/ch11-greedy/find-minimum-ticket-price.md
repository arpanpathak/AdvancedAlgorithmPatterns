# 11.20 Find Minimum Ticket Price

> **Source**: [`src/main/kotlin/facebook/FindMinimumTicketPrice.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/facebook/FindMinimumTicketPrice.kt)
> **Pattern**: backward suffix-min sweep · **Core page**

## The Problem

Depart on day `i` (cost `departure[i]`), return on a **later or same** day at `returnPrices[j]`. Min total cost.

- Constraints: arrays equal length; costs ≥ 0.

## Examples

```
Input:  departure = [1,3,2], returnPrices = [4,1,2]
Output: 2   (depart day 2 (cost 2) + return same day 2 = 2+... hmm: min over i of departure[i] + min(returnPrices[i..]) )
```

## Intuition — for each departure, the best return is the suffix minimum

Backward sweep keeps `minReturnPrice` = cheapest return from day i onward; each departure's total is `departure[i] + minReturnPrice`:

```kotlin
var minReturnPrice = Int.MAX_VALUE
var minCost = Int.MAX_VALUE

for (i in n - 1 downTo 0) {
    minReturnPrice = minOf(minReturnPrice, returnPrices[i])     // suffix min
    minCost = minOf(minCost, departure[i] + minReturnPrice)     // this departure + best return
}
return minCost
```

**Why backward?** The return must be on day ≥ departure — the suffix min (not the global min) is the eligible set. Walking backward builds the suffix incrementally; each i sees exactly its valid returns.

**Why is this greedy/DP trivial?** The choice decomposes: departure day i is independent of the return pick (suffix min) — no state beyond the running min. The [11.16](best-time-to-buy-and-sell-stock.md) running-extreme sweep in reverse.

## Approach 1 — For each i, scan the suffix (O(n²))

Find the min return per departure: correct, slow.

## Approach 2 — Backward suffix-min sweep (the repo's version, optimal)

```kotlin
class FindMinimumTicketPrice {
    /**
     * @param departure    departure costs by day
     * @param returnPrices return costs by day
     * @return             minimum total trip cost
     */
    fun findMinimumTicketCost(departure: IntArray, returnPrices: IntArray): Int {
        val n = returnPrices.size
        var minReturnPrice = Int.MAX_VALUE
        var minCost = Int.MAX_VALUE

        for (i in n - 1 downTo 0) {
            minReturnPrice = minOf(minReturnPrice, returnPrices[i])
            minCost = minOf(minCost, departure[i] + minReturnPrice)
        }
        return minCost
    }
}
```

```java
public class FindMinimumTicketPrice {
    /**
     * @param departure    departure costs by day
     * @param returnPrices return costs by day
     * @return             minimum total trip cost
     */
    public int findMinimumTicketCost(int[] departure, int[] returnPrices) {
        int minReturn = Integer.MAX_VALUE, minCost = Integer.MAX_VALUE;

        for (int i = returnPrices.length - 1; i >= 0; i--) {
            minReturn = Math.min(minReturn, returnPrices[i]);
            minCost = Math.min(minCost, departure[i] + minReturn);
        }
        return minCost;
    }
}
```

```cpp
#include <vector>
#include <algorithm>
#include <climits>

class FindMinimumTicketPrice {
public:
    /**
     * @param departure    departure costs by day
     * @param returnPrices return costs by day
     * @return             minimum total trip cost
     */
    int findMinimumTicketCost(std::vector<int>& departure, std::vector<int>& returnPrices) {
        int minReturn = INT_MAX, minCost = INT_MAX;

        for (int i = returnPrices.size() - 1; i >= 0; i--) {
            minReturn = std::min(minReturn, returnPrices[i]);
            minCost = std::min(minCost, departure[i] + minReturn);
        }
        return minCost;
    }
};
```

```python
def find_minimum_ticket_cost(departure: list[int], return_prices: list[int]) -> int:
    """
    @param departure:     departure costs by day
    @param return_prices: return costs by day
    @return:              minimum total trip cost
    """
    min_return = float("inf")
    min_cost = float("inf")

    for i in range(len(return_prices) - 1, -1, -1):
        min_return = min(min_return, return_prices[i])
        min_cost = min(min_cost, departure[i] + min_return)

    return min_cost
```

```rust
impl Solution {
    /// @param departure    departure costs by day
    /// @param return_prices return costs by day
    /// @return             minimum total trip cost
    pub fn find_minimum_ticket_cost(departure: Vec<i32>, return_prices: Vec<i32>) -> i32 {
        let mut min_return = i32::MAX;
        let mut min_cost = i32::MAX;

        for i in (0..return_prices.len()).rev() {
            min_return = min_return.min(return_prices[i]);
            min_cost = min_cost.min(departure[i] + min_return);
        }
        min_cost
    }
}
```

## Dry run

**Input:** `departure = [1,3,2], returnPrices = [4,1,2]`.

```
i=2: minReturn = 2.  minCost = 2 + 2 = 4.
i=1: minReturn = min(2,1) = 1.  minCost = min(4, 3+1=4) = 4.
i=0: minReturn = min(1,4) = 1.  minCost = min(4, 1+1=2) = 2.

Output: 2 ✓  (depart day 0 cost 1, return day 1 cost 1)
```

The backward sweep's magic: at i=0, `minReturn = 1` is the cheapest return *from day 0 onward* — the global minimum happens to be eligible. Had the cheap return been only *before* the departure, the suffix would correctly exclude it.

## Complexity

**Time.** One backward pass:

$$
T(n) = O(n)
$$

**Space.** Two scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Best Time To Buy And Sell Stock** ([11.16](best-time-to-buy-and-sell-stock.md)) — the same running-extreme with buy-before-sell.
- **Interview follow-up:** "Why does the suffix min work but the global min not?" The return day must be ≥ the departure day — a cheaper return *earlier* in time is ineligible. The backward sweep's `minReturn` is *exactly* the eligible set at each i: the minimum over `returnPrices[i..]`.
