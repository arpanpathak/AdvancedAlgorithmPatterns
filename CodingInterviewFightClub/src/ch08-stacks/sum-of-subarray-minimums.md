# 8.25 Sum Of Subarray Minimums

> **Source**: [`src/main/kotlin/stack/SumOfSubArrayMinimum.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/SumOfSubArrayMinimum.kt)
> **Pattern**: monotonic-stack contribution · **Core page**

## The Problem

Sum of every subarray's minimum (mod 1e9+7).

- Constraints: n ≤ 3×10⁴.

## Examples

```
Input:  arr = [3,1,2,4]   -> Output: 17
```

## Intuition — each element is the min of (left span × right span) subarrays

For each i, find the previous smaller (`left[i]`) and next smaller-or-equal (`right[i]`) — `arr[i]` contributes `arr[i] × (i - left) × (right - i)`:

```kotlin
val stack = ArrayDeque<Int>()
var sum = 0L
val mod = 1_000_000_007

val left = IntArray(n)
val right = IntArray(n)

// left[i] = index of previous smaller
for (i in 0 until n) {
    while (stack.isNotEmpty() && arr[stack.last()] >= arr[i]) stack.removeLast()
    left[i] = if (stack.isEmpty()) -1 else stack.last()
    stack.add(i)
}

stack.clear()
// right[i] = index of next smaller-or-equal
for (i in n - 1 downTo 0) {
    while (stack.isNotEmpty() && arr[stack.last()] > arr[i]) stack.removeLast()
    right[i] = if (stack.isEmpty()) n else stack.last()
    stack.add(i)
}

for (i in 0 until n) {
    sum = (sum + arr[i].toLong() * (i - left[i]) * (right[i] - i)) % mod
}
return sum.toInt()
```

**Why the strict/≤ asymmetry?** Strict on one side and ≤ on the other makes each subarray's minimum *unique* — no double counting. The [8.3](daily-temperatures.md) monotonic stack in the span-counting role.

## Approach 1 — Monotonic-stack contributions (the repo's version, optimal)

```kotlin
class SumOfSubArrayMinimum {
    /**
     * @param arr input array
     * @return    sum of subarray minimums mod 1e9+7
     */
    fun sumSubarrayMins(arr: IntArray): Int {
        val n = arr.size
        val stack = ArrayDeque<Int>()
        var sum = 0L
        val mod = 1_000_000_007

        val left = IntArray(n)
        for (i in 0 until n) {
            while (stack.isNotEmpty() && arr[stack.last()] >= arr[i]) stack.removeLast()
            left[i] = if (stack.isEmpty()) -1 else stack.last()
            stack.add(i)
        }

        stack.clear()
        val right = IntArray(n)
        for (i in n - 1 downTo 0) {
            while (stack.isNotEmpty() && arr[stack.last()] > arr[i]) stack.removeLast()
            right[i] = if (stack.isEmpty()) n else stack.last()
            stack.add(i)
        }

        for (i in 0 until n) {
            sum = (sum + arr[i].toLong() * (i - left[i]) * (right[i] - i)) % mod
        }
        return sum.toInt()
    }
}
```

```java
import java.util.*;

public class SumOfSubarrayMinimums {
    /**
     * @param arr input array
     * @return    sum of subarray minimums mod 1e9+7
     */
    public int sumSubarrayMins(int[] arr) {
        int n = arr.length;
        int mod = 1_000_000_007;
        Deque<Integer> stack = new ArrayDeque<>();
        int[] left = new int[n], right = new int[n];

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) stack.pop();
            left[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }

        stack.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) stack.pop();
            right[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum = (sum + (long) arr[i] * (i - left[i]) * (right[i] - i)) % mod;
        }
        return (int) sum;
    }
}
```

```cpp
#include <vector>
#include <stack>

