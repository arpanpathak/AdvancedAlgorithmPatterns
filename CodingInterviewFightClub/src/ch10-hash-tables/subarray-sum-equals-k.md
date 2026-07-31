# 10.8 Subarray Sum Equals K

> **Source:** [`src/main/kotlin/array/prefixsum/SubArraySumEqualsToK.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/prefixsum/SubArraySumEqualsToK.kt)
> **Pattern:** prefix sums + frequency map · **Core page**

## The Problem

Given `nums` (can be negative!) and `k`, count the number of **contiguous subarrays** whose sum equals `k`.

- Constraints: $1 \le n \le 2 \times 10^4$; values fit in `Int`.

## Examples

```
Input:  nums = [1,1,1], k = 2   -> Output: 2   ([1,1] at 0..1 and 1..2)
Input:  nums = [1,2,3], k = 3   -> Output: 2   ([1,2] and [3])
Input:  nums = [1,-1,0], k = 0  -> Output: 3   ([1,-1], [0], [1,-1,0])
```

## Intuition — a subarray's sum is a *difference of prefix sums*

`sum(nums[i..j]) = prefix[j] - prefix[i-1]`. So "subarrays ending at `j` with sum `k`" ⟺ "prefix sums before `j` equal to `prefix[j] - k`". One pass with a **frequency map of prefix sums seen so far**:

```
preSumFreq = {0: 1}          # the empty prefix: subarrays starting at index 0
for num in nums:
    sum += num
    count += preSumFreq[sum - k]     # how many earlier prefixes complete this subarray?
    preSumFreq[sum]++                # this prefix is now available to later endings
```

**Why does `{0: 1}` matter?** A subarray starting at index 0 has no earlier prefix — the "prefix before it" is the empty one (sum 0). Seeding the map makes `sum == k` count as a valid subarray.

**Why a map and not a sliding window?** The sliding-window template ([15.0](../ch15-sliding-window/pattern-primer.md)) requires *monotone* window sums — negatives break the shrink-until-valid logic. Prefix-sum counting handles negatives by construction: it never relies on ordering, only on "earlier prefix values".

**Order matters in the update:** count with `sum - k` *before* recording `sum` — otherwise a zero-length window (same index) would self-match. The repo's sequence (`count += ...` then `preSumFreq[sum]++`) is exactly that discipline.

## Approach 1 — All subarrays (O(n^2))

Double loop summing every window: correct, quadratic — and the baseline this map eliminates.

## Approach 2 — Prefix-sum frequency map (the repo's version, optimal)

```kotlin
class SubArraySumEqualsToK {
    /**
     * @param nums input array (may contain negatives)
     * @param k    target subarray sum
     * @return     number of contiguous subarrays with sum == k
     */
    fun subarraySum(nums: IntArray, k: Int): Int {
        val preSumFreq = mutableMapOf<Int, Int>()
        preSumFreq[0] = 1          // the empty prefix: subarrays starting at index 0

        var count = 0
        var sum = 0
        for (num in nums) {
            sum += num
            count += preSumFreq[sum - k] ?: 0     // earlier prefixes completing a k-sum window
            preSumFreq[sum] = (preSumFreq[sum] ?: 0) + 1
        }
        return count
    }
}
```

```java
import java.util.*;

public class SubarraySumEqualsK {
    /**
     * @param nums input array (may contain negatives)
     * @param k    target subarray sum
     * @return     number of contiguous subarrays with sum == k
     */
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        freq.put(0, 1);                       // the empty prefix

        int count = 0, sum = 0;
        for (int num : nums) {
            sum += num;
            count += freq.getOrDefault(sum - k, 0);   // earlier prefixes completing a k-sum window
            freq.merge(sum, 1, Integer::sum);
        }
        return count;
    }
}
```

```cpp
#include <unordered_map>
#include <vector>

class SubarraySumEqualsK {
public:
    /**
     * @param nums input array (may contain negatives)
     * @param k    target subarray sum
     * @return     number of contiguous subarrays with sum == k
     */
    int subarraySum(std::vector<int>& nums, int k) {
        std::unordered_map<int, int> freq;
        freq[0] = 1;                          // the empty prefix

        int count = 0, sum = 0;
        for (int num : nums) {
            sum += num;
            count += freq[sum - k];           // earlier prefixes completing a k-sum window
            freq[sum]++;
        }
        return count;
    }
};
```

```python
def subarray_sum(nums: list[int], k: int) -> int:
    """
    @param nums: input array (may contain negatives)
    @param k:    target subarray sum
    @return:     number of contiguous subarrays with sum == k
    """
    freq = {0: 1}                      # the empty prefix
    count = 0
    total = 0

    for num in nums:
        total += num
        count += freq.get(total - k, 0)   # earlier prefixes completing a k-sum window
        freq[total] = freq.get(total, 0) + 1
    return count
```

```rust
use std::collections::HashMap;

impl Solution {
    /// @param nums input array (may contain negatives)
    /// @param k    target subarray sum
    /// @return     number of contiguous subarrays with sum == k
    pub fn subarray_sum(nums: Vec<i32>, k: i32) -> i32 {
        let mut freq: HashMap<i32, i32> = HashMap::new();
        freq.insert(0, 1);                     // the empty prefix

        let (mut count, mut total) = (0, 0);
        for num in nums {
            total += num;
            count += freq.get(&(total - k)).copied().unwrap_or(0);   // completing windows
            *freq.entry(total).or_insert(0) += 1;
        }
        count
    }
}
```

## Dry run

**Input:** `nums = [1,1,1]`, `k = 2`.

```
freq = {0:1}, total = 0, count = 0

num=1: total=1.  count += freq[1-2=-1]? 0.  freq[1]=1 -> {0:1, 1:1}
num=1: total=2.  count += freq[0]=1 -> count=1.  freq[2]=1
num=1: total=3.  count += freq[1]=1 -> count=2.  freq[3]=1

Output: 2 ✓   ([1,1] ending at index 1 via prefix 0; [1,1] ending at index 2 via prefix 1)
```

The second window's count comes from the *first* `num=1`'s recorded prefix — the map's "earlier prefixes" are exactly the possible subarray starts. The negative-friendly `[1,-1,0], k=0` case works the same way: `total` revisits 0, and each revisit of `freq[0]` counts a zero-sum window.

## Complexity

**Time.** One pass with O(1) map ops:

$$
T(n) = O(n)
$$

**Space.** The prefix-frequency map:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Path Sum III** ([5.9](../ch05-trees/path-sum-iii.md)) — the exact same prefix-counting on a *tree*, with DFS carry/restore replacing the single pass.
- **Continuous Subarray Sum / Subarray Sums Divisible By K** (`array/prefixsum/`) — the same map keyed by `sum % k` for divisibility questions.
- **Contiguous Array** (`array/prefixsum/ContiguousArray.kt`) — prefix sums with +1/-1 encoding; the map stores *first* occurrence instead of counts.
- **Interview follow-up:** "Why can't the sliding window handle negatives?" Window shrink relies on monotonicity — removing elements must decrease the sum. Negatives break that, so the shrink-until-valid loop can't terminate correctly. Prefix-sum counting makes no monotonic assumption: it only asks "did this prefix value appear earlier?", which is order-agnostic and negative-safe.
