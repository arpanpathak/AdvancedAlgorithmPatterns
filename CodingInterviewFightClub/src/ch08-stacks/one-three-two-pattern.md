# 8.20 One Three Two Pattern

> **Source**: [`src/main/kotlin/stack/OneThreeTwoPattern.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/OneThreeTwoPattern.kt)
> **Pattern**: decreasing stack with a third-element memory · **Core page**

## The Problem

Does `nums` contain `i < j < k` with `nums[i] < nums[k] < nums[j]`?

- Constraints: n ≤ 2×10⁵.

## Examples

```
Input:  nums = [1,2,3,4]    -> Output: false
Input:  nums = [3,1,4,2]    -> Output: true   (1 < 2 < 4)
Input:  nums = [-1,3,2,0]   -> Output: true
```

## Intuition — scan right-to-left; the stack holds candidate `j`s; `third` is the best `k`

Walk from the right. `third` = the largest value that has a smaller element to its left (a valid `k`). For each `nums[i]`: if `nums[i] < third`, we've found `i < k < j` → true. The stack keeps a **decreasing** sequence of candidate `j`s; popping smaller values updates `third`:

```kotlin
val stack = ArrayDeque<Int>()
var thirdElement = Int.MIN_VALUE

for (i in nums.size - 1 downTo 0) {
    if (nums[i] < thirdElement) return true

    while (stack.isNotEmpty() && nums[i] > stack.last()) {
        thirdElement = stack.removeLast()   // a smaller element follows nums[i]'s left side
    }
    stack.add(nums[i])
}
return false
```

**Why does the stack stay decreasing?** Only values larger than the current top remain — smaller ones pop and become `third` candidates. The invariant: the stack is decreasing, and `third` is the max of everything popped (the [8.3](daily-temperatures.md) monotonic discipline with a memory).

## Approach 1 — Brute force triples (O(n³))

Check all i<j<k: correct, absurd.

## Approach 2 — Decreasing stack + third memory (the repo's version, optimal)

```kotlin
class OneThreeTwoPattern {
    /**
     * @param nums input array
     * @return     true iff a 132 pattern exists
     */
    fun find132pattern(nums: IntArray): Boolean {
        val stack = ArrayDeque<Int>()
        var thirdElement = Int.MIN_VALUE

        for (i in nums.size - 1 downTo 0) {
            if (nums[i] < thirdElement) return true

            while (stack.isNotEmpty() && nums[i] > stack.last()) {
                thirdElement = stack.removeLast()
            }

            stack.add(nums[i])
        }
        return false
    }
}
```

```java
import java.util.*;

public class OneThreeTwoPattern {
    /**
     * @param nums input array
     * @return     true iff a 132 pattern exists
     */
    public boolean find132pattern(int[] nums) {
        Deque<Integer> stack = new ArrayDeque<>();
        int third = Integer.MIN_VALUE;

        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums[i] < third) return true;

            while (!stack.isEmpty() && nums[i] > stack.peek()) {
                third = stack.pop();
            }
            stack.push(nums[i]);
        }
        return false;
    }
}
```

```cpp
#include <vector>
#include <stack>

class OneThreeTwoPattern {
public:
    /**
     * @param nums input array
     * @return     true iff a 132 pattern exists
     */
    bool find132pattern(std::vector<int>& nums) {
        std::stack<int> stack;
        int third = INT_MIN;

        for (int i = nums.size() - 1; i >= 0; i--) {
            if (nums[i] < third) return true;

            while (!stack.empty() && nums[i] > stack.top()) {
                third = stack.top();
                stack.pop();
            }
            stack.push(nums[i]);
        }
        return false;
    }
};
```

```python
def find132pattern(nums: list[int]) -> bool:
    """
    @param nums: input array
    @return:     true iff a 132 pattern exists
    """
    stack = []
    third = float("-inf")

    for num in reversed(nums):
        if num < third:
            return True

        while stack and num > stack[-1]:
            third = stack.pop()

        stack.append(num)

    return False
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     true iff a 132 pattern exists
    pub fn find132pattern(nums: Vec<i32>) -> bool {
        let mut stack: Vec<i32> = Vec::new();
        let mut third = i32::MIN;

        for &num in nums.iter().rev() {
            if num < third { return true; }

            while let Some(&top) = stack.last() {
                if num <= top { break; }
                third = stack.pop().unwrap();
            }
            stack.push(num);
        }
        false
    }
}
```

## Dry run

**Input:** `nums = [3,1,4,2]`.

```
scan right: 2: stack [].  push 2.  third = MIN.
4: 4 < MIN? no.  while 4 > 2 -> third = 2, pop.  push 4.  stack [4].
1: 1 < 2? YES -> return true ✓   (1 < 2 < 4 at indices 1, 3, 2)
```

The intuition crystallizes: at `4`, popping `2` records "there is a 2 with a smaller element to its left" (the eventual 1); the next smaller element (`1`) beats it → 132 found. The stack's decreasing order means every pop produces a valid `(j, k)` pair waiting for an `i`.

## Complexity

**Time.** Each element pushed/popped once:

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Next Greater Element** ([8.4](next-greater-element-ii.md)) — the monotonic-stack family.
- **Interview follow-up:** "Why right-to-left?" Scanning rightward would need to track both `j` and `k` simultaneously; right-to-left lets the stack + `third` encode the `(j, k)` pairs as they're discovered, and a smaller `i` triggers instantly.
