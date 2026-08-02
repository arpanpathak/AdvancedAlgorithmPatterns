# 2.17 House Robber

> **Source:** [`src/main/kotlin/array/dp/HouseRobber.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/HouseRobber.kt) (+ `HouseRobber_II.kt`, `MaximumSumOfNonAdjacentElements.kt` — the same problem under two names)
> **Pattern:** include/exclude DP with two running vars · **Core page**

## The Problem

Given `nums[i]` (money in house i), rob the **maximum** amount without robbing **adjacent** houses.

- Constraints: $1 \le n \le 100$; values fit in `Int`.

## Examples

```
Input:  nums = [2,7,9,3,1]   -> Output: 12   (rob houses 0, 2, 4)
Input:  nums = [2,1,1,2]     -> Output: 4    (rob 0 and 3)
```

## Intuition — at each house, "take it" vs "skip it"; only the last two answers matter

The decision at house `i` is binary: rob it (then house `i-1` is off-limits) or skip it. The classic recurrence:

$$
dp[i] = \max(dp[i-1],\; nums[i] + dp[i-2])
$$

**Why do only two previous answers matter?** `dp[i]` depends only on `dp[i-1]` and `dp[i-2]` — the repo's `rob_iterative` carries them as two variables (`include`, `exclude`) with a `temp` swap, reaching $O(1)$ space. This is the "rolling two" pattern from [2.16](coin-change.md)'s table compressed to scalars.

**The repo's top-down** (`rob(dp, nums, i)`) reads: `maxOf(nums[i] + rob(i+2), rob(i+1))` — the same recurrence written as a memoized DFS. Both are the [12.0](../ch12-backtracking/pattern-primer.md) include/exclude shape without the undo (no constraint to respect besides adjacency).

**The `i >= nums.size -> 0` base case** is the chain's end: beyond the last house, nothing to rob. `dp` indexed by house position (not amount) because the *order* is the constraint — compare [2.16](coin-change.md) where amount was the axis.

## Approach 1 — Memoized recursion (the repo's style)

`rob(i) = max(rob(i+1), nums[i] + rob(i+2))` with an `IntArray` memo: correct, $O(n)$ space.

## Approach 2 — Two rolling variables (the repo's `rob_iterative`, optimal)

```kotlin
class HouseRobber {
    /**
     * @param nums money per house
     * @return     max money without robbing adjacent houses
     */
    fun rob(nums: IntArray): Int {
        if (nums.isEmpty()) return 0

        var include = 0       // best ending with robbing the current house
        var exclude = 0       // best ending with skipping it

        for (num in nums) {
            val temp = include
            include = num + exclude      // rob this house: must have skipped the last
            exclude = maxOf(temp, exclude)   // skip this house: keep the better of the two
        }
        return maxOf(include, exclude)
    }
}
```

```java
public class HouseRobber {
    /**
     * @param nums money per house
     * @return     max money without robbing adjacent houses
     */
    public int rob(int[] nums) {
        int include = 0, exclude = 0;
        for (int num : nums) {
            int temp = include;
            include = num + exclude;                 // rob this: skip the previous
            exclude = Math.max(temp, exclude);       // skip this: keep the best so far
        }
        return Math.max(include, exclude);
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class HouseRobber {
public:
    /**
     * @param nums money per house
     * @return     max money without robbing adjacent houses
     */
    int rob(std::vector<int>& nums) {
        int include = 0, exclude = 0;
        for (int num : nums) {
            int temp = include;
            include = num + exclude;                 // rob this: skip the previous
            exclude = std::max(temp, exclude);       // skip this: keep the best so far
        }
        return std::max(include, exclude);
    }
};
```

```python
def rob(nums: list[int]) -> int:
    """
    @param nums: money per house
    @return:     max money without robbing adjacent houses
    """
    include = exclude = 0
    for num in nums:
        temp = include
        include = num + exclude          # rob this: skip the previous
        exclude = max(temp, exclude)     # skip this: keep the best so far
    return max(include, exclude)
```

```rust
impl Solution {
    /// @param nums money per house
    /// @return     max money without robbing adjacent houses
    pub fn rob(nums: Vec<i32>) -> i32 {
        let (mut include, mut exclude) = (0, 0);
        for num in nums {
            let temp = include;
            include = num + exclude;             // rob this: skip the previous
            exclude = temp.max(exclude);         // skip this: keep the best so far
        }
        include.max(exclude)
    }
}
```

## Reading the code — what's actually happening

```kotlin
var include = 0       // best ending with robbing the current house
var exclude = 0       // best ending with skipping it
for (num in nums) {
    val temp = include
    include = num + exclude          // rob this house: must have skipped the last
    exclude = maxOf(temp, exclude)   // skip this house: keep the better of the two
}
return maxOf(include, exclude)
```

Two variables act as a tiny state machine, and the order of the three lines is the entire logic. Walk through one house at a time:

- **`include` means "the best total where the last house was robbed".** To rob house `i`, house `i-1` must NOT have been robbed — so the new `include` is `num + exclude` (this house's money plus the best total *ending with a skip* before it). We never add to the old `include`, because robbing two adjacent houses is illegal.
- **`exclude` means "the best total where the last house was skipped".** Skipping house `i` lets us keep whichever was better before: `maxOf(temp, exclude)` — `temp` is the old `include` (we *could* have robbed the previous house and now skip this one), `exclude` is the old skip. Taking the max is the DP's "best so far".
- **`temp` preserves the old `include`** because the next line overwrites it. Without the save, `exclude` would compare against the *new* include — double-counting this house. This is the classic rolling-variable shuffle: three values, two slots, one temp.
- **After the loop, the answer is `max(include, exclude)`** — the best ending with a rob vs. the best ending with a skip; the better of the two is the global optimum.

Trace `[2,7,9,3,1]`: after house 2 (value 2): include 2, exclude 0. House 7: include `7+0=7`, exclude `max(2,0)=2`. House 9: include `9+2=11`, exclude `max(7,2)=7`. House 3: include `3+7=10`, exclude 11. House 1: include `1+11=12`, exclude 11. Answer `max(12,11)=12` ✓ — houses 0, 2, 4.

## Dry run

**Input:** `nums = [2,7,9,3,1]`.

```
include=0, exclude=0
num=2: temp=0.  include=2+0=2.  exclude=max(0,0)=0.
num=7: temp=2.  include=7+0=7.  exclude=max(2,0)=2.
num=9: temp=7.  include=9+2=11. exclude=max(7,2)=7.
num=3: temp=11. include=3+7=10. exclude=max(11,7)=11.
num=1: temp=10. include=1+11=12. exclude=max(10,11)=11.

Output: max(12, 11) = 12 ✓   (houses 0, 2, 4 = 2+9+1)
```

The swap is the state machine: `include` (rob *this* house → must have skipped the last) always rebuilds from `exclude`, and `exclude` (skip *this* house → keep the running best) absorbs the old `include` via `temp`. Two scalars carry the whole recurrence `dp[i] = max(dp[i-1], nums[i] + dp[i-2])`.

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Two variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **House Robber II** (`array/dp/HouseRobber_II.kt`) — the circular street: run the linear DP twice (skip house 0, skip house n-1) and take the max. Same two-variable core.
- **House Robber III** (`graph/HouseRobber3.kt`) — the *tree* version: post-order with `(rob, skip)` pairs per node (see [5.4](../ch05-trees/binary-tree-maximum-path-sum.md)'s DFS-returning-state shape).
- **House Robber IV** ([1.10](../ch01-binary-search/house-robber-iv.md)) — the binary-search twist: minimize the *capability* with a greedy feasibility check.
- **Interview follow-up:** "Why can't you just take every other house?" The optimum isn't necessarily an alternating pattern (`[2,1,1,2]` → take 0 and 3 = 4, but alternating from 0 gives 2+1=3). The DP's max-at-each-step is what lets the pattern break and rejoin.
