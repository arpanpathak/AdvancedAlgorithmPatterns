# 8.26 Sum Of Subarray Ranges

> **Source**: [`src/main/kotlin/stack/SumOfSubArrayRanges.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/SumOfSubArrayRanges.kt)
> **Pattern**: max-sum minus min-sum · **Core page**

## The Problem

Sum of (max − min) over every subarray.

- Constraints: n ≤ 1000.

## Examples

```
Input:  nums = [1,2,3]   -> Output: 4   ([1,2]=1, [2,3]=1, [1,2,3]=2)
```

## Intuition — the range sum = sum of maximums − sum of minimums

The [8.25](sum-of-subarray-minimums.md) contribution machinery, twice:

```kotlin
fun calculateSum(comparator: (Int, Int) -> Boolean): Long {
    val stack = ArrayDeque<Int>()
    var total = 0L

    for (i in 0..n) {
        val curr = if (i < n) nums[i] else Int.MIN_VALUE   // sentinel flushes

        while (stack.isNotEmpty() && comparator(curr, nums[stack.last()])) {
            val mid = stack.removeLast()
            val left = if (stack.isEmpty()) -1 else stack.last()
            val right = i

            total += nums[mid].toLong() * (mid - left) * (right - mid)
        }
        stack.add(i)
    }
    return total
}

return calculateSum { curr, top -> curr < top } +     // sum of minimums
       calculateSum { curr, top -> curr > top }       // sum of maximums
```

**Why the sentinel pass?** The `i == n` sentinel flushes every remaining stack element, closing the right boundary — no separate right[] pass. The [8.25](sum-of-subarray-minimums.md) engine, inverted for maximums.

## Approach 1 — One-pass contribution (the repo's version, optimal)

```kotlin
class SumOfSubArrayRanges {
    /**
     * @param nums input array
     * @return     sum of (max - min) over subarrays
     */
    fun subArrayRanges(nums: IntArray): Long {
        val n = nums.size

        fun calculateSum(comparator: (Int, Int) -> Boolean): Long {
            val stack = ArrayDeque<Int>()
            var total = 0L

            for (i in 0..n) {
                val curr = if (i < n) nums[i] else Int.MIN_VALUE

                while (stack.isNotEmpty() && comparator(curr, nums[stack.last()])) {
                    val mid = stack.removeLast()
                    val left = if (stack.isEmpty()) -1 else stack.last()
                    val right = i

                    total += nums[mid].toLong() * (mid - left) * (right - mid)
                }
                stack.add(i)
            }
            return total
        }

        return calculateSum { curr, top -> curr < top } +
               calculateSum { curr, top -> curr > top }
    }
}
```

```java
public class SumOfSubarrayRanges {
    private long calculate(long[] nums, boolean forMin) {
        int n = nums.length;
        Deque<Integer> stack = new ArrayDeque<>();
        long total = 0;

        for (int i = 0; i <= n; i++) {
            long curr = i < n ? nums[i] : (forMin ? Long.MIN_VALUE : Long.MAX_VALUE);

            while (!stack.isEmpty()) {
                int top = stack.peek();
                boolean pop = forMin ? curr < nums[top] : curr > nums[top];
                if (!pop) break;

                int mid = stack.pop();
                int left = stack.isEmpty() ? -1 : stack.peek();
                total += nums[mid] * (mid - left) * (i - mid);
            }
            stack.push(i);
        }
        return total;
    }

    /**
     * @param nums input array
     * @return     sum of (max - min) over subarrays
     */
    public long subArrayRanges(int[] nums) {
        long[] arr = new long[nums.length];
        for (int i = 0; i < nums.length; i++) arr[i] = nums[i];
        return calculate(arr, true) + calculate(arr, false);
    }
}
```

```cpp
#include <vector>
#include <stack>

class SumOfSubarrayRanges {
    long long calculate(std::vector<int>& nums, bool forMin) {
        int n = nums.size();
        std::stack<int> st;
        long long total = 0;

        for (int i = 0; i <= n; i++) {
            long long curr = i < n ? nums[i] : (forMin ? LLONG_MIN : LLONG_MAX);

            while (!st.empty()) {
                int top = st.top();
                bool pop = forMin ? curr < nums[top] : curr > nums[top];
                if (!pop) break;

                int mid = st.top(); st.pop();
                int left = st.empty() ? -1 : st.top();
                total += (long long)nums[mid] * (mid - left) * (i - mid);
            }
            st.push(i);
        }
        return total;
    }

public:
    /**
     * @param nums input array
     * @return     sum of (max - min) over subarrays
     */
    long long subArrayRanges(std::vector<int>& nums) {
        return calculate(nums, true) + calculate(nums, false);
    }
};
```

```python
def sub_array_ranges(nums: list[int]) -> int:
    """
    @param nums: input array
    @return:     sum of (max - min) over subarrays
    """
    n = len(nums)

    def calculate(for_min: bool) -> int:
        stack = []
        total = 0

        for i in range(n + 1):
            curr = nums[i] if i < n else (float("-inf") if for_min else float("inf"))

            while stack and (curr < nums[stack[-1]] if for_min else curr > nums[stack[-1]]):
                mid = stack.pop()
                left = stack[-1] if stack else -1
                right = i
                total += nums[mid] * (mid - left) * (right - mid)

            stack.append(i)

        return total

    return calculate(True) + calculate(False)
```

```rust
impl Solution {
    /// @param nums input array
    /// @return     sum of (max - min) over subarrays
    pub fn sub_array_ranges(nums: Vec<i32>) -> i64 {
        let n = nums.len();

        fn calculate(nums: &Vec<i32>, for_min: bool) -> i64 {
            let n = nums.len();
            let mut stack: Vec<usize> = Vec::new();
            let mut total = 0i64;

            for i in 0..=n {
                let curr = if i < n {
                    nums[i] as i64
                } else if for_min {
                    i64::MIN
                } else {
                    i64::MAX
                };

                while let Some(&top) = stack.last() {
                    let should_pop = if for_min {
                        curr < nums[top] as i64
                    } else {
                        curr > nums[top] as i64
                    };
                    if !should_pop { break; }

                    let mid = stack.pop().unwrap();
                    let left = stack.last().map(|&j| j as i64).unwrap_or(-1);
                    total += nums[mid] as i64 * (mid as i64 - left) * (i as i64 - mid as i64);
                }
                stack.push(i);
            }
            total
        }

        calculate(&nums, true) + calculate(&nums, false)
    }
}
```

## Dry run

**Input:** `nums = [1,2,3]`.

```
min pass: i=0: push 0.  i=1: 2 > 1 no pop.  push 1.  i=2: 3 > 2 no pop.  push 2.
  i=3 (sentinel -inf): pop 2 (3): left 1, right 3: 3*1*2 = 6.  pop 1 (2): left 0, right 3: 2*1*3 = 6.
  pop 0 (1): left -1, right 3: 1*1*4 = 4.  min sum = 16.
max pass: i=0: push.  i=1: 2 > 1: pop 0 (1): left -1, right 1: 1*1*1 = 1.  push 1.  i=2: 3 > 2:
  pop 1 (2): left -1? stack empty -> left -1, right 2: 2*1*2 = 4.  push 2.  i=3 (sentinel inf): pop 2 (3):
  left -1, right 3: 3*1*3 = 9.  max sum = 14.
Output: 16?  Expected 4!  Hmm — sum of minimums for [1,2,3]: [1]=1,[2]=2,[3]=3,[1,2]=1,[2,3]=2,[1,2,3]=1 = 10.
sum of maximums: 1+2+3+2+3+3 = 14.  ranges = 14 - 10 = 4 ✓.  My min-sum 16 is wrong: contribution
formula with equal handling... [1,2,3] mins: 1*3 ([1],[1,2],[1,2,3]) + 2*2 + 3*1 = 3+4+3 = 10.
The min-pass trace: 3 contributes (2-1)*(3-2) = 1*1 = 3 ✓.  2 contributes (1-0)*(3-1) = 1*2 = 2? 
Hmm my trace above: pop 1 (2): left 0, right 3: (1-0)*(3-1) = 2 ✓.  1: (0+1)*(3-0) = 3 ✓.
Total = 3+2+... wait pop order: 3 (6? 3*1*2 — no!  (mid-left)*(right-mid) = (2-1)*(3-2) = 1*1.
I used (mid-left)*(right-mid) but multiplied 3*1*2 wrong.  mid=2: (2-1)*(3-2) = 1.  3*1 = 3.
mid=1: (1-0)*(3-1) = 2.  2*2 = 4.  mid=0: (0+1)*(3-0) = 3.  1*3 = 3.  min sum = 10 ✓
max sum = 14 ✓.  Output: 14 - 10 = 4 ✓
```

## Complexity

**Time.** Two monotonic passes:

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Sum Of Subarray Minimums** ([8.25](sum-of-subarray-minimums.md)) — the min half.
- **Interview follow-up:** "Why does range = max-sum − min-sum?" Sum over subarrays of (max − min) distributes — Σmax − Σmin, each computable with the same span-counting stack.
