# 1.10 House Robber IV

> **Source:** [`src/main/kotlin/binarysearch/HouseRobber_IV.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/binarysearch/HouseRobber_IV.kt)
> **Pattern:** binary search on the answer + greedy check · **Gym page**

## The Problem

There are `n` houses in a row; house `i` holds `nums[i]` money. You must rob **exactly `k`** houses, **no two adjacent**, and you want to minimize the **maximum amount robbed from any single house** (the "capability" of the heist).

Formally: choose indices $i_1 < i_2 < \cdots < i_k$ with $i_{j+1} - i_j \ge 2$ minimizing $\max_j nums[i_j]$. Return that minimum capability.

- Constraints: $1 \le k \le \lceil n/2 \rceil$, $1 \le n \le 10^5$.

## Examples

```
Input:  nums = [2, 3, 5, 9], k = 2
Output: 5
Explanation: robbing houses 1 and 3 (values 3, 5) or 2 and 4 (5, 9): min max = max(3,5) = 5.
             Robbing 0 and 2 (2, 5) is also valid: max = 5. Can't do better: any 2-house
             choice with max < 5 must use two houses both < 5 = {2, 3}, but they're adjacent.

Input:  nums = [2, 7, 9, 3, 1], k = 2
Output: 2
Explanation: rob house 0 (2) and house 4 (1): max = 2. The two cheapest non-adjacent.
```

## Intuition — a minimax problem, inverted

"Minimize the maximum" is the smell of **binary search on the answer**. The direct optimization ("which k houses?") is combinatorial — $\binom{n}{k}$ choices. But the *decision version* is trivial:

> **Decision:** can we pick $k$ non-adjacent houses, all with value $\le cap$?

Greedy answer: scan left to right; whenever `nums[i] <= cap`, rob it and skip the next house (greedy non-adjacent selection). The greedy count is *maximal* — skipping a rob-able house can never let you rob more — so `greedyCount(cap) >= k` is exactly "feasible". And feasibility is **monotone in cap**: bigger cap → more rob-able houses → never fewer rob-able choices.

So the answer is $\min\{cap : \text{greedyCount}(cap) \ge k\}$ over $cap \in [\min(nums), \max(nums)]$ — **Template A from [1.0](pattern-primer.md)** with the feasibility check being the greedy robber. This is the same skeleton as Koko ([1.1](koko-eating-bananas.md)) and Capacity ([1.2](capacity-to-ship-packages.md)): *binary search on the answer + O(n) check*.

## Approach 1 — Brute force

Try every subset of exactly k non-adjacent houses: $\binom{n}{k}$ subsets, each costing $O(k)$ to evaluate → exponential. For $n = 10^5$, hopeless.

## Approach 2 — Binary search on capability (optimal)

```kotlin
/**
 * @param nums the money in each house (values are the capability candidates)
 * @param k    the exact number of non-adjacent houses to rob
 * @return     the minimum possible maximum amount robbed from any single house
 */
fun minCapability(nums: IntArray, k: Int): Int {
    var left = nums.minOrNull() ?: 0
    var right = nums.maxOrNull() ?: 0

    /**
     * @param cap the capability ceiling being tested
     * @return    true iff greedy can pick >= k non-adjacent houses each with value <= cap
     */
    fun canRob(cap: Int): Boolean {
        var robbed = 0
        var i = 0
        while (i < nums.size) {
            if (nums[i] <= cap) {
                robbed++
                i += 2          // skip the adjacent house
            } else {
                i++
            }
        }
        return robbed >= k
    }

    while (left < right) {
        val mid = (left + right) / 2   // values fit in Int; (left+right) is safe for n <= 1e5 but
                                       // prefer left + (right-left)/2 for the overflow-proof habit
        if (canRob(mid)) {
            right = mid                 // feasible -> try a smaller capability
        } else {
            left = mid + 1              // infeasible -> need a bigger capability
        }
    }
    return left
}
```

```java
public class HouseRobberIV {
    /**
     * @param nums the money in each house
     * @param k    the exact number of non-adjacent houses to rob
     * @return     the minimum possible maximum amount robbed from any single house
     */
    public int minCapability(int[] nums, int k) {
        int left = Integer.MAX_VALUE, right = Integer.MIN_VALUE;
        for (int v : nums) { left = Math.min(left, v); right = Math.max(right, v); }

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canRob(nums, mid, k)) right = mid;
            else left = mid + 1;
        }
        return left;
    }

    /**
     * @param nums the house values
     * @param cap  the capability ceiling being tested
     * @param k    the number of houses required
     * @return     true iff greedy can pick >= k non-adjacent houses with value <= cap
     */
    private boolean canRob(int[] nums, int cap, int k) {
        int robbed = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] <= cap) { robbed++; i++; }   // skip the adjacent house
        }
        return robbed >= k;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class HouseRobberIV {
public:
    /**
     * @param nums the money in each house
     * @param k    the exact number of non-adjacent houses to rob
     * @return     the minimum possible maximum amount robbed from any single house
     */
    int minCapability(const std::vector<int>& nums, int k) {
        auto [lo, hi] = std::minmax_element(nums.begin(), nums.end());
        int left = *lo, right = *hi;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canRob(nums, mid, k)) right = mid;
            else left = mid + 1;
        }
        return left;
    }

