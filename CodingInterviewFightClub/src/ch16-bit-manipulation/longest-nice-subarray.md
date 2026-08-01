# 16.14 Longest Nice Subarray

> **Source**: [`src/main/kotlin/bitset/LongestNiceSubarray.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/bitset/LongestNiceSubarray.kt)
> **Pattern**: sliding window with OR mask · **Core page**

## The Problem

The longest subarray where every pair's AND is 0 (all bits distinct).

- Constraints: n ≤ 10⁵.

## Examples

```
Input:  nums = [1,3,8,48,10]   -> Output: 3   ([3,8,48])
```

## Intuition — the window's OR mask holds all bits; an AND ≠ 0 means a collision

```kotlin
var left = 0
var bitMask = 0
var maxLen = 0

for (right in nums.indices) {
    while ((bitMask and nums[right]) != 0) {
        bitMask = bitMask xor nums[left]
        left++
    }

    bitMask = bitMask or nums[right]
    maxLen = maxOf(maxLen, right - left + 1)
}
return maxLen
```

**Why the XOR removal?** The window's bits are distinct — `nums[left]`'s bits are exactly in the mask, so XOR (toggle) removes them on the shrink. The [15.x](../ch15-sliding-window/pattern-primer.md) variable window with a bit payload.

## Approach 1 — Sliding OR window (the repo's version, optimal)

```kotlin
class LongestNiceSubarray {
    /**
     * @param nums input array
     * @return     longest nice subarray
     */
    fun longestNiceSubarray(nums: IntArray): Int {
        var left = 0
        var bitMask = 0
        var maxLen = 0

        for (right in nums.indices) {
            while ((bitMask and nums[right]) != 0) {
                bitMask = bitMask xor nums[left]
                left++
            }

            bitMask = bitMask or nums[right]
            maxLen = maxOf(maxLen, right - left + 1)
        }
        return maxLen
    }
}
```

```java
public class LongestNiceSubarray {
    /**
     * @param nums input array
     * @return     longest nice subarray
     */
    public int longestNiceSubarray(int[] nums) {
        int left = 0, mask = 0, best = 0;

        for (int right = 0; right < nums.length; right++) {
            while ((mask & nums[right]) != 0) {
                mask ^= nums[left];
                left++;
            }

            mask |= nums[right];
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
```

```cpp
#include <vector>
#include <algorithm>

class LongestNiceSubarray {
public:
    /**
     * @param nums input array
     * @return     longest nice subarray
     */
    int longestNiceSubarray(std::vector<int>& nums) {
        int left = 0, mask = 0, best = 0;

        for (int right = 0; right < (int)nums.size(); right++) {
            while ((mask & nums[right]) != 0) {
                mask ^= nums[left];
                left++;
            }

            mask |= nums[right];
            best = std::max(best, right - left + 1);
        }
        return best;
    }
};
```

```python
def longest_nice_subarray(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     longest nice subarray
    """
    left = 0
    mask = 0
    best = 0

    for right, num in enumerate(nums):
        while mask & num:
            mask ^= nums[left]
            left += 1

        mask |= num
        best = max(best, right - left + 1)

    return best
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     longest nice subarray
    pub fn longest_nice_subarray(nums: Vec<i32>) -> i32 {
        let (mut left, mut mask, mut best) = (0, 0, 0);

        for right in 0..nums.len() {
            while mask & nums[right] != 0 {
                mask ^= nums[left];
                left += 1;
            }

            mask |= nums[right];
            best = best.max(right - left + 1);
        }
        best as i32
    }
}
```

## Dry run

**Input:** `nums = [1,3,8,48,10]`.

```
1: mask 1.  3: 1&3 = 1? 1(01) & 3(11) = 1 != 0 -> shrink: mask ^= 1 -> 0, left 1.  mask 3.  len 1.
8: 3&8=0.  mask 11.  len 2.  48: 11&48 = 0? 11(01011) & 48(110000) = 0.  mask 59.  len 3.
10: 59&10 = 59(111011) & 10(001010) = 001010 = 10 != 0 -> shrink: mask ^= 3 (left=1): 59^3 = 56.
  still 56&10 = 8 != 0 -> mask ^= 8: 48.  48&10 = 0.  mask 58.  left 3.  len 2.
best = 3 ✓
```

## Complexity

**Time.** Amortized O(n):

$$
T(n) = O(n)
$$

**Space.** Constants:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Interview follow-up:** "Why does XOR remove exactly `nums[left]`?" The invariant guarantees the window's bits are disjoint — each value's bits appear once, so toggling restores the pre-add state.
