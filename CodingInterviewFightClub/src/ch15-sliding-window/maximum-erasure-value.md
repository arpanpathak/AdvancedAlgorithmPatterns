# 15.9 Maximum Erasure Value

> **Source:** [`src/main/kotlin/sliding_window/MaximumErasureValue.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/MaximumErasureValue.kt)
> **Pattern:** all-unique window with a sum · **Core page**

## The Problem

Given `nums`, find the **maximum sum** of a subarray with **all distinct** elements.

- Constraints: $1 \le n \le 10^5$; values fit in `Int`.

## Examples

```
Input:  nums = [4,2,4,5,6]   -> Output: 17   (the subarray [2,4,5,6])
Input:  nums = [5,2,1,2,5,2,1,2,5] -> Output: 8   ([5,2,1] or [1,2,5])
```

## Intuition — the [15.1](longest-substring-without-repeating-characters.md) window, tracking the *sum* not the length

"All distinct" is the [15.1](longest-substring-without-repeating-characters.md) constraint; the twist is maximizing **sum** instead of length. Same machinery — a `Set` and a left pointer — plus a running `windowSum`:

```
for num in nums:
    while set.contains(num):          # shrink until the duplicate is out
        windowSum -= nums[windowStart]
        set.remove(nums[windowStart++])
    windowSum += num
    maxSum = max(maxSum, windowSum)
    set.add(num)
return maxSum
```

**Why the `while`-shrink instead of `if`?** The duplicate may be anywhere in the window — `[2,4,5,4]` adding the second `4` needs to evict everything *through* the first `4`. The while-loop slides `windowStart` forward, subtracting each evicted value from the running sum — the [15.5](minimum-size-subarray-sum.md) shrink discipline with sum-tracking.

**Why does the set + sum stay consistent?** Every add/remove updates both structures together — the set is the distinctness constraint, `windowSum` its numeric echo. The window is always valid (all-distinct) after the shrink, so `maxSum` is taken over valid windows only.

## Approach 1 — Check all subarrays (O(n²))

Every window, verify distinctness: correct, quadratic.

## Approach 2 — Shrink-on-duplicate window (the repo's version, optimal)

```kotlin
class MaximumErasureValue {
    /**
     * @param nums input array
     * @return     max sum of a subarray with all distinct elements
     */
    fun maximumUniqueSubarray(nums: IntArray): Int {
        val set = mutableSetOf<Int>()
        var (windowSum, windowStart, maxSum) = listOf(0, 0, 0)

        nums.forEach { num ->
            while (set.contains(num)) {           // shrink past the duplicate
                windowSum -= nums[windowStart]
                set.remove(nums[windowStart++])
            }
            windowSum += num
            maxSum = maxOf(maxSum, windowSum)
            set.add(num)
        }
        return maxSum
    }
}
```

```java
import java.util.*;

public class MaximumErasureValue {
    /**
     * @param nums input array
     * @return     max sum of a subarray with all distinct elements
     */
    public int maximumUniqueSubarray(int[] nums) {
        Set<Integer> set = new HashSet<>();
        int sum = 0, start = 0, best = 0;

        for (int num : nums) {
            while (set.contains(num)) {            // shrink past the duplicate
                sum -= nums[start];
                set.remove(nums[start++]);
            }
            sum += num;
            best = Math.max(best, sum);
            set.add(num);
        }
        return best;
    }
}
```

```cpp
#include <unordered_set>
#include <vector>

class MaximumErasureValue {
public:
    /**
     * @param nums input array
     * @return     max sum of a subarray with all distinct elements
     */
    int maximumUniqueSubarray(std::vector<int>& nums) {
        std::unordered_set<int> set;
        int sum = 0, start = 0, best = 0;

        for (int num : nums) {
            while (set.count(num)) {               // shrink past the duplicate
                sum -= nums[start];
                set.erase(nums[start++]);
            }
            sum += num;
            best = std::max(best, sum);
            set.insert(num);
        }
        return best;
    }
};
```

```python
def maximum_unique_subarray(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     max sum of a subarray with all distinct elements
    """
    seen = set()
    total = best = 0
    start = 0

    for num in nums:
        while num in seen:               # shrink past the duplicate
            total -= nums[start]
            seen.remove(nums[start])
            start += 1
        total += num
        best = max(best, total)
        seen.add(num)

    return best
```

```rust
use std::collections::HashSet;

impl Solution {
    /// @param nums input array
    /// @return     max sum of a subarray with all distinct elements
    pub fn maximum_unique_subarray(nums: Vec<i32>) -> i32 {
        let mut seen = HashSet::new();
        let (mut total, mut start, mut best) = (0, 0, 0);

        for &num in &nums {
            while seen.contains(&num) {            // shrink past the duplicate
                total -= nums[start];
                seen.remove(&nums[start]);
                start += 1;
            }
            total += num;
            best = best.max(total);
            seen.insert(num);
        }
        best
    }
}
```

## Dry run

**Input:** `nums = [4,2,4,5,6]`.

```
set={}, sum=0, start=0, best=0
num=4: not in set.  sum=4.  best=4.  set={4}
num=2: sum=6.  best=6.  set={4,2}
num=4: IN set -> shrink: sum-=nums[0]=4 -> 2.  set.remove(4), start=1.  (4 now absent)
       sum-=nums[1]=2 -> 0.  set.remove(2), start=2.  set empty.
       sum+=4 -> 4.  best=6.  set={4}
num=5: sum=9.  best=9.  set={4,5}
num=6: sum=15.  best=15.  set={4,5,6}

Output: 15? — WRONG TRACE, the window is [4,5,6] = 15.  The correct answer is 17 ([2,4,5,6]).
```

**Correction — the shrink must stop at the duplicate's *first* occurrence:**

```
The second 4 at index 2: shrink only until 4 is out -> evict index 0 (4): sum = 6-4 = 2, start=1, set={2}.
                       the while loop: set.contains(4)? NO (removed) -> stop.
sum += 4 -> 6.  best=6 (window [2,4]).  set={2,4}
num=5: sum=11.  best=11.  set={2,4,5}
num=6: sum=17.  best=17.  set={2,4,5,6}

Output: 17 ✓  (the window [2,4,5,6])
```

The subtlety: the `while` evicts **only until the duplicate is gone** — after removing the first `4`, `4` is out of the set, so the loop stops and the window becomes `[2,4,5,6]`. Evicting the second `4` too (my wrong trace) over-shrinks. The set's `contains` check after each removal is what stops exactly at the right spot.

## Complexity

**Time.** Each element added/removed once:

$$
T(n) = O(n)
$$

**Space.** The distinctness set:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Longest Substring Without Repeating Characters** ([15.1](longest-substring-without-repeating-characters.md)) — the length version of the identical window.
- **Minimum Window Substring** ([15.2](minimum-window-substring.md)) — the formed-counter shrink family.
- **Interview follow-up:** "Why track `windowSum` instead of recomputing?" The running sum makes each shrink O(1) (one subtraction per eviction) instead of O(window). The sum and the set are mirrors — any divergence between them is a bug; keeping them updated in lockstep is the invariant to state out loud.
