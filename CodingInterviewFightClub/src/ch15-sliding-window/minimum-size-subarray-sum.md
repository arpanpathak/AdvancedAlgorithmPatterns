# 15.5 Minimum Size Subarray Sum

> **Source:** [`src/main/kotlin/sliding_window/MinimumSizeSubarraySum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/MinimumSizeSubarraySum.kt)
> **Pattern:** shrink-until-valid · **Core page**

## The Problem

Given `nums` (positive integers) and a `target`, return the **minimum length** of a contiguous subarray whose sum is ≥ `target`, or `0` if none.

- Constraints: $1 \le n \le 10^5$; all values positive.

## Examples

```
Input:  target = 7, nums = [2,3,1,2,4,3]   -> Output: 2   ([4,3])
Input:  target = 11, nums = [1,1,1,1,1,1]  -> Output: 0
```

## Intuition — expand until ≥ target, then shrink as much as possible

The condition is monotone: once a window's sum ≥ target, *extending* it keeps it ≥ target, and *shrinking* it may drop below. That monotonicity is exactly what the window template ([15.0](pattern-primer.md)) needs:

```
for right in nums.indices:
    windowSum += nums[right]                 # expand
    while windowSum >= target:               # valid: try to shrink
        minLength = min(minLength, right - left + 1)
        windowSum -= nums[left]; left++      # shrink — this may make it invalid
```

**Why is `while` correct (and necessary)?** A window can be valid for *several* consecutive shrinks — each removal may keep the sum ≥ target. The while loop records every valid length as it shrinks, so the minimum is found. (Contrast [15.3](longest-repeating-character-replacement.md), where one shrink per step sufficed — different validity geometry.)

**Why must all elements be positive?** The monotonicity "shrinking decreases the sum" requires positive values. With negatives, a shrink could *increase* the sum and the while-loop reasoning breaks. (The problem guarantees positivity — that's a precondition, not an implementation detail.)

**The `Int.MAX_VALUE` sentinel:** if no window ever reaches the target, `minLength` stays `MAX_VALUE` → return `0`. The classic "did we ever find one?" sentinel.

## Approach 1 — Prefix sums + binary search (O(n log n))

Build prefix sums, then for each start binary-search the first end with sum ≥ target: correct, but the window is $O(n)$ — simpler and better.

## Approach 2 — Shrink-until-valid window (the repo's version, optimal)

```kotlin
class MinimumSizeSubarraySum {
    /**
     * @param target minimum sum required
     * @param nums   positive integers
     * @return       minimum window length with sum >= target, or 0
     */
    fun minSubArrayLen(target: Int, nums: IntArray): Int {
        var (windowStart, windowSum) = 0 to 0
        var minLength = Int.MAX_VALUE

        for (i in nums.indices) {
            windowSum += nums[i]                       // expand

            // Shrink window
            while (windowSum >= target) {              // valid: record and shrink
                minLength = minOf(minLength, i - windowStart + 1)
                windowSum -= nums[windowStart++]       // shrink
            }
        }
        return if (minLength == Int.MAX_VALUE) 0 else minLength
    }
}
```

```java
public class MinimumSizeSubarraySum {
    /**
     * @param target minimum sum required
     * @param nums   positive integers
     * @return       minimum window length with sum >= target, or 0
     */
    public int minSubArrayLen(int target, int[] nums) {
        int start = 0, sum = 0, minLen = Integer.MAX_VALUE;

        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];                            // expand

            while (sum >= target) {                    // valid: record and shrink
                minLen = Math.min(minLen, i - start + 1);
                sum -= nums[start++];                  // shrink
            }
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }
}
```

```cpp
#include <vector>

class MinimumSizeSubarraySum {
public:
    /**
     * @param target minimum sum required
     * @param nums   positive integers
     * @return       minimum window length with sum >= target, or 0
     */
    int minSubArrayLen(int target, std::vector<int>& nums) {
        int start = 0, sum = 0, minLen = INT_MAX;

        for (int i = 0; i < (int)nums.size(); i++) {
            sum += nums[i];                            // expand

            while (sum >= target) {                    // valid: record and shrink
                minLen = std::min(minLen, i - start + 1);
                sum -= nums[start++];                  // shrink
            }
        }
        return minLen == INT_MAX ? 0 : minLen;
    }
};
```

```python
def min_sub_array_len(target: int, nums: list[int]) -> int:
    """
    @param target: minimum sum required
    @param nums:   positive integers
    @return:       minimum window length with sum >= target, or 0
    """
    start = 0
    total = 0
    min_len = float("inf")

    for i, num in enumerate(nums):
        total += num                            # expand

        while total >= target:                  # valid: record and shrink
            min_len = min(min_len, i - start + 1)
            total -= nums[start]                # shrink
            start += 1
    return min_len if min_len != float("inf") else 0
```

```rust
impl Solution {
    /// @param target minimum sum required
    /// @param nums   positive integers
    /// @return       minimum window length with sum >= target, or 0
    pub fn min_sub_array_len(target: i32, nums: Vec<i32>) -> i32 {
        let mut start = 0usize;
        let mut total = 0i32;
        let mut min_len = usize::MAX;

        for i in 0..nums.len() {
            total += nums[i];                   // expand

            while total >= target {             // valid: record and shrink
                min_len = min_len.min(i - start + 1);
                total -= nums[start];
                start += 1;                     // shrink
            }
        }
        if min_len == usize::MAX { 0 } else { min_len as i32 }
    }
}
```

## Dry run

**Input:** `target = 7`, `nums = [2,3,1,2,4,3]`.

```
start=0, sum=0, minLen=MAX

i=0 (2): sum=2.    < 7.
i=1 (3): sum=5.    < 7.
i=2 (1): sum=6.    < 7.
i=3 (2): sum=8.    >= 7 -> record len 4.  sum-=2, start=1 (sum=6).  stop.
i=4 (4): sum=10.   >= 7 -> record len 4 (4-1+1).  sum-=3, start=2 (sum=7).  still >= 7:
                     record len 3 (4-2+1).  sum-=1, start=3 (sum=6).  stop.
i=5 (3): sum=9.    >= 7 -> record len 3 (5-3+1).  sum-=2, start=4 (sum=7).  still >= 7:
                     record len 2 (5-4+1).  sum-=4, start=5 (sum=3).  stop.

minLen = 2 ✓   ([4,3])
```

The while-loop is essential here: at i=4, the window `[2,1,2,4]` (sum 10) shrinks *twice* — recording lengths 4 and 3 — before dropping below target. A single `if`-shrink would have missed the length-3 window `[1,2,4]`.

## Complexity

**Time.** Each element enters and leaves once:

$$
T(n) = O(n)
$$

**Space.** Two variables:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Maximum Erasure Value** (`src/main/kotlin/sliding_window/`) — the same shrink-until-valid window with a distinctness constraint.
- **Minimum Window Substring** ([15.2](minimum-window-substring.md)) — validity by *coverage* instead of sum; the `formed` counter replaces the running sum.
- **Interview follow-up:** "Why does positivity matter?" The while-loop's correctness rests on "shrinking can only decrease the sum" — with negative elements, a shrink could raise the sum past target and the loop would terminate too early. Positive inputs are what make the window's validity monotone, which is the precondition of the whole template.
