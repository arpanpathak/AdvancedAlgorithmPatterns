# 12.9 Partition To K Equal Sum Subsets

> **Source:** [`src/main/kotlin/backtracking/PartitionToKEqualSumSubsets.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/backtracking/PartitionToKEqualSumSubsets.kt)
> **Pattern:** subset-building backtracking · **Core page**

## The Problem

Given `nums` and `k`, can the array be partitioned into `k` subsets with **equal sums**?

- Constraints: $1 \le k \le 16$; $1 \le n \le 16$; values fit in `Int`.

## Examples

```
Input:  nums = [4,3,2,3,5,2,1], k = 4   -> Output: true   (each subset sums to 5)
Input:  nums = [1,2,3,4], k = 3         -> Output: false  (sum 10 not divisible by 3)
```

## Intuition — build subsets one at a time, each capped at `target`

First, the cheap impossibility filters: `total % k != 0` → false (equal sums force `target = total / k` to be integer). Then the search: **fill subsets one at a time**, each constrained to `<= target`, and when one reaches exactly `target`, start the next:

```
backtrack(start, currentSum, remainingSubsets):
    remainingSubsets == 0  -> true (all k subsets built)
    currentSum == target   -> backtrack(0, 0, remainingSubsets - 1)   # this subset done
    for i in start..n-1:
        if !used[i] && currentSum + nums[i] <= target:
            used[i] = true
            if backtrack(i + 1, currentSum + nums[i], remainingSubsets): return true
            used[i] = false     # undo
    return false
```

**Why the `<= target` pruning?** A subset can't exceed the target (excess is never rebalanced — all subsets are capped at the same value), so any element that would push past `target` is skipped *before* recursion. Combined with the `currentSum == target` early-out, this keeps the tree near the actual answer count.

**Why `i + 1` inside a subset but `0` when starting the next?** Within one subset, elements are chosen in increasing index order (dedupe, like [12.8](combination-sum.md)); when a subset completes, the *next* one may start anywhere unused — hence the `backtrack(0, ...)` reset. The `used` array is the shared state across subsets; the undo restores it for the next attempt.

**The early `% k` filter is the depth signal:** state it before any search — if `total % k != 0`, the answer is false in O(1) and the whole exponential search is moot.

## Approach 1 — DP over masks (O(k · 2^n))

`dp[mask] = can the used-set be partitioned?` — also correct; the backtracking below is the direct search.

## Approach 2 — Subset-building backtracking (the repo's version, optimal)

```kotlin
class PartitionToKEqualSumSubsets {
    /**
     * @param nums input array
     * @param k    number of equal-sum subsets
     * @return     true iff nums partitions into k equal-sum subsets
     */
    fun canPartitionKSubsets(nums: IntArray, k: Int): Boolean {
        val totalSum = nums.sum()
        if (totalSum % k != 0) return false          // equal sums need an integer target

        val targetSum = totalSum / k
        val used = BooleanArray(nums.size)

        fun backtrack(start: Int, currentSum: Int, remainingSubsets: Int): Boolean {
            if (remainingSubsets == 0) return true
            if (currentSum == targetSum)
                return backtrack(0, 0, remainingSubsets - 1)   // this subset is complete

            for (i in start until nums.size) {
                if (!used[i] && currentSum + nums[i] <= targetSum) {
                    used[i] = true
                    if (backtrack(i + 1, currentSum + nums[i], remainingSubsets))
                        return true
                    used[i] = false                          // undo
                }
            }
            return false
        }
        return backtrack(0, 0, k)
    }
}
```

```java
public class PartitionToKEqualSumSubsets {
    /**
     * @param nums input array
     * @param k    number of equal-sum subsets
     * @return     true iff nums partitions into k equal-sum subsets
     */
    public boolean canPartitionKSubsets(int[] nums, int k) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % k != 0) return false;            // equal sums need an integer target

        int target = total / k;
        boolean[] used = new boolean[nums.length];

        return backtrack(0, 0, k, target, used, nums);
    }

    private boolean backtrack(int start, int sum, int remaining,
                              int target, boolean[] used, int[] nums) {
        if (remaining == 0) return true;
        if (sum == target) return backtrack(0, 0, remaining - 1, target, used, nums);  // complete

        for (int i = start; i < nums.length; i++) {
            if (!used[i] && sum + nums[i] <= target) {
                used[i] = true;
                if (backtrack(i + 1, sum + nums[i], remaining, target, used, nums)) return true;
                used[i] = false;                     // undo
            }
        }
        return false;
    }
}
```

```cpp
#include <vector>

