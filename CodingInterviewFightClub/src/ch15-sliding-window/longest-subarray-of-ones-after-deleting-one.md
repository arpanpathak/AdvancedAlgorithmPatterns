# 15.10 Longest Subarray Of 1s After Deleting One Element

> **Source:** [`src/main/kotlin/sliding_window/LongestSubArraysOfOneAfterDeletingOneElement.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/LongestSubArraysOfOneAfterDeletingOneElement.kt)
> **Pattern:** zero-count window with one deletion · **Core page**

## The Problem

The longest subarray of `1`s after deleting **exactly one** element.

- Constraints: $1 \le n \le 10^5$; binary array.

## Examples

```
Input:  nums = [1,1,0,1]        -> Output: 3   (delete the 0: [1,1,1])
Input:  nums = [0,1,1,1,0,1,1,0,1] -> Output: 5   (delete a 0, keep the longest run)
Input:  nums = [1,1,1]          -> Output: 2   (must delete one!)
```

## Intuition — a window with at most one `0`, and the answer is `window − 1`

The [15.7](max-consecutive-ones-iii.md) window with `k = 1` zeros allowed — but the answer *subtracts the deleted element*:

```kotlin
var (zeroCount, longestWindow, windowStart) = Triple(0, 0, 0)

for (i in indices) {
    zeroCount += (1 - nums[i])              // count zeros

    while (zeroCount > 1) {                 // shrink to at most one zero
        zeroCount -= (1 - nums[windowStart++])
    }

    longestWindow = maxOf(longestWindow, i - windowStart)   // window length, minus the 0
}
return longestWindow
```

**Why `i - windowStart` instead of `i - windowStart + 1`?** The window contains exactly one `0` (the deleted element) in the interesting case — its length `+1` minus the deleted 0 is `i - windowStart`. For an all-ones array, the window is all 1s but we *must* delete one → `i - windowStart` still correct (`[1,1,1]`: window 3, answer 2).

**Why count zeros via `1 - nums[i]`?** The [15.7](max-consecutive-ones-iii.md) counting idiom: `1 - bit` is 1 for 0, 0 for 1 — the zero-counter without a branch. The shrink loop evicts while zeroCount exceeds the deletion budget.

## Approach 1 — Split on zeros, combine adjacent runs (O(n))

Find all-zero gaps, merge the adjacent 1-runs: correct, fiddly edge cases.

## Approach 2 — Zero-count window (the repo's version, optimal)

```kotlin
class LongestSubArraysOfOneAfterDeletingOneElement {
    /**
     * @param nums binary array
     * @return     longest subarray of 1s after deleting one element
     */
    fun longestSubarray(nums: IntArray): Int {
        var (zeroCount, longestWindow, windowStart) = Triple(0, 0, 0)

        for (i in 0 until nums.size) {
            zeroCount += (1 - nums[i])

            // Shrink while more than one zero: only one can be deleted
            while (zeroCount > 1) {
                zeroCount -= (1 - nums[windowStart++])
            }

            longestWindow = maxOf(longestWindow, i - windowStart)
        }
        return longestWindow
    }
}
```

```java
public class LongestSubarrayOfOnesAfterDeletingOneElement {
    /**
     * @param nums binary array
     * @return     longest subarray of 1s after deleting one element
     */
    public int longestSubarray(int[] nums) {
        int zeros = 0, start = 0, best = 0;

        for (int i = 0; i < nums.length; i++) {
            zeros += 1 - nums[i];

            while (zeros > 1) {
                zeros -= 1 - nums[start++];
            }

            best = Math.max(best, i - start);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class LongestSubarrayOfOnesAfterDeletingOneElement {
public:
    /**
     * @param nums binary array
     * @return     longest subarray of 1s after deleting one element
     */
    int longestSubarray(std::vector<int>& nums) {
        int zeros = 0, start = 0, best = 0;

        for (int i = 0; i < (int)nums.size(); i++) {
            zeros += 1 - nums[i];

            while (zeros > 1) {
                zeros -= 1 - nums[start++];
            }

            best = std::max(best, i - start);
        }
        return best;
    }
};
```

```python
def longest_subarray(nums: list[int]) -> int:
    """
    @param nums: binary array
    @return:     longest subarray of 1s after deleting one element
    """
    zeros = 0
    start = 0
    best = 0

    for i, num in enumerate(nums):
        zeros += 1 - num

        while zeros > 1:
            zeros -= 1 - nums[start]
            start += 1

        best = max(best, i - start)
    return best
```

```rust
impl Solution {
    /// @param nums binary array
    /// @return     longest subarray of 1s after deleting one element
    pub fn longest_subarray(nums: Vec<i32>) -> i32 {
        let mut zeros = 0;
        let mut start = 0;
        let mut best = 0;

        for (i, &num) in nums.iter().enumerate() {
            zeros += 1 - num;

            while zeros > 1 {
                zeros -= 1 - nums[start];
                start += 1;
            }

            best = best.max((i - start) as i32);
        }
        best
    }
}
```

## Dry run

**Input:** `nums = [0,1,1,1,0,1,1,0,1]`.

```
i=0 (0): zeros=1.  best=0.
i=1 (1): zeros=1.  best=1.
i=2 (1): best=2.
i=3 (1): best=3.        (window [0,1,1,1] -> 3 ones after deleting the 0)
i=4 (0): zeros=2 -> shrink: evict nums[0]=0, zeros=1, start=1.  best=3.  window [1,1,1,0]
i=5 (1): best=4.        (window [1,1,1,0,1] -> 4 ones, delete the 0)
i=6 (1): best=5.        (window [1,1,1,0,1,1] -> 5 ones ✓)
i=7 (0): zeros=2 -> shrink: evict nums[1]=1, zeros=1, start=2.  best=5.
i=8 (1): best=5.

Output: 5 ✓
```

The `i - windowStart` accounting: at i=6, windowStart=1, so `6 - 1 = 5` — the window `[1,1,1,0,1,1]` has length 6, one 0 (deleted), five 1s. All-ones input `[1,1,1]`: window never shrinks, `i - start` runs 0,1,2 → 2 ✓ (the forced deletion). Single-0 input `[1,1,0,1]`: best 3 ✓.

## Complexity

**Time.** Each element in/out once:

$$
T(n) = O(n)
$$

**Space.** Scalars:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Max Consecutive Ones III** ([15.7](max-consecutive-ones-iii.md)) — the general `k` zeros; this page is `k=1` with the −1 answer.
- **Maximum Erasure Value** ([15.9](maximum-erasure-value.md)) — the all-distinct window twin.
- **Interview follow-up:** "Why `i - windowStart` instead of `+1`?" The window's `1s` count is length − zeroCount, and zeroCount is exactly 1 in the maximal case — so the answer is `length − 1 = i − windowStart`. For all-ones windows the same formula handles the mandatory deletion. The −1 is the problem's "delete exactly one" baked into the length.
