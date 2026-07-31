# 1.2 Capacity To Ship Packages Within D Days

> **Source:** [`src/main/kotlin/binarysearch/CapacityToShipPackageWithinDDays.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/CapacityToShipPackageWithinDDays.kt)
> **Pattern:** binary search on the answer · **Core page**

## The Problem

A conveyor belt ships packages **in order**. You are given `weights` — the weight of each package — and `days` — the number of days you have. Packages are loaded onto the ship in the given order, one day at a time, without reordering.

The ship has a **capacity** `C` (total weight it can carry per day). Each day you must ship the *next* packages until adding one more would exceed `C`.

Find the **minimum capacity** `C` that ships all packages within `days` days.

- Constraints: $1 \le n \le 5 \times 10^4$, $1 \le days \le n$, $1 \le weights[i] \le 500$.

## Examples

```
Example 1
Input:  weights = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10], days = 5
Output: 15
Explanation: with C = 15:
  day 1: 1+2+3+4+5 = 15
  day 2: 6+7      = 13
  day 3: 8        = 8
  day 4: 9        = 9
  day 5: 10       = 10   -> exactly 5 days. C = 14 fails (1+2+3+4+5=15 > 14 splits into more days).

Example 2
Input:  weights = [3, 2, 2, 4, 1, 4], days = 3
Output: 6
Explanation: day 1: 3+2 = 5, day 2: 2+4 = 6, day 3: 1+4 = 5 → 3 days with capacity 6.
```

## Intuition — the Koko twin

This is Koko ([1.1](koko-eating-bananas.md)) with different clothes:

- The **answer range** is $[\max(weights), \sum(weights)]$. Capacity below the heaviest single package can *never* ship that package; capacity at the total sum ships everything in one day.
- **Feasibility is monotone:** $P(C)$ = "all packages ship within `days` days at capacity $C$". Bigger capacity can only mean *fewer* days — you can always ship the same packages you could before, and possibly more per day. So $P$ is a false-prefix/true-suffix predicate:

```
P(C):   F F F F F T T T T T
              ^
        answer = first true
```

- **The feasibility check is a greedy day-packer.** To test capacity $C$: walk the packages, accumulate into a running load, and whenever adding the next package would exceed $C$, close the current day and start a new one with that package. Count days. This is *optimal* for the "ship in order" constraint — no smarter packing exists because reordering is forbidden and each package is atomic.

Total: $O(n \log S)$ where $S = \sum weights \le 2.5 \times 10^7$. The log factor is only ~25; the $n$ factor is 1 pass per check.

## Approach 1 — Brute force (try every capacity)

Scan $C$ from $\max(weights)$ upward, run the greedy day-counter, return the first $C$ that fits. Cost: up to $S - \max$ candidates × $O(n)$ per check $\approx 2.5 \times 10^7 \times 5 \times 10^4$ — catastrophically slow. The monotone structure begs for halving instead.

## Approach 2 — Binary search on the answer (optimal)

```kotlin
/**
 * @param weights the weight of each package, in shipping order
 * @param days    the number of days available to ship everything
 * @return        the minimum capacity C such that all packages ship within `days` days
 */
fun shipWithinDays(weights: IntArray, days: Int): Int {
    var sum = 0
    var max = 0
    for (weight in weights) {
        sum += weight
        max = maxOf(max, weight)
    }

    var left = max          // infeasible below this
    var right = sum         // always feasible
    while (left < right) {
        val mid = left + (right - left) / 2
        if (feasible(weights, mid, days)) {
            right = mid     // feasible -> try smaller capacity
        } else {
            left = mid + 1  // infeasible -> need more capacity
        }
    }
    return left
}

/**
 * @param weights the packages in order
 * @param capacity the candidate ship capacity to test
 * @param days    the day budget
 * @return        true iff a greedy day-packer fits all packages into <= `days` days
 */
