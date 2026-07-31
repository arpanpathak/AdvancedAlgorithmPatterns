# 8.4 Next Greater Element II

> **Source:** [`src/main/kotlin/stack/NextGreaterElement_II.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/NextGreaterElement_II.kt)
> **Pattern:** circular monotonic stack · **Core page**

## The Problem

Given a circular integer array `nums` (the last element's next is the first), return an array where `answer[i]` is the **next greater element** for `nums[i]` — the first value strictly greater than it when scanning forward (wrapping around), or `-1` if none exists.

- Constraints: $1 \le n \le 10^4$; $-10^9 \le nums[i] \le 10^9$.

## Examples

```
Input:  nums = [1,2,1]
Output: [2,-1,2]     (1 -> 2; 2 has no greater anywhere; the last 1 wraps to 2)

Input:  nums = [1,2,3,4,3]
Output: [2,3,4,-1,4] (the last 3 wraps to the first 4)
```

## Intuition — the circle is a doubled array, swept once

The circular twist breaks the plain [8.3](daily-temperatures.md) monotonic sweep in one way: an element can find its next greater **after wrapping past the end**. Two standard fixes:

1. **Doubled array** — conceptually concatenate `nums` with itself and sweep `2n` positions, storing only the first `n` results.
2. **Modulo indexing** — sweep `i in 0 until 2*n`, index the array with `i % n`, and only push indices from the *first* pass (`i < n`) onto the stack.

Both are the same idea; the repo uses modulo. The monotonic logic is unchanged from [8.3](daily-temperatures.md): while the current value beats the stack top, the top's next greater is the current value — pop and record.

**Why does pushing only `i < n` work?** During the second pass, every element of the stack (all from the first pass) can still be resolved by a wrapped-around element. But an index from the second pass must never be pushed — it would only be resolved by a *third* pass, and the answer would be wrong (or the loop would never terminate). One push per element, `2n` resolution opportunities: every element either finds its greater in the first pass, finds it in the wrap-around pass, or stays on the stack with `-1`.

**"Strictly greater"** means `>` (not `>=`) in the while condition — equal values do *not* resolve each other, matching the problem statement.

## Approach 1 — For each element, scan forward with wrap (too slow)

For each `i`, walk up to `n` steps (mod `n`) until a greater value: $O(n^2)$ worst case (e.g., strictly decreasing arrays).

## Approach 2 — Circular monotonic stack (the repo's version, optimal)

```kotlin
class NextGreaterElement_II {
    /**
     * @param nums circular array
     * @return     next greater element for each index, -1 if none
     */
    fun nextGreaterElements(nums: IntArray): IntArray {
        val n = nums.size
        val result = IntArray(n) { -1 }              // default: no greater anywhere
        val stack = mutableListOf<Int>()             // indices of unresolved elements

        // Traverse the array twice (for circular behavior)
        for (i in 0 until 2 * n) {
            val currentIndex = i % n                 // modulo simulates the wrap

            while (stack.isNotEmpty() && nums[stack.last()] < nums[currentIndex]) {
                val index = stack.removeLast()
                result[index] = nums[currentIndex]   // currentIndex is the next greater
            }

            // Only add indices from the first traversal (i < n)
            if (i < n) {
                stack.add(currentIndex)              // each element pushed exactly once
            }
        }
        return result
    }
}
```

```java
import java.util.*;

public class NextGreaterElementII {
    /**
     * @param nums circular array
     * @return     next greater element for each index, -1 if none
     */
    public int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < 2 * n; i++) {
            int cur = nums[i % n];
            while (!stack.isEmpty() && nums[stack.peek()] < cur) {
                result[stack.pop()] = cur;
            }
            if (i < n) stack.push(i);                // push only first-pass indices
        }
        return result;
    }
}
```

```cpp
#include <stack>
#include <vector>

