# 3.1 Two Sum II — Input Array Is Sorted

> **Source:** [`src/main/kotlin/array/twopointer/TwoSum_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/array/twopointer/TwoSum_II.kt)
> **Pattern:** converge (Dance 1) · **Core page — the two-pointer archetype**

## The Problem

Given a **1-indexed** sorted array `numbers` and a `target`, find two numbers that sum to `target`. Return their 1-based indices `[i, j]`. Exactly one solution exists; each element used once.

- Constraints: $2 \le n \le 3 \times 10^4$, sorted ascending.

## Examples

```
Input:  numbers = [2, 7, 11, 15], target = 9
Output: [1, 2]
Explanation: numbers[0] + numbers[1] = 2 + 7 = 9.

Input:  numbers = [2, 3, 4], target = 6
Output: [1, 3]

Input:  numbers = [-1, 0], target = -1
Output: [1, 2]
```

## Intuition — monotonicity of the pair sum

The unsorted version needs a hash map ($O(n)$ time, $O(n)$ space). The sorted version needs **no extra space** — because of a monotonicity argument:

- Start with `left = 0`, `right = n-1` (the widest pair).
- `s = numbers[left] + numbers[right]`.
  - `s < target` → we need a *larger* sum. The only way is to move `left` right (increasing one addend; `numbers` is sorted, so `numbers[left+1] ≥ numbers[left]`). **Every pair `(left, k)` with `k < right` is now provably too small** — discard the entire row of `left`.
  - `s > target` → move `right` left; **every pair `(k, right)` with `k > left` is provably too large** — discard the column of `right`.

Each step kills a full row or column of the $n \times n$ candidate matrix, so the loop terminates in at most $n$ steps — **$O(n)$, not $O(n^2)$**. This is Dance 1 from [3.0](pattern-primer.md) in its purest form.

## Approach 1 — Hash map (works on unsorted too)

Store `target - x` for each `x`; find the complement. $O(n)$ time, $O(n)$ space. Correct everywhere, but it *ignores* the sorted structure — the interview's whole point.

## Approach 2 — Two pointers converge (optimal)

```kotlin
/**
 * @param numbers the 1-indexed sorted array
 * @param target  the sum to find
 * @return        the 1-based indices [i, j] of the two numbers, or empty if none
 */
fun twoSum(numbers: IntArray, target: Int): IntArray {
    var start = 0
    var end = numbers.lastIndex

    while (start < end) {
        val sum = numbers[start] + numbers[end]
        when {
            sum == target -> return intArrayOf(start + 1, end + 1)
            sum < target  -> start++    // need a bigger sum -> advance left
            else          -> end--      // need a smaller sum -> retreat right
        }
    }
    return intArrayOf()   // unreachable given "exactly one solution"
}
```

```java
public class TwoSumII {
    /**
     * @param numbers the 1-indexed sorted array
     * @param target  the sum to find
     * @return        the 1-based indices [i, j] of the two numbers, or empty if none
     */
    public int[] twoSum(int[] numbers, int target) {
        int start = 0, end = numbers.length - 1;
        while (start < end) {
            int sum = numbers[start] + numbers[end];
            if (sum == target) return new int[]{start + 1, end + 1};
            if (sum < target) start++;
            else end--;
        }
        return new int[0];
    }
}
```

```cpp
#include <vector>

class TwoSumII {
public:
    /**
     * @param numbers the 1-indexed sorted array
     * @param target  the sum to find
     * @return        the 1-based indices [i, j] of the two numbers, or empty if none
     */
    std::vector<int> twoSum(const std::vector<int>& numbers, int target) {
        int start = 0, end = (int)numbers.size() - 1;
        while (start < end) {
            int sum = numbers[start] + numbers[end];
            if (sum == target) return {start + 1, end + 1};
            if (sum < target) start++;
            else end--;
        }
        return {};
    }
};
```

```python
def two_sum(numbers: list[int], target: int) -> list[int]:
    """
    @param numbers: the 1-indexed sorted array
    @param target:  the sum to find
    @return:        the 1-based indices [i, j] of the two numbers, or empty if none
    """
    start, end = 0, len(numbers) - 1
    while start < end:
        s = numbers[start] + numbers[end]
        if s == target:
            return [start + 1, end + 1]
        if s < target:
            start += 1          # need a bigger sum -> advance left
        else:
            end -= 1            # need a smaller sum -> retreat right
    return []
```

```rust
impl Solution {
    /// @param numbers the 1-indexed sorted array
    /// @param target  the sum to find
    /// @return        the 1-based indices [i, j] of the two numbers, or empty if none
    pub fn two_sum(numbers: Vec<i32>, target: i32) -> Vec<i32> {
        let (mut start, mut end) = (0usize, numbers.len() - 1);
        while start < end {
            let sum = numbers[start] + numbers[end];
            if sum == target {
                return vec![start as i32 + 1, end as i32 + 1];
            }
            if sum < target {
                start += 1;
            } else {
                end -= 1;
            }
        }
        vec![]
    }
}
```

## Dry run

**Input:** `numbers = [2, 7, 11, 15]`, `target = 9`.

```
start=0  end=3  sum = 2 + 15 = 17 > 9  -> end=2   (15 can't pair with anyone)
start=0  end=2  sum = 2 + 11 = 13 > 9  -> end=1   (11 dies too)
start=0  end=1  sum = 2 + 7  = 9  == 9 -> return [1, 2] ✓
```

Watch the discarded rows/columns:

```
       2    7   11   15
  2    ✗    ✓    ✗    ✗        (17>9 kills the 15 column; 13>9 kills the 11 column)
  7
 11
 15
```

The first step (`sum=17`) didn't just fail — it *proved* that `2 + 15`, `7 + 15`, `11 + 15` are all too big (everything right of 2 is ≥ 7... wait, strictly: since `numbers[0]=2` is the smallest, `2 + 15` is the *smallest* sum with 15, and it's already too big — so no pair with 15 can work). That's the entire row-discard argument, executable in one line.

## Complexity

**Time.**

$$
T(n) = O(n)
$$

The two pointers move at most $n$ times combined (each step moves exactly one, and they never cross).

**Space.** $O(1)$.

## Variants & follow-ups

- **[3.2](three-sum.md)** — the same dance inside a loop over a third number.
- **4Sum** (`src/main/kotlin/array/twopointer/4Sum.kt`) — nest the dance twice (with a duplicate-skip discipline).
- **Container With Most Water / Trapping Rain Water** (`src/main/kotlin/array/twopointer/TrappingRainWater.kt`) — the same converge dance, but the *decision* is about which wall to keep, not which sum to match.
- **Interview follow-up:** "What if the array is NOT sorted?" The monotonicity dies — switch to the hash map (or sort first, costing $O(n \log n)$). Say that boundary explicitly.
- **Interview follow-up:** "Prove the loop terminates." Each iteration moves exactly one pointer strictly toward the other; the distance `end - start` strictly decreases, so the loop runs at most $n-1$ times.
