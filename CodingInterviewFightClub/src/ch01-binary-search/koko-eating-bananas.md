# 1.1 Koko Eating Bananas

> **Source:** [`src/main/kotlin/binarysearch/KokoEatingBanana.kt`](https://github.com/arpanpathak/Algorithms_Kotlin/blob/main/src/main/kotlin/binarysearch/KokoEatingBanana.kt)
> **Pattern:** binary search on the answer · **Core page**

## The Problem

Koko loves bananas. There are `n` piles, and the `i`-th pile has `piles[i]` bananas. The guards are going to be away for `h` hours, and Koko wants to eat **all** the bananas before they return.

Koko decides her eating speed: `k` bananas per hour. Each hour she picks one pile and eats `k` bananas from it. If a pile has fewer than `k` bananas, she eats the whole pile and then **cannot** eat anything else that hour (the hour is "wasted"). She can choose `k` freely, and can change it between hours — but the speed must be an integer.

Find the **minimum** integer `k` such that Koko can finish all piles within `h` hours.

- Constraints: $1 \le n \le 10^4$, $n \le h \le 10^9$, $1 \le piles[i] \le 10^9$.

## Examples

```
Example 1
Input:  piles = [3, 6, 7, 11], h = 8
Output: 4
Explanation: at k = 4:  pile 3 → 1h, pile 6 → 2h, pile 7 → 2h, pile 11 → 3h. Total = 8h. ✓
             at k = 3:  3→1h, 6→2h, 7→3h, 11→4h. Total = 10h > 8. ✗
             So 4 is the minimum speed that works.

Example 2
Input:  piles = [30, 11, 23, 4, 20], h = 5
Output: 30
Explanation: with only 5 hours, Koko must finish each pile in exactly 1 hour → k = 30 (max pile).
```

## Intuition — why binary search applies at all

Two facts make this a binary search problem instead of a brute-force problem:

1. **The answer lives in a small, ordered range.** The speed is between $1$ and $\max(piles)$ — eating faster than the largest pile never helps (each pile already takes ≥ 1 hour). So the candidate space is $[1, \max(piles)]$, size $R \le 10^9$.

2. **Feasibility is monotone in the speed.** Define $P(k)$ = "Koko can finish all piles in $\le h$ hours at speed $k$." If $k$ works, then **any** faster speed $k' > k$ also works — eating more bananas per hour can never make her *slower*. So $P$ looks like:

```
P(k):   F F F F F T T T T T
              ^
          answer = first true
```

This is *exactly* the **Binary Search Theorem** from [1.0](pattern-primer.md): the boundary between "too slow" and "fast enough" is the minimum valid speed, and halving finds it in $O(\log R)$ feasibility checks.

**The cost of one check:** at speed $k$, pile $i$ takes $\lceil piles[i] / k \rceil = \lfloor (piles[i] + k - 1) / k \rfloor$ hours (integer ceiling division — the "+k−1 then integer divide" trick). Sum over all piles, compare to $h$: $O(n)$ per check.

Total: $O(n \log R)$ — about $10^4 \times 30 = 3 \times 10^5$ operations for the worst case. A linear scan over all $R$ candidate speeds would be $10^4 \times 10^9$ — hopeless. **This is the entire point.**

## Approach 1 — Brute force (linear scan over speeds)

Try $k = 1, 2, 3, \dots$ until one works. The first working $k$ is the answer.

```python
def min_eating_speed(piles: list[int], h: int) -> int:
    """O(R) candidate speeds × O(n) check each = O(R·n). Too slow for R up to 1e9."""
    for k in range(1, max(piles) + 1):
        if sum((p + k - 1) // k for p in piles) <= h:
            return k
    return max(piles)
```

- **Time:** $O(R \cdot n)$ where $R = \max(piles)$ — up to $10^{13}$ operations. Dead on arrival for real constraints.
- **Space:** $O(1)$.

The structure ($P$ monotone) is exactly what binary search is for. The brute force *throws away* that structure; the optimal solution *exploits* it.

## Approach 2 — Binary search on the answer (optimal)

```kotlin
/**
 * @param piles the number of bananas in each pile (1..1e9 each)
 * @param h     the number of hours the guards are away (h >= piles.size)
 * @return      the minimum integer eating speed k (bananas/hour) such that
 *              Koko can finish all piles within h hours
 */
fun minEatingSpeed(piles: IntArray, h: Int): Int {
    var left = 1                      // too slow to be feasible, but a valid lower bound
    var right = piles.maxOrNull()!!   // always feasible: one hour per pile at most

    while (left < right) {            // Template A: find first true
        val mid = left + (right - left) / 2
        if (canEatAllBananas(piles, h, mid)) {
            right = mid               // feasible -> try slower
        } else {
            left = mid + 1            // infeasible -> must go faster
        }
    }
    return left
}

/**
 * @param piles the pile sizes
 * @param h     the hour budget
 * @param k     the candidate speed to test
 * @return      true iff sum of ceil(pile / k) over all piles is <= h
 */
private fun canEatAllBananas(piles: IntArray, h: Int, k: Int): Boolean {
    var totalHours = 0
    for (pile in piles) {
        totalHours += (pile + k - 1) / k   // ceiling division without floating point
    }
    return totalHours <= h
}
```

```java
public class KokoEatingBanana {
    /**
     * @param piles the number of bananas in each pile
     * @param h     the number of hours the guards are away (h >= piles.length)
     * @return      the minimum integer speed k such that all piles finish within h hours
     */
    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = 0;
        for (int pile : piles) right = Math.max(right, pile);   // max pile = always-feasible speed

        while (left < right) {
            int mid = left + (right - left) / 2;                // overflow-safe midpoint
            if (canEatAllBananas(piles, h, mid)) {
                right = mid;                                    // feasible -> slow down
            } else {
                left = mid + 1;                                 // infeasible -> speed up
            }
        }
        return left;
    }

    /**
     * @param piles the pile sizes
     * @param h     the hour budget
     * @param k     candidate speed to test
     * @return      true iff sum of ceil(pile / k) over all piles is <= h
     */
    private boolean canEatAllBananas(int[] piles, int h, int k) {
        long totalHours = 0;
        for (int pile : piles) {
            totalHours += (pile + k - 1L) / k;                  // 1L avoids overflow on the +k-1
        }
        return totalHours <= h;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class KokoEatingBanana {
public:
    /**
     * @param piles the number of bananas in each pile
     * @param h     the number of hours the guards are away (h >= piles.size())
     * @return      the minimum integer speed k such that all piles finish within h hours
     */
    int minEatingSpeed(const std::vector<int>& piles, int h) {
        int left = 1;
        int right = *std::max_element(piles.begin(), piles.end());  // always-feasible speed

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canEatAllBananas(piles, h, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

private:
    /**
     * @param piles the pile sizes
     * @param h     the hour budget
     * @param k     candidate speed to test
     * @return      true iff sum of ceil(pile / k) over all piles is <= h
     */
    bool canEatAllBananas(const std::vector<int>& piles, int h, int k) {
        long long totalHours = 0;
        for (int pile : piles) {
            totalHours += (pile + k - 1LL) / k;   // integer ceiling division
        }
        return totalHours <= h;
    }
};
```

```python
def min_eating_speed(piles: list[int], h: int) -> int:
    """
    @param piles: the number of bananas in each pile
    @param h:     the number of hours the guards are away (h >= len(piles))
    @return:      the minimum integer speed k such that all piles finish within h hours
    """
    left, right = 1, max(piles)          # right = always-feasible speed
    while left < right:
        mid = left + (right - left) // 2
        if can_eat_all(piles, h, mid):
            right = mid                  # feasible -> try slower
        else:
            left = mid + 1               # infeasible -> must go faster
    return left


def can_eat_all(piles: list[int], h: int, k: int) -> bool:
    """
    @param piles: the pile sizes
    @param h:     the hour budget
    @param k:     candidate speed to test
    @return:      True iff sum of ceil(pile / k) over all piles is <= h
    """
    total_hours = sum((pile + k - 1) // k for pile in piles)   # ceiling division
    return total_hours <= h
```

```rust
impl Solution {
    /// @param piles the number of bananas in each pile
    /// @param h     the number of hours the guards are away (h >= piles.len())
    /// @return      the minimum integer speed k such that all piles finish within h hours
    pub fn min_eating_speed(piles: Vec<i32>, h: i32) -> i32 {
        let (mut left, mut right) = (1, *piles.iter().max().unwrap());   // right = always-feasible
        while left < right {
            let mid = left + (right - left) / 2;
            if Self::can_eat_all(&piles, h, mid) {
                right = mid;                 // feasible -> try slower
            } else {
                left = mid + 1;              // infeasible -> must go faster
            }
        }
        left
    }

    /// @param piles the pile sizes
    /// @param h     the hour budget
    /// @param k     candidate speed to test
    /// @return      true iff sum of ceil(pile / k) over all piles is <= h
    fn can_eat_all(piles: &[i32], h: i32, k: i32) -> bool {
        let total: i64 = piles.iter().map(|&p| (p as i64 + k as i64 - 1) / k as i64).sum();
        total <= h as i64
    }
}
```

## Dry run

**Input:** `piles = [3, 6, 7, 11]`, `h = 8`. Candidate space $[1, 11]$:

```
left=1  right=11  mid=6  -> hours(6) = 1+1+2+2 = 6 <= 8  FEASIBLE -> right=6
left=1  right=6   mid=3  -> hours(3) = 1+2+3+4 = 10 > 8  NOT      -> left=4
left=4  right=6   mid=5  -> hours(5) = 1+2+2+3 = 8  <= 8  FEASIBLE -> right=5
left=4  right=5   mid=4  -> hours(4) = 1+2+2+3 = 8  <= 8  FEASIBLE -> right=4
left=4  right=4   -> loop ends, return 4
```

Let's verify the feasibility check by hand for the two interesting midpoints:

| Speed k | pile 3 | pile 6 | pile 7 | pile 11 | total hours | vs h=8 |
|---|---|---|---|---|---|---|
| 3 | ⌈3/3⌉=1 | ⌈6/3⌉=2 | ⌈7/3⌉=3 | ⌈11/3⌉=4 | **10** | too slow ✗ |
| 4 | ⌈3/4⌉=1 | ⌈6/4⌉=2 | ⌈7/4⌉=2 | ⌈11/4⌉=3 | **8** | works ✓ |

Notice the search never even *looked* at speeds 1, 2, 7–11 — it discarded them in 5 halving steps. Brute force would have examined 11.

**Why `right` lands on a feasible answer:** `right` starts at $\max(piles)$ (feasible: each pile takes exactly 1 hour) and only ever moves *down* onto feasible midpoints. `left` starts at 1 (infeasible whenever $h < n$; but even when 1 is feasible, the invariant still finds the *minimum* feasible because we always probe lower). The invariant "left infeasible, right feasible" survives every iteration, so at termination `left == right` is the boundary = answer.

## Complexity

**Time.** Each iteration runs `canEatAllBananas` in $O(n)$ (one pass, integer divisions). The search space $[1, \max(piles)]$ has size $R$, halved $\log_2 R$ times:

$$
T(n) = n \cdot \log_2 R = O(n \log R)
$$

With $n \le 10^4$ and $R \le 10^9$: $\approx 10^4 \times 30 = 3 \times 10^5$ operations.

**Space.** Four scalar variables plus the input: $O(1)$ auxiliary.

## Variants & follow-ups

- **Capacity To Ship Packages Within D Days** ([1.2](capacity-to-ship-packages.md)) — identical shape; the feasibility check becomes a greedy packer.
- **Minimum Number of Taps to Water a Garden** (`src/main/kotlin/array/greedy/`) — "monotone in the answer" again, but the check is greedy interval covering.
- **House Robber IV** ([1.10](house-robber-iv.md)) — binary search on the *capability* (a value), with a greedy "rob non-adjacent" check.
- **Interview follow-up:** "What if `h` can be smaller than `n`?" Then the answer is still well-defined, but the range shrinks — the loop handles it, because $k \ge \max(piles)$ is always feasible and the search space is never empty.
- **Interview follow-up:** "Prove ceiling division." $\lceil a/b \rceil = \lfloor (a + b - 1) / b \rfloor$ because $a = qb + r$ with $0 \le r < b$; adding $b - 1$ pushes the numerator past the next multiple of $b$ exactly when $r > 0$.