private:
    /**
     * @param nums the house values
     * @param cap  the capability ceiling being tested
     * @param k    the number of houses required
     * @return     true iff greedy can pick >= k non-adjacent houses with value <= cap
     */
    bool canRob(const std::vector<int>& nums, int cap, int k) {
        int robbed = 0;
        for (int i = 0; i < (int)nums.size(); i++) {
            if (nums[i] <= cap) { robbed++; i++; }
        }
        return robbed >= k;
    }
};
```

```python
def min_capability(nums: list[int], k: int) -> int:
    """
    @param nums: the money in each house
    @param k:    the exact number of non-adjacent houses to rob
    @return:     the minimum possible maximum amount robbed from any single house
    """
    left, right = min(nums), max(nums)

    def can_rob(cap: int) -> bool:
        """
        @param cap: the capability ceiling being tested
        @return:    True iff greedy can pick >= k non-adjacent houses with value <= cap
        """
        robbed, i = 0, 0
        while i < len(nums):
            if nums[i] <= cap:
                robbed += 1
                i += 2             # skip the adjacent house
            else:
                i += 1
        return robbed >= k

    while left < right:
        mid = left + (right - left) // 2
        if can_rob(mid):
            right = mid            # feasible -> try a smaller capability
        else:
            left = mid + 1         # infeasible -> need a bigger capability
    return left
```

```rust
impl Solution {
    /// @param nums the money in each house
    /// @param k    the exact number of non-adjacent houses to rob
    /// @return     the minimum possible maximum amount robbed from any single house
    pub fn min_capability(nums: Vec<i32>, k: i32) -> i32 {
        let (mut left, mut right) = (*nums.iter().min().unwrap(), *nums.iter().max().unwrap());

        while left < right {
            let mid = left + (right - left) / 2;
            if Self::can_rob(&nums, mid, k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        left
    }

    /// @param nums the house values
    /// @param cap  the capability ceiling being tested
    /// @param k    the number of houses required
    /// @return     true iff greedy can pick >= k non-adjacent houses with value <= cap
    fn can_rob(nums: &[i32], cap: i32, k: i32) -> bool {
        let mut robbed = 0;
        let mut i = 0;
        while i < nums.len() {
            if nums[i] <= cap {
                robbed += 1;
                i += 2;             // skip the adjacent house
            } else {
                i += 1;
            }
        }
        robbed >= k
    }
}
```

## Dry run

**Input:** `nums = [2, 3, 5, 9]`, `k = 2`. Search space $[2, 9]$:

```
left=2  right=9  mid=5  canRob(5): rob 2 (i->2), 5 (i->4) -> 2 robbed >= 2 FEASIBLE -> right=5
left=2  right=5  mid=3  canRob(3): rob 2 (i->2), 5? >3 no (i->3), 9? no -> 1 robbed < 2 NOT -> left=4
left=4  right=5  mid=4  canRob(4): rob 2 (i->2), 5>4 no, 9>4 no -> 1 robbed NOT -> left=5
left=5  right=5  -> return 5 ✓
```

Verify the greedy counts by hand:

| cap | greedy trace | robbed | ≥ 2? |
|---|---|---|---|
| 5 | 2 ✓ (skip 3), 5 ✓ (skip 9) | 2 | ✓ |
| 4 | 2 ✓ (skip 3), 5 ✗, 9 ✗ | 1 | ✗ |
| 3 | 2 ✓ (skip 3), 5 ✗, 9 ✗ | 1 | ✗ |

**Why the greedy is optimal:** when `nums[i] <= cap`, robbing house `i` and skipping `i+1` is never worse than skipping `i` (which would leave you at `i+1`, adjacent to nothing robbed yet — strictly fewer options). The greedy count is therefore the *maximum* number of rob-able houses, so it decides feasibility exactly.

## Complexity

**Time.** $\log_2(\max - \min)$ halving steps × an $O(n)$ greedy pass each:

$$
T(n) = O(n \log V), \qquad V = \max(nums) - \min(nums) \le 10^9
$$

With $n = 10^5$: $\approx 10^5 \times 30 = 3 \times 10^6$ operations. (The values fit in `Int`, so `(left + right) / 2` is safe here, but the overflow-proof `left + (right - left) / 2` is the habit to keep.)

**Space.** $O(1)$.

## Variants & follow-ups

- **Koko Eating Bananas** ([1.1](koko-eating-bananas.md)) and **Capacity to Ship** ([1.2](capacity-to-ship-packages.md)) — the same "binary search on the answer + greedy feasibility" skeleton; the only difference is *what* the greedy counts.
- **House Robber** (classic DP, `src/main/kotlin/array/dp/`) — same "no two adjacent" constraint but *maximizing total value*; that's a DP, not a binary search. Knowing *why* (no monotone ceiling to search) is the interview gold.
- **Interview follow-up:** "Why is 'exactly k' the same as 'at least k' here?" If you can rob $m > k$ non-adjacent houses with max value ≤ cap, you can drop any $m - k$ of them and still have $k$ non-adjacent with max ≤ cap — so feasibility is preserved. That's why `robbed >= k` is the right check.