fun feasible(weights: IntArray, capacity: Int, days: Int): Boolean {
    var daysNeeded = 1
    var currentLoad = 0
    for (weight in weights) {
        currentLoad += weight
        if (currentLoad > capacity) {
            daysNeeded++            // close the current day, start a fresh one with this package
            currentLoad = weight
        }
    }
    return daysNeeded <= days
}
```

```java
public class CapacityToShipPackageWithinDDays {
    /**
     * @param weights the weight of each package, in shipping order
     * @param days    the number of days available to ship everything
     * @return        the minimum capacity C such that all packages ship within `days` days
     */
    public int shipWithinDays(int[] weights, int days) {
        int sum = 0, max = 0;
        for (int w : weights) {
            sum += w;
            max = Math.max(max, w);
        }

        int left = max, right = sum;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (feasible(weights, mid, days)) right = mid;   // feasible -> smaller capacity
            else left = mid + 1;                              // infeasible -> more capacity
        }
        return left;
    }

    /**
     * @param weights  the packages in order
     * @param capacity the candidate ship capacity to test
     * @param days     the day budget
     * @return         true iff a greedy day-packer fits all packages into <= `days` days
     */
    private boolean feasible(int[] weights, int capacity, int days) {
        int daysNeeded = 1, currentLoad = 0;
        for (int w : weights) {
            currentLoad += w;
            if (currentLoad > capacity) {
                daysNeeded++;
                currentLoad = w;
            }
        }
        return daysNeeded <= days;
    }
}
```

```cpp
#include <vector>

class CapacityToShipPackageWithinDDays {
public:
    /**
     * @param weights the weight of each package, in shipping order
     * @param days    the number of days available to ship everything
     * @return        the minimum capacity C such that all packages ship within `days` days
     */
    int shipWithinDays(const std::vector<int>& weights, int days) {
        int sum = 0, max = 0;
        for (int w : weights) {
            sum += w;
            max = std::max(max, w);
        }

        int left = max, right = sum;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (feasible(weights, mid, days)) right = mid;
            else left = mid + 1;
        }
        return left;
    }

private:
    /**
     * @param weights  the packages in order
     * @param capacity the candidate ship capacity to test
     * @param days     the day budget
     * @return         true iff a greedy day-packer fits all packages into <= `days` days
     */
    bool feasible(const std::vector<int>& weights, int capacity, int days) {
        int daysNeeded = 1, currentLoad = 0;
        for (int w : weights) {
            currentLoad += w;
            if (currentLoad > capacity) {
                daysNeeded++;
                currentLoad = w;
            }
        }
        return daysNeeded <= days;
    }
};
```

```python
def ship_within_days(weights: list[int], days: int) -> int:
    """
    @param weights: the weight of each package, in shipping order
    @param days:    the number of days available to ship everything
    @return:        the minimum capacity C such that all packages ship within `days` days
    """
    left, right = max(weights), sum(weights)
    while left < right:
        mid = left + (right - left) // 2
        if feasible(weights, mid, days):
            right = mid        # feasible -> try smaller capacity
        else:
            left = mid + 1     # infeasible -> need more capacity
    return left


def feasible(weights: list[int], capacity: int, days: int) -> bool:
    """
    @param weights:  the packages in order
    @param capacity: the candidate ship capacity to test
    @param days:     the day budget
    @return:         True iff a greedy day-packer fits all packages into <= `days` days
    """
    days_needed, current_load = 1, 0
    for w in weights:
        current_load += w
        if current_load > capacity:
            days_needed += 1
            current_load = w
    return days_needed <= days
```

```rust
impl Solution {
    /// @param weights the weight of each package, in shipping order
    /// @param days    the number of days available to ship everything
    /// @return        the minimum capacity C such that all packages ship within `days` days
    pub fn ship_within_days(weights: Vec<i32>, days: i32) -> i32 {
        let (mut left, mut right) = (*weights.iter().max().unwrap(), weights.iter().sum::<i32>());
        while left < right {
            let mid = left + (right - left) / 2;
            if Self::feasible(&weights, mid, days) {
                right = mid;          // feasible -> try smaller capacity
            } else {
                left = mid + 1;       // infeasible -> need more capacity
            }
        }
        left
    }