class NextGreaterElementII {
public:
    /**
     * @param nums circular array
     * @return     next greater element for each index, -1 if none
     */
    std::vector<int> nextGreaterElements(std::vector<int>& nums) {
        int n = nums.size();
        std::vector<int> result(n, -1);
        std::stack<int> st;

        for (int i = 0; i < 2 * n; i++) {
            int cur = nums[i % n];
            while (!st.empty() && nums[st.top()] < cur) {
                result[st.top()] = cur;
                st.pop();
            }
            if (i < n) st.push(i);                   // push only first-pass indices
        }
        return result;
    }
};
```

```python
def next_greater_elements(nums: list[int]) -> list[int]:
    """
    @param nums: circular array
    @return:     next greater element for each index, -1 if none
    """
    n = len(nums)
    result = [-1] * n
    stack = []                                   # indices of unresolved elements

    for i in range(2 * n):
        cur = nums[i % n]
        while stack and nums[stack[-1]] < cur:
            result[stack.pop()] = cur
        if i < n:
            stack.append(i)                      # push only first-pass indices
    return result
```

```rust
impl Solution {
    /// @param nums circular array
    /// @return     next greater element for each index, -1 if none
    pub fn next_greater_elements(nums: Vec<i32>) -> Vec<i32> {
        let n = nums.len();
        let mut result = vec![-1; n];
        let mut stack: Vec<usize> = Vec::new();

        for i in 0..2 * n {
            let cur = nums[i % n];
            while let Some(&idx) = stack.last() {
                if nums[idx] >= cur { break; }
                result[idx] = cur;               // current index is the next greater
                stack.pop();
            }
            if i < n {
                stack.push(i % n);               // push only first-pass indices
            }
        }
        result
    }
}
```

## Dry run

**Input:** `nums = [1,2,3,4,3]`.

```
result = [-1,-1,-1,-1,-1], stack = []

i=0 (1): stack empty; push 0.                         stack=[0]
i=1 (2): 2 > 1 -> result[0]=2, pop. push 1.           stack=[1]
i=2 (3): 3 > 2 -> result[1]=3, pop. push 2.           stack=[2]
i=3 (4): 4 > 3 -> result[2]=4, pop. push 3.           stack=[3]
i=4 (3): 3 > 4? no. push 4 (i<5).                     stack=[3,4]
i=5 (1): 1 > 3? no. (i>=5: no push)                   stack=[3,4]
i=6 (2): 2 > 3? no.                                   stack=[3,4]
i=7 (3): 3 > 4? no.  (3 > 3? no — strictly greater)   stack=[3,4]
i=8 (4): 4 > 3 -> result[4]=4, pop. 4 > 4? no.        stack=[3]
i=9 (3): 3 > 4? no.                                   stack=[3]

result = [2,3,4,-1,4] ✓   (index 3 = 4 has no greater — even wrapped; the others resolved)
```

The wrap moment is i=8: the element at index 4 (value 3) finally sees the *first* 4 from the second pass — distance doesn't matter, only value — so `result[4]=4`. Meanwhile index 3 (the 4 itself) stays on the stack forever: nothing is strictly greater than 4, so it keeps its `-1`.

## Complexity

**Time.** Each index pushed once; the stack pops each element at most once across `2n` iterations:

$$
T(n) = O(n)
$$

**Space.** The stack and result:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Next Greater Element I** (`src/main/kotlin/stack/NextGreaterElement_I.kt`) — the *non-circular* version with a subset query: sweep once, record `value -> next greater` in a map, answer the query list. The "circular" flag is literally the only difference from this page.
- **Daily Temperatures** ([8.3](daily-temperatures.md)) — distances instead of values, no wrap: the same while-pop skeleton.
- **Next Smaller Element** — mirror the comparator (`>` instead of `<`); everything else is identical. Saying "flip the comparison" is the 5-second answer.
- **Interview follow-up:** "Why is one push per element enough even with a doubled sweep?" Because the second pass exists *only* to resolve leftover indices from the first. Pushing second-pass indices would create answers that depend on a third pass — an infinite loop or wrong values. The `i < n` guard is what keeps the sweep linear.