class PartitionToKEqualSumSubsets {
    bool backtrack(int start, int sum, int remaining, int target,
                   std::vector<bool>& used, std::vector<int>& nums) {
        if (remaining == 0) return true;
        if (sum == target) return backtrack(0, 0, remaining - 1, target, used, nums);  // complete

        for (int i = start; i < (int)nums.size(); i++) {
            if (!used[i] && sum + nums[i] <= target) {
                used[i] = true;
                if (backtrack(i + 1, sum + nums[i], remaining, target, used, nums)) return true;
                used[i] = false;                     // undo
            }
        }
        return false;
    }

public:
    /**
     * @param nums input array
     * @param k    number of equal-sum subsets
     * @return     true iff nums partitions into k equal-sum subsets
     */
    bool canPartitionKSubsets(std::vector<int>& nums, int k) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % k != 0) return false;            // equal sums need an integer target

        std::vector<bool> used(nums.size(), false);
        return backtrack(0, 0, k, total / k, used, nums);
    }
};
```

```python
def can_partition_k_subsets(nums: list[int], k: int) -> bool:
    """
    @param nums: input array
    @param k:    number of equal-sum subsets
    @return:     true iff nums partitions into k equal-sum subsets
    """
    total = sum(nums)
    if total % k != 0:
        return False                         # equal sums need an integer target

    target = total // k
    nums.sort(reverse=True)                  # big-first: fail fast on impossible elements
    used = [False] * len(nums)

    def backtrack(start: int, cur: int, remaining: int) -> bool:
        if remaining == 0:
            return True
        if cur == target:
            return backtrack(0, 0, remaining - 1)    # this subset is complete

        for i in range(start, len(nums)):
            if not used[i] and cur + nums[i] <= target:
                used[i] = True
                if backtrack(i + 1, cur + nums[i], remaining):
                    return True
                used[i] = False                        # undo
        return False

    return backtrack(0, 0, k)
```

```rust
impl Solution {
    /// @param nums input array
    /// @param k    number of equal-sum subsets
    /// @return     true iff nums partitions into k equal-sum subsets
    pub fn can_partition_k_subsets(nums: Vec<i32>, k: i32) -> bool {
        let total: i32 = nums.iter().sum();
        if total % k != 0 { return false; }            // equal sums need an integer target

        let target = total / k;
        let mut nums = nums;
        nums.sort_unstable_by(|a, b| b.cmp(a));        // big-first: fail fast
        let mut used = vec![false; nums.len()];

        fn backtrack(start: usize, cur: i32, remaining: i32, target: i32,
                     nums: &Vec<i32>, used: &mut Vec<bool>) -> bool {
            if remaining == 0 { return true; }
            if cur == target {
                return backtrack(0, 0, remaining - 1, target, nums, used);   // complete
            }
            for i in start..nums.len() {
                if !used[i] && cur + nums[i] <= target {
                    used[i] = true;
                    if backtrack(i + 1, cur + nums[i], remaining, target, nums, used) {
                        return true;
                    }
                    used[i] = false;                   // undo
                }
            }
            false
        }

        backtrack(0, 0, k, target, &nums, &mut used)
    }
}
```

## Dry run

**Input:** `nums = [4,3,2,3,5,2,1]`, `k = 4`. `total = 20`, `target = 5`.

```
backtrack(0, 0, 4): i=0: take 4 -> cur 4.  i=1: 3 -> 7 > 5 skip.  i=2: 2 -> 6 > 5 skip.
                   i=3: 3 -> 7 skip.  i=4: 5 skip.  i=5: 2 skip.  i=6: 1 -> 4+1 = 5 == target
                   -> backtrack(0, 0, 3):
                     i=0: 4 unused? no (used).  i=1: 3 -> cur 3.  i=2: 2 -> 5 == target
                     -> backtrack(0, 0, 2):
                       i=1..: 4,3 used.  i=4: 5 -> 5 == target -> backtrack(0,0,1):
                         i=2: 2 -> cur 2.  i=3: 3 -> 5 == target -> backtrack(0,0,0): remaining == 0 -> true

Output: true ✓   (subsets {4,1}, {3,2}, {5}, {2,3})
```

The subset-completion handoff is visible: each `cur == target` triggers `backtrack(0, 0, remaining - 1)` — a *fresh* subset scanning from index 0 (any unused element may start it), while the `used` array is the only shared state. The `4,1` → `3,2` → `5` → `2,3` chain builds all four subsets without conflict.

## Complexity

**Time.** Exponential worst case; the `<= target` pruning + big-first ordering make typical cases far smaller:

$$
T(n) = O(k \cdot 2^n) \text{ worst}
$$

**Space.** The `used` array + recursion:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Partition Equal Subset Sum** ([2.6](../ch02-dynamic-programming/partition-equal-subset-sum.md)) — the `k = 2` special case: a *subset-sum* DP instead of backtracking.
- **Matchsticks To Square** — literally `k = 4` of this problem; the same template.
- **Interview follow-up:** "Why sort descending as a pruning trick?" A too-large element (any single `nums[i] > target`) makes the answer false — descending order *finds* such an element on the very first attempts instead of deep in the tree. It also fills subsets quickly (big pieces first), which shortens the search when a valid partition exists.
