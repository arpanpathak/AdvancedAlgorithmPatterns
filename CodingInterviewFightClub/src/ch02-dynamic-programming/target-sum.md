# 2.21 Target Sum

> **Source:** [`src/main/kotlin/array/dp/TargetSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/TargetSum.kt)
> **Pattern:** (index, sum) memo · **Core page**

## The Problem

Assign `+` or `-` before each `nums[i]`; count the ways the expression sums to `target`.

- Constraints: $1 \le n \le 20$; sum fits in `Int`.

## Examples

```
Input:  nums = [1,1,1,1,1], target = 3   -> Output: 5   (four +1 and one -1: C(5,1))
Input:  nums = [1], target = 1           -> Output: 1
```

## Intuition — the decision is binary (+/−); the state is (index, running sum)

Each number gets a sign — the classic pick-skip recursion with a sign axis:

```
ways(index, sum):
    index == n       -> 1 iff sum == target
    else             -> ways(index+1, sum + nums[index]) + ways(index+1, sum - nums[index])
```

**Why memoize on `(index, sum)`?** The same `(i, sum)` recurs in many sign-assignment paths (`+1-1+1` and `-1+1+1` both reach sum 1 at index 3). The repo keys the cache by `"$index $sum"` — a string key for the pair. The state space is `n × sum-range`, and with n ≤ 20 that's tiny.

**The subset-sum transform** (the repo's second method): `sum(P) - sum(N) = target` and `sum(P) + sum(N) = total` ⟹ `sum(P) = (total + target) / 2` — counting subsets reaching that sum is the [2.6](partition-equal-subset-sum.md) engine. Mention it as the "there's a math shortcut" follow-up.

## Approach 1 — Brute force all 2^n assignments

Enumerate every sign combination: correct, exponential (fine at n ≤ 20, memoizable).

## Approach 2 — Memoized (index, sum) (the repo's version, optimal)

```kotlin
class TargetSum {
    private val dp = mutableMapOf<String, Int>()

    /**
     * @param nums   input values
     * @param target target expression sum
     * @return       number of +/- assignments reaching target
     */
    fun findTargetSumWays(nums: IntArray, target: Int): Int {
        return ways(nums, target, 0, 0)
    }

    private fun ways(nums: IntArray, target: Int, index: Int, sum: Int): Int {
        val state = "$index $sum"
        return when {
            index >= nums.size -> if (sum == target) 1 else 0
            dp.containsKey(state) -> dp[state]!!
            else -> {
                val count = ways(nums, target, index + 1, sum + nums[index]) +
                        ways(nums, target, index + 1, sum - nums[index])
                dp[state] = count
                count
            }
        }
    }
}
```

```java
import java.util.*;

public class TargetSum {
    /**
     * @param nums   input values
     * @param target target expression sum
     * @return       number of +/- assignments reaching target
     */
    public int findTargetSumWays(int[] nums, int target) {
        Map<String, Integer> memo = new HashMap<>();
        return ways(nums, target, 0, 0, memo);
    }

    private int ways(int[] nums, int target, int i, int sum, Map<String, Integer> memo) {
        String key = i + " " + sum;
        if (memo.containsKey(key)) return memo.get(key);
        if (i == nums.length) return sum == target ? 1 : 0;

        int count = ways(nums, target, i + 1, sum + nums[i], memo)
                  + ways(nums, target, i + 1, sum - nums[i], memo);
        memo.put(key, count);
        return count;
    }
}
```

```cpp
#include <unordered_map>
#include <string>
#include <vector>

class TargetSum {
    std::unordered_map<std::string, int> memo;

    int ways(std::vector<int>& nums, int target, int i, int sum) {
        std::string key = std::to_string(i) + " " + std::to_string(sum);
        if (memo.count(key)) return memo[key];
        if (i == (int)nums.size()) return sum == target ? 1 : 0;

        int count = ways(nums, target, i + 1, sum + nums[i])
                  + ways(nums, target, i + 1, sum - nums[i]);
        return memo[key] = count;
    }

public:
    /**
     * @param nums   input values
     * @param target target expression sum
     * @return       number of +/- assignments reaching target
     */
    int findTargetSumWays(std::vector<int>& nums, int target) {
        return ways(nums, target, 0, 0);
    }
};
```

```python
def find_target_sum_ways(nums: list[int], target: int) -> int:
    """
    @param nums:   input values
    @param target: target expression sum
    @return:       number of +/- assignments reaching target
    """
    from functools import lru_cache

    @lru_cache(None)
    def ways(i: int, s: int) -> int:
        if i == len(nums):
            return 1 if s == target else 0
        return ways(i + 1, s + nums[i]) + ways(i + 1, s - nums[i])

    return ways(0, 0)
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums   input values
    /// @param target target expression sum
    /// @return       number of +/- assignments reaching target
    pub fn find_target_sum_ways(nums: Vec<i32>, target: i32) -> i32 {
        fn ways(nums: &[i32], target: i32, i: usize, sum: i32,
                memo: &mut HashMap<(usize, i32), i32>) -> i32 {
            if i == nums.len() { return if sum == target { 1 } else { 0 }; }
            if let Some(&v) = memo.get(&(i, sum)) { return v; }

            let count = ways(nums, target, i + 1, sum + nums[i], memo)
                      + ways(nums, target, i + 1, sum - nums[i], memo);
            memo.insert((i, sum), count);
            count
        }

        ways(&nums, target, 0, 0, &mut HashMap::new())
    }
}
```

## Dry run

**Input:** `nums = [1,1,1,1,1]`, `target = 3`.

```
ways(0,0): +1 -> ways(1,1): +1 -> ... -> ways(5,5): 5==3? no -> 0
                                      ... -1 -> ways(5,3): 3==3 -> 1
Each path with exactly one -1 reaches sum 3 at the end (4 - 1 = 3).
The recursion counts C(5,1) = 5 such paths.

ways(0,0) = 5 ✓
```

The memo's saving: `ways(3, 1)` (after `+1-1+1` or `-1+1+1`) is computed once and reused by both parents — the `"$index $sum"` key is exactly the path-summary that makes the 2^n tree collapse to `n × sum-range` states.

## Complexity

**Time.** States × O(1):

$$
T(n, S) = O(n \cdot S)
$$

**Space.** The memo:

$$
S(n, S) = O(n \cdot S)
$$

## Variants & follow-ups

- **Partition Equal Subset Sum** ([2.6](partition-equal-subset-sum.md)) — the subset-sum transform's home: `sum(P) = (total + target)/2`.
- **Count Ways To Pick K Coins** ([2.14](count-ways-to-pick-k-coins-divisible-by-m.md)) — the same (index, remainder) counting shape.
- **Interview follow-up:** "When is the subset-sum transform worth it?" When `target` is near `total/2`, the subset-sum DP runs in O(n·total) with a 1-D array — often faster than the 2-D (index, sum) memo. The transform is `sum(P) = (total + target) / 2`; it requires `(total + target)` even and within range.
