# 2.38 Continuous Subarray Sum

> **Source**: [`src/main/kotlin/array/prefixsum/ContinuousSubarraySum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/ContinuousSubarraySum.kt)
> **Pattern**: prefix-mod repetition · **Core page**

## The Problem

A subarray of length ≥ 2 whose sum is a multiple of `k`.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  nums = [23,2,4,6,7], k = 6   -> Output: true  ([2,4])
Input:  nums = [23,2,6,4,7], k = 6   -> Output: true  ([23,2,6,4,7])
```

## Intuition — two equal prefix-sums (mod k) bracket a multiple-of-k window

The [10.24](../ch10-hash-tables/contiguous-array.md) repeated-prefix idea: `sum[i] ≡ sum[j] (mod k)` means `nums[i+1..j]` sums to a multiple of k. Keep the *earliest* index per remainder, requiring length ≥ 2:

```kotlin
val sumsSet: HashSet<Int> = HashSet(nums.size)
var sum = 0
var prevSum: Int

for (num in nums) {
    prevSum = sum
    sum = (sum + num) % k

    if (sumsSet.contains(sum)) return true
    sumsSet.add(prevSum)
}
return false
```

**Why the offset set trick?** Adding `prevSum` (one step behind) enforces length ≥ 2 — a remainder repeated by an adjacent pair (e.g. a single k-multiple element) doesn't falsely trigger. The [10.8](../ch10-hash-tables/subarray-sum-equals-k.md) prefix-frequency family.

## Approach 1 — Earliest-remainder map (the canonical)

Keep the first index per remainder; check `i - first[rem] >= 2`. Simple, explicit.

## Approach 2 — Offset set (the repo's version, optimal)

```kotlin
class ContinuousSubarraySum {
    /**
     * @param nums input array
     * @param k    modulus
     * @return     true iff a length >= 2 multiple-of-k subarray exists
     */
    fun checkSubarraySum(nums: IntArray, k: Int): Boolean {
        val sumsSet: HashSet<Int> = HashSet(nums.size)
        var sum = 0
        var prevSum: Int

        for (num in nums) {
            prevSum = sum
            sum = (sum + num) % k

            if (sumsSet.contains(sum)) return true
            sumsSet.add(prevSum)
        }
        return false
    }
}
```

```java
import java.util.*;

public class ContinuousSubarraySum {
    /**
     * @param nums input array
     * @param k    modulus
     * @return     true iff a length >= 2 multiple-of-k subarray exists
     */
    public boolean checkSubarraySum(int[] nums, int k) {
        Map<Integer, Integer> first = new HashMap<>();
        first.put(0, -1);
        int sum = 0;

        for (int i = 0; i < nums.length; i++) {
            sum = (sum + nums[i]) % k;

            if (first.containsKey(sum)) {
                if (i - first.get(sum) >= 2) return true;
            } else {
                first.put(sum, i);
            }
        }
        return false;
    }
}
```

```cpp
#include <vector>
#include <unordered_set>

class ContinuousSubarraySum {
public:
    /**
     * @param nums input array
     * @param k    modulus
     * @return     true iff a length >= 2 multiple-of-k subarray exists
     */
    bool checkSubarraySum(std::vector<int>& nums, int k) {
        std::unordered_set<int> seen;
        int sum = 0, prev = 0;

        for (int num : nums) {
            prev = sum;
            sum = (sum + num) % k;

            if (seen.count(sum)) return true;
            seen.insert(prev);
        }
        return false;
    }
};
```

```python
def check_subarray_sum(nums: list[int], k: int) -> bool:
    """
    @param nums: input array
    @param k:    modulus
    @return:     true iff a length >= 2 multiple-of-k subarray exists
    """
    first = {0: -1}
    total = 0

    for i, num in enumerate(nums):
        total = (total + num) % k

        if total in first:
            if i - first[total] >= 2:
                return True
        else:
            first[total] = i

    return False
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums input array
    /// @param k    modulus
    /// @return     true iff a length >= 2 multiple-of-k subarray exists
    pub fn check_subarray_sum(nums: Vec<i32>, k: i32) -> bool {
        let mut first: HashMap<i32, i32> = HashMap::new();
        first.insert(0, -1);
        let mut sum = 0;

        for (i, &num) in nums.iter().enumerate() {
            sum = (sum + num).rem_euclid(k);

            if let Some(&j) = first.get(&sum) {
                if i as i32 - j >= 2 { return true; }
            } else {
                first.insert(sum, i as i32);
            }
        }
        false
    }
}
```

## Dry run

**Input:** `nums = [23,2,4,6,7], k = 6`.

```
prefix mods: 23%6=5.  (5+2)=7%6=1.  (1+4)=5 — repeated!  indices 0 and 2, length 3 >= 2 -> true ✓
```

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** The set/map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Subarray Sum Equals K** ([10.8](../ch10-hash-tables/subarray-sum-equals-k.md)) — the exact-sum cousin.
- **Interview follow-up:** "Why the length ≥ 2 requirement matters?" A single element that's itself a multiple of k would repeat a remainder trivially — the offset/earliest-index machinery excludes it.