    /// @param weights  the packages in order
    /// @param capacity the candidate ship capacity to test
    /// @param days     the day budget
    /// @return         true iff a greedy day-packer fits all packages into <= `days` days
    fn feasible(weights: &[i32], capacity: i32, days: i32) -> bool {
        let mut days_needed = 1;
        let mut current_load = 0;
        for &w in weights {
            current_load += w;
            if current_load > capacity {
                days_needed += 1;
                current_load = w;
            }
        }
        days_needed <= days
    }
}
```

## Dry run

**Input:** `weights = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]`, `days = 5`. Search space $[10, 55]$:

```
left=10  right=55  mid=32  -> greedy days(32) = 3  ≤ 5  FEASIBLE -> right=32
left=10  right=32  mid=21  -> greedy days(21) = 3  ≤ 5  FEASIBLE -> right=21
left=10  right=21  mid=15  -> greedy days(15) = 5  ≤ 5  FEASIBLE -> right=15
left=10  right=15  mid=12  -> greedy days(12) = 6  > 5  NOT      -> left=13
left=13  right=15  mid=14  -> greedy days(14) = 6  > 5  NOT      -> left=15
left=15  right=15  -> loop ends, return 15
```

Let me double-check the critical feasibility traces by hand (the "why 15 works, 14 doesn't" table):

| Capacity | Day 1 | Day 2 | Day 3 | Day 4 | Day 5 | Day 6 | ≤ 5 days? |
|---|---|---|---|---|---|---|---|
| 15 | 1+2+3+4+5=15 | 6+7=13 | 8 | 9 | 10 | — | ✓ |
| 14 | 1+2+3+4=10 | 5+6=11 | 7 | 8 | 9 | 10 | ✗ (6 days) |

For C=14: day1: 1,2,3,4 → 10; +5 = 15 > 14, so day2 starts with 5: 5,6 → 11; +7 = 18 > 14, day3 starts with 7: 7,8 → 15 > 14, day4 starts with 8: 8,9 → 17 > 14, day5 starts with 9: 9,10 → 19 > 14, day6: 10. That's **6 days** > 5 ✗. So 14 is infeasible and 15 is the boundary. The binary search found it in 5 feasibility checks.

## Complexity

**Time.** $O(\log(S - \max))$ halving steps, each costing an $O(n)$ greedy pass:

$$
T(n) = n \cdot \log_2(S - \max) = O(n \log S)
$$

With $S \le 2.5 \times 10^7$: $\approx 5 \times 10^4 \times 25 = 1.25 \times 10^6$ operations.

**Space.** $O(1)$ auxiliary (a handful of scalars; the input array is not copied).

## Why the greedy check is correct

The "ship in order, close a day when the next package overflows" rule is not a heuristic — it is forced. Because packages cannot be reordered and a day cannot be split mid-package, the *only* decision is where day boundaries fall, and the earliest possible boundary (greedy) leaves the maximum remaining capacity for future days. Formally: if a feasible packing exists with day boundaries $b_1 < b_2 < \dots$, then the greedy boundaries $g_1 \le b_1, g_2 \le b_2, \dots$ never finish later, so greedy uses $\le$ the optimal number of days. Hence "greedy days ≤ budget" is both necessary and sufficient for feasibility.

## Variants & follow-ups

- **Koko Eating Bananas** ([1.1](koko-eating-bananas.md)) — identical skeleton; the greedy check is "accumulate hours", not "accumulate load".
- **House Robber IV** ([1.10](house-robber-iv.md)) — same skeleton, greedy non-adjacent picker.
- **Split Array Largest Sum** (classic sibling) — the answer is the same "minimum feasible capacity"; the check counts subarrays with sum ≤ mid.
- **Interview follow-up:** "What if packages could be reordered?" Then feasibility is still monotone, but the check becomes a bin-packing argument (NP-hard in general) — the in-order constraint is what keeps this polynomial. Say that out loud and you sound senior.