class SumOfSubarrayMinimums {
public:
    /**
     * @param arr input array
     * @return    sum of subarray minimums mod 1e9+7
     */
    int sumSubarrayMins(std::vector<int>& arr) {
        int n = arr.size();
        long long mod = 1e9 + 7;
        std::stack<int> st;
        std::vector<int> left(n), right(n);

        for (int i = 0; i < n; i++) {
            while (!st.empty() && arr[st.top()] >= arr[i]) st.pop();
            left[i] = st.empty() ? -1 : st.top();
            st.push(i);
        }

        while (!st.empty()) st.pop();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.empty() && arr[st.top()] > arr[i]) st.pop();
            right[i] = st.empty() ? n : st.top();
            st.push(i);
        }

        long long sum = 0;
        for (int i = 0; i < n; i++) {
            sum = (sum + (long long)arr[i] * (i - left[i]) * (right[i] - i)) % mod;
        }
        return (int)sum;
    }
};
```

```python
def sum_subarray_mins(arr: list[int]) -> int:
    """
    @param arr: input array
    @return:    sum of subarray minimums mod 1e9+7
    """
    n = len(arr)
    mod = 10**9 + 7

    left = [-1] * n
    stack = []
    for i in range(n):
        while stack and arr[stack[-1]] >= arr[i]:
            stack.pop()
        left[i] = stack[-1] if stack else -1
        stack.append(i)

    right = [n] * n
    stack = []
    for i in range(n - 1, -1, -1):
        while stack and arr[stack[-1]] > arr[i]:
            stack.pop()
        right[i] = stack[-1] if stack else n
        stack.append(i)

    return sum(arr[i] * (i - left[i]) * (right[i] - i) for i in range(n)) % mod
```

```rust
impl Solution {
    /// @param arr input array
    /// @return    sum of subarray minimums mod 1e9+7
    pub fn sum_subarray_mins(arr: Vec<i32>) -> i32 {
        let n = arr.len();
        let mut left = vec![0i64; n];
        let mut right = vec![0i64; n];
        let mut stack: Vec<usize> = Vec::new();

        for i in 0..n {
            while let Some(&j) = stack.last() {
                if arr[j] >= arr[i] { stack.pop(); } else { break; }
            }
            left[i] = stack.last().map(|&j| j as i64).unwrap_or(-1);
            stack.push(i);
        }

        stack.clear();
        for i in (0..n).rev() {
            while let Some(&j) = stack.last() {
                if arr[j] > arr[i] { stack.pop(); } else { break; }
            }
            right[i] = stack.last().map(|&j| j as i64).unwrap_or(n as i64);
            stack.push(i);
        }

        let mut sum = 0i64;
        for i in 0..n {
            sum = (sum + arr[i] as i64 * (i as i64 - left[i]) * (right[i] - i as i64)) % 1_000_000_007;
        }
        sum as i32
    }
}
```

## Dry run

**Input:** `arr = [3,1,2,4]`.

```
left: 3: -1.  1: -1.  2: 1.  4: 2.
right: 4: 4.  2: 4? 4>2? arr[3]=4 > 2 -> pop... right[2]: stack has 3 (4): 4 > 2 -> pop.  empty -> 4.
  1: next smaller-or-equal: 3? arr[0]=3 > 1 pop... empty -> 4.  3: right = 1 (arr[1]=1 <= 3).
  3: left -1, right 1: 3*1*1 = 3.  1: left -1, right 4: 1*1*4 = 4.  2: left 1, right 4: 2*1*2 = 4.
  4: left 2, right 4: 4*1*1 = 4.  sum = 3+4+4+4 = 15?  Expected 17.
  Subarrays mins: [3]=3, [1]=1, [2]=2, [4]=4, [3,1]=1, [1,2]=1, [2,4]=2, [3,1,2]=1, [1,2,4]=1,
  [3,1,2,4]=1.  Sum: 3+1+2+4 +1+1+2 +1+1 +1 = 17.
  My right[] trace is off: right[3] (4): no smaller-or-equal to the right -> n=4.  right[2] (2): arr[3]=4 > 2 -> pop, empty -> 4.  right[1] (1): 2 > 1 pop? wait stack after left pass... I recomputed right with a fresh stack: i=3: stack empty -> right=4.  i=2: arr[3]=4 > 2 pop; empty -> right=4.  i=1: arr[2]=2 > 1 pop; arr[3]=4 > 1 pop; empty -> right=4.  i=0: arr[1]=1 <= 3 -> right=1.
  Contributions: 3: (0-(-1))*(1-0) = 1*1 = 1 → 3.  1: (1+1)*(4-1) = 2*3 = 6 → 6.  2: (2-1)*(4-2) = 1*2 = 2 → 4.  4: (3-2)*(4-3) = 1*1 = 1 → 4.  Sum = 3+6+4+4 = 17 ✓
```

## Complexity

**Time.** Two passes:

$$
T(n) = O(n)
$$

**Space.** Arrays + stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Sum Of Subarray Ranges** ([8.26](sum-of-subarray-ranges.md)) — max − min per subarray.
- **Interview follow-up:** "Why strict on one side only?" Equal minima would be double-counted without the asymmetry — the strict/≤ split assigns each subarray's minimum to exactly one index.
