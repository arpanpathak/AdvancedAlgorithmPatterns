# 1.20 Split Array Largest Sum

> **Source:** [`src/main/kotlin/array/dp/SplitArrayLargestSum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/dp/SplitArrayLargestSum.kt)
> **Pattern:** minimize the max — binary search the answer · **Core page**

## The Problem

Split `nums` into `k` contiguous subarrays minimizing the **largest sum** among them.

- Constraints: $1 \le k \le n \le 1000$.

## Examples

```
Input:  nums = [7,2,5,10,8], k = 2   -> Output: 18   ([7,2,5] and [10,8])
Input:  nums = [1,2,3,4,5], k = 2    -> Output: 9    ([1,2,3] and [4,5])
```

## Intuition — "minimize the max" is binary-searchable

If a cap `C` can split the array into ≤ k parts each ≤ C, then any larger cap also works — **monotone**. Binary search the smallest feasible cap in `[max(nums), sum(nums)]`:

```
canSplit(C): greedy-pack: walk nums, start a new part whenever the running sum would exceed C.
             feasible iff parts ≤ k.

binary search: lo = max(nums), hi = sum(nums)
  mid = (lo + hi) / 2
  canSplit(mid) ? hi = mid : lo = mid + 1
return lo
```

**Why `max(nums)` as the floor?** No part can hold less than the biggest element — `C < max(nums)` is infeasible by construction. The [1.x](../ch01-binary-search/pattern-primer.md) "answer must lie in [lo, hi]" tightening.

**Why greedy packing for feasibility?** To test a cap, greedily extend the current part as far as possible — any feasible split can be rearranged into the greedy one (exchange argument), so the greedy part count is the *minimum* possible. The repo's memoized DP ([2.x](../ch02-dynamic-programming/pattern-primer.md) `dfs(i, splitsLeft)` over prefix sums) is the exact alternative; the binary search is the canonical LeetCode answer.

## Approach 1 — DP over (index, parts) (the repo's version)

`dfs(i, k)` = min largest sum splitting `nums[i:]` into k parts (prefix-sum lookups, prune when `currentSum > best`): O(n²k).

## Approach 2 — Binary search the answer (optimal)

```kotlin
class SplitArrayLargestSum {
    /**
     * @param nums input array
     * @param k    number of subarrays
     * @return     minimized largest subarray sum
     */
    fun splitArray(nums: IntArray, k: Int): Int {
        var lo = 0L
        var hi = 0L
        for (num in nums) { lo = maxOf(lo, num.toLong()); hi += num }

        fun canSplit(cap: Long): Boolean {
            var parts = 1
            var running = 0L
            for (num in nums) {
                if (running + num > cap) { parts++; running = 0 }
                running += num
            }
            return parts <= k
        }

        while (lo < hi) {
            val mid = lo + (hi - lo) / 2
            if (canSplit(mid)) hi = mid
            else lo = mid + 1
        }
        return lo.toInt()
    }
}
```

```java
public class SplitArrayLargestSum {
    /**
     * @param nums input array
     * @param k    number of subarrays
     * @return     minimized largest subarray sum
     */
    public int splitArray(int[] nums, int k) {
        long lo = 0, hi = 0;
        for (int num : nums) { lo = Math.max(lo, num); hi += num; }

        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;

            int parts = 1;
            long running = 0;
            for (int num : nums) {
                if (running + num > mid) { parts++; running = 0; }
                running += num;
            }

            if (parts <= k) hi = mid;
            else lo = mid + 1;
        }
        return (int) lo;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class SplitArrayLargestSum {
public:
    /**
     * @param nums input array
     * @param k    number of subarrays
     * @return     minimized largest subarray sum
     */
    int splitArray(std::vector<int>& nums, int k) {
        long lo = 0, hi = 0;
        for (int num : nums) { lo = std::max(lo, (long)num); hi += num; }

        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;

            int parts = 1;
            long running = 0;
            for (int num : nums) {
                if (running + num > mid) { parts++; running = 0; }
                running += num;
            }

            if (parts <= k) hi = mid;
            else lo = mid + 1;
        }
        return (int)lo;
    }
};
```

```python
def split_array(nums: list[int], k: int) -> int:
    """
    @param nums: input array
    @param k:    number of subarrays
    @return:     minimized largest subarray sum
    """
    def can_split(cap: int) -> bool:
        parts = 1
        running = 0
        for num in nums:
            if running + num > cap:
                parts += 1
                running = 0
            running += num
        return parts <= k

    lo, hi = max(nums), sum(nums)
    while lo < hi:
        mid = (lo + hi) // 2
        if can_split(mid):
            hi = mid
        else:
            lo = mid + 1
    return lo
```

```rust
impl Solution {
    /// @param nums input array
    /// @param k    number of subarrays
    /// @return     minimized largest subarray sum
    pub fn split_array(nums: Vec<i32>, k: i32) -> i32 {
        let can_split = |cap: i64| -> bool {
            let mut parts = 1i64;
            let mut running = 0i64;
            for &num in &nums {
                if running + num as i64 > cap { parts += 1; running = 0; }
                running += num as i64;
            }
            parts <= k as i64
        };

        let mut lo = *nums.iter().max().unwrap() as i64;
        let mut hi: i64 = nums.iter().map(|&v| v as i64).sum();

        while lo < hi {
            let mid = lo + (hi - lo) / 2;
            if can_split(mid) { hi = mid; } else { lo = mid + 1; }
        }
        lo as i32
    }
}
```

## Dry run

**Input:** `nums = [7,2,5,10,8]`, `k = 2`.

```
lo = max = 10, hi = sum = 32
mid = 21: canSplit(21)? 7+2+5=14, +10=24 > 21 -> new part.  [14][10+8=18] = 2 parts ≤ 2 ✓ -> hi=21
mid = 15: 7+2+5=14, +10>15 -> [14][10], 8 -> 3 parts > 2 ✗ -> lo=16
mid = 18: 7+2+5=14, +10>18 -> [14][10], 8: 10+8=18 ≤ 18 -> [14][18] = 2 parts ✓ -> hi=18
mid = 17: 14, +10>17 -> [14][10], 8 -> 3 parts ✗ -> lo=18
lo == hi == 18.  Output: 18 ✓
```

The monotone predicate is the crux: cap 18 works (2 parts), cap 17 doesn't (3 parts) — the boundary is exactly the answer. The greedy pack computes the *minimum* part count for each cap, and the search zooms to the smallest feasible cap.

## Complexity

**Time.** O(n) feasibility × O(log Σ) iterations:

$$
T(n) = O(n \log \Sigma)
$$

**Space.** Scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Capacity To Ship Packages** ([1.2](capacity-to-ship-packages.md)) — the same "minimize the max with a feasible-cap binary search", with days instead of parts.
- **Koko Eating Bananas** ([1.3](koko-eating-bananas.md)) — the same shape, eating speed instead of cap.
- **Interview follow-up:** "Why is the greedy part count the minimum?" If a cap is feasible with some split, the greedy (extend each part maximally) uses *at most* as many parts — any split's parts can be merged into greedier ones without exceeding the cap. So `greedyParts ≤ any split's parts`; testing `greedyParts ≤ k` decides feasibility exactly.
