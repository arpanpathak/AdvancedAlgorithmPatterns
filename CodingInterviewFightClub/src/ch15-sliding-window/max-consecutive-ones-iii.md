# 15.7 Max Consecutive Ones III

> **Source:** [`src/main/kotlin/sliding_window/MaxConsecutiveOnes_III.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/sliding_window/MaxConsecutiveOnes_III.kt)
> **Pattern:** flip-budget window · **Core page**

## The Problem

Given a binary array `nums` and an integer `k`, return the length of the **longest subarray with at most k zeros** (equivalently: you may flip at most k zeros to ones).

- Constraints: $1 \le n \le 10^5$; `k` in range.

## Examples

```
Input:  nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2   -> Output: 6   ([1,1,1,0,0,1,1,1,1] -> flip two zeros)
Input:  nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k = 3 -> Output: 10
```

## Intuition — the budget `k` counts zeros in the window

Flipping at most k zeros ⟺ the window contains **at most k zeros**. The window template: expand; if the zero-count exceeds k, shrink until it's k again.

**The repo's compact version** uses the "budget" directly — `K` starts at `k` and *decrements* on every zero seen:

```kotlin
var K = k
while (right < nums.size) {
    if (nums[right++] == 0) K--           // spent the budget on a zero
    if (K < 0) {                          // too many zeros: drop the leftmost
        K += 1 - nums[left++]             // +1 if it was a zero (refund), +0 if a one
    }
}
return right - left
```

The `K += 1 - nums[left++]` line is the trick: if the leaving element is a `0`, `1 - 0 = 1` refunds the budget; if it's a `1`, `1 - 1 = 0` no change. One arithmetic line encodes the conditional refund.

**Why track the budget instead of `maxLen`?** The window never shrinks below its longest valid length — the `right - left` at the end *is* the answer, because `left` only ever moves when the window is over-budget, and `right` only grows. This is the "the window length never decreases, so the final length is the max" flavor — the same trick as [15.3](longest-repeating-character-replacement.md), where `maxLength` is tracked because there the shrink condition differs.

## Approach 1 — For each start, extend to the budget limit (O(n^2))

For every `left`, extend `right` while zeros ≤ k: quadratic, and the baseline.

## Approach 2 — Budget window with refund (the repo's version, optimal)

```kotlin
class MaxConsecutiveOnes_III {
    /**
     * @param nums binary array
     * @param k    flips allowed
     * @return     longest subarray with at most k zeros
     */
    fun longestOnes(nums: IntArray, k: Int): Int {
        var left = 0
        var K = k                            // remaining flip budget
        var right = 0

        while (right < nums.size) {
            if (nums[right++] == 0)          // entering a zero spends the budget
                K--

            if (K < 0) {                     // over budget: drop the leftmost element
                K += 1 - nums[left++]        // +1 if it was a zero (refund), +0 if a one
            }
        }
        return right - left                  // the window never shrank below the max
    }

    // Another intuitive solution using an explicit while loop
    fun longestOnes1(nums: IntArray, k: Int): Int {
        var left = 0
        var remainingK = k
        var maxLen = 0

        for (right in nums.indices) {
            if (nums[right] == 0) remainingK--

            // If the window becomes invalid, move `left` forward
            while (remainingK < 0) {
                if (nums[left++] == 0) remainingK++
            }
            maxLen = maxOf(maxLen, right - left + 1)
        }
        return maxLen
    }
}
```

```java
public class MaxConsecutiveOnesIII {
    /**
     * @param nums binary array
     * @param k    flips allowed
     * @return     longest subarray with at most k zeros
     */
    public int longestOnes(int[] nums, int k) {
        int left = 0, right = 0, budget = k;

        while (right < nums.length) {
            if (nums[right++] == 0) budget--;      // entering a zero spends the budget

            if (budget < 0) {                      // over budget: drop the leftmost element
                budget += 1 - nums[left++];        // +1 if it was a zero (refund), +0 if a one
            }
        }
        return right - left;                       // the window never shrank below the max
    }
}
```

```cpp
#include <vector>

class MaxConsecutiveOnesIII {
public:
    /**
     * @param nums binary array
     * @param k    flips allowed
     * @return     longest subarray with at most k zeros
     */
    int longestOnes(std::vector<int>& nums, int k) {
        int left = 0, right = 0, budget = k;

        while (right < (int)nums.size()) {
            if (nums[right++] == 0) budget--;      // entering a zero spends the budget

            if (budget < 0) {                      // over budget: drop the leftmost element
                budget += 1 - nums[left++];        // +1 if it was a zero (refund), +0 if a one
            }
        }
        return right - left;                       // the window never shrank below the max
    }
};
```

```python
def longest_ones(nums: list[int], k: int) -> int:
    """
    @param nums: binary array
    @param k:    flips allowed
    @return:     longest subarray with at most k zeros
    """
    left = 0
    budget = k
    right = 0

    while right < len(nums):
        if nums[right] == 0:                 # entering a zero spends the budget
            budget -= 1
        right += 1

        if budget < 0:                       # over budget: drop the leftmost element
            if nums[left] == 0:
                budget += 1                  # refund the spent zero
            left += 1
    return right - left                      # the window never shrank below the max
```

```rust
impl Solution {
    /// @param nums binary array
    /// @param k    flips allowed
    /// @return     longest subarray with at most k zeros
    pub fn longest_ones(nums: Vec<i32>, k: i32) -> i32 {
        let (mut left, mut right) = (0usize, 0usize);
        let mut budget = k;

        while right < nums.len() {
            if nums[right] == 0 { budget -= 1; }   // entering a zero spends the budget
            right += 1;

            if budget < 0 {                        // over budget: drop the leftmost element
                if nums[left] == 0 { budget += 1; }    // refund the spent zero
                left += 1;
            }
        }
        (right - left) as i32                      // the window never shrank below the max
    }
}
```

## Dry run

**Input:** `nums = [1,1,1,0,0,0,1,1,1,1,0]`, `k = 2`.

```
budget = 2, left = 0, right = 0

right 0-2 (1,1,1): no budget change.  right=3.
right 3 (0): budget=1.  right=4.
right 4 (0): budget=0.  right=5.
right 5 (0): budget=-1 -> over budget: nums[0]=1 -> no refund, left=1.  right=6.
right 6 (1): budget=-1 -> nums[1]=1 -> left=2.  right=7.
right 7 (1): budget=-1 -> nums[2]=1 -> left=3.  right=8.
right 8 (1): budget=-1 -> nums[3]=0 -> refund, budget=0, left=4.  right=9.
right 9 (1): budget=0.  right=10.
right 10 (0): budget=-1 -> nums[4]=0 -> refund, budget=0, left=5.  right=11.

return 11 - 5 = 6 ✓   (the window [5..11) = [0,1,1,1,1,0], two zeros)
```

The refund arithmetic in action: each over-budget step moves `left` past one element — refunding if it was a zero (recovering budget) or not if a one (the zero deficit just shifts right). The window `[left, right)` always has exactly `k` zeros, and because `left` only advances on overshoot, the final length is the max.

## Complexity

**Time.** One pass, each pointer moves at most n:

$$
T(n) = O(n)
$$

**Space.** Constant:

$$
S(n) = O(1)
$$

## Variants & follow-ups

- **Longest Repeating Character Replacement** ([15.3](longest-repeating-character-replacement.md)) — the same budget-window idea over a 26-letter alphabet.
- **Max Consecutive Ones (I/II)** — the budget is 0 (pure runs) or 1 (one deletion/flip); the same template with `k` fixed.
- **Interview follow-up:** "Why can the final window length be returned directly, without tracking a max?" The window never shrinks: `right` only grows, and `left` only advances when the window is over-budget — one step per over-budget expansion, so the length `right - left` is non-decreasing and its final value is the maximum. The budget arithmetic (`+= 1 - nums[left++]`) is a conditional refund folded into one line.
