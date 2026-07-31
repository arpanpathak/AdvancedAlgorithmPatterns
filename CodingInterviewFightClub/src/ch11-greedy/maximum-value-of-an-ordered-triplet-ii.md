# 11.27 Maximum Value Of An Ordered Triplet II

> **Source**: [`src/main/kotlin/greedy/MaximumValueOfAnOrderedTriplet_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/greedy/MaximumValueOfAnOrderedTriplet_II.kt)
> **Pattern**: running max + max-difference · **Core page**

## The Problem

Max `(nums[i] - nums[j]) * nums[k]` with `i < j < k`.

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  nums = [12,6,1,2,7]   -> Output: 77   ((12-6)*7? no: 12-1=11 *7 = 77)
Input:  nums = [1,10,3,4,19]  -> Output: 133  ((10-3)*19 = 133)
```

## Intuition — maintain the max value and the max difference as k advances

For each `k`, the best product is `maxDifference * nums[k]`, where `maxDifference = max(nums[i] - nums[j])` over `i < j < k`. Both roll forward:

```kotlin
var maxTripletValue: Long = 0
var maxValueSoFar: Long = 0       // max nums[i] seen
var maxDifference: Long = 0       // max (nums[i] - nums[j]) seen

for (k in 0 until n) {
    maxTripletValue = maxOf(maxTripletValue, maxDifference * nums[k].toLong())

    maxDifference = maxOf(maxDifference, maxValueSoFar - nums[k].toLong())
    maxValueSoFar = maxOf(maxValueSoFar, nums[k].toLong())
}
return maxTripletValue
```

**Why the update order?** At index k: the product uses the difference from *earlier* pairs (before k becomes a j), then k updates both rolling values for future k's. The [11.16](best-time-to-buy-and-sell-stock.md) running-extreme family, stacked twice.

**Why `Long`?** `(nums[i] - nums[j]) * nums[k]` can exceed `Int` — the Long casts are the [11.0](pattern-primer.md) hygiene.

## Approach 1 — Running max/difference (the repo's version, optimal)

```kotlin
class MaximumValueOfAnOrderedTriplet_II {
    /**
     * @param nums input array
     * @return     max (nums[i] - nums[j]) * nums[k]
     */
    fun maximumTripletValue(nums: IntArray): Long {
        val n = nums.size
        var maxTripletValue: Long = 0
        var maxValueSoFar: Long = 0
        var maxDifference: Long = 0

        for (k in 0 until n) {
            maxTripletValue = maxOf(maxTripletValue, maxDifference * nums[k].toLong())

            maxDifference = maxOf(maxDifference, maxValueSoFar - nums[k].toLong())
            maxValueSoFar = maxOf(maxValueSoFar, nums[k].toLong())
        }
        return maxTripletValue
    }
}
```

```java
public class MaximumValueOfAnOrderedTripletII {
    /**
     * @param nums input array
     * @return     max (nums[i] - nums[j]) * nums[k]
     */
    public long maximumTripletValue(int[] nums) {
        long best = 0, maxValue = 0, maxDiff = 0;

        for (int k : nums) {
            best = Math.max(best, maxDiff * k);

            maxDiff = Math.max(maxDiff, maxValue - k);
            maxValue = Math.max(maxValue, k);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class MaximumValueOfAnOrderedTripletII {
public:
    /**
     * @param nums input array
     * @return     max (nums[i] - nums[j]) * nums[k]
     */
    long long maximumTripletValue(std::vector<int>& nums) {
        long long best = 0, maxValue = 0, maxDiff = 0;

        for (int k : nums) {
            best = std::max(best, maxDiff * k);

            maxDiff = std::max(maxDiff, maxValue - k);
            maxValue = std::max(maxValue, (long long)k);
        }
        return best;
    }
};
```

```python
def maximum_triplet_value(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     max (nums[i] - nums[j]) * nums[k]
    """
    best = max_value = max_diff = 0

    for k in nums:
        best = max(best, max_diff * k)

        max_diff = max(max_diff, max_value - k)
        max_value = max(max_value, k)

    return best
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     max (nums[i] - nums[j]) * nums[k]
    pub fn maximum_triplet_value(nums: Vec<i32>) -> i64 {
        let (mut best, mut max_value, mut max_diff) = (0i64, 0i64, 0i64);

        for k in nums {
            best = best.max(max_diff * k as i64);

            max_diff = max_diff.max(max_value - k as i64);
            max_value = max_value.max(k as i64);
        }
        best
    }
}
```

## Dry run

**Input:** `nums = [12,6,1,2,7]`.

```
k=12: best 0.  maxDiff = max(0, 0-12) = 0.  maxValue = 12.
k=6:  best 0.  maxDiff = max(0, 12-6) = 6.  maxValue = 12.
k=1:  best = max(0, 6*1) = 6.  maxDiff = max(6, 12-1) = 11.  maxValue = 12.
k=2:  best = max(6, 11*2) = 22.  maxDiff = max(11, 12-2) = 11.
k=7:  best = max(22, 11*7) = 77.  maxDiff = max(11, 12-7) = 11.

Output: 77 ✓  ((12-1)*7)
```

The two rolling values encode the *best pair* `(i, j)` for every future k: `maxDifference` is the best `nums[i] - nums[j]` so far. Each k multiplies it, then contributes itself as a potential new j (via the diff update) and i (via the max update).

## Complexity

**Time.** One pass:

$$
T(n) = O(n)
$$

**Space.** Three scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Best Time To Buy And Sell Stock** ([11.16](best-time-to-buy-and-sell-stock.md)) — the running-extreme ancestor.
- **Interview follow-up:** "Why must the product precede the updates?" At index k, k is the *multiplier* — using it as a j or i would break `i < j < k`. The update order enforces the index ordering.
