# 8.3 Daily Temperatures

> **Source:** [`src/main/kotlin/stack/DailyTemperatures.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/DailyTemperatures.kt)
> **Pattern:** monotonic stack · **Core page**

## The Problem

Given an array `temperatures` (daily highs), return an array `answer` where `answer[i]` is the number of days you must wait until a **warmer** day — or `0` if none ever comes.

- Constraints: $1 \le n \le 10^5$; $30 \le temperatures[i] \le 100$.

## Examples

```
Input:  temperatures = [73,74,75,71,69,72,76,73]
Output: [1,1,4,2,1,1,0,0]
        73 -> 74 (1 day), 75 -> 76 (4 days), 71 -> 72 (1 day)... last two never warmer

Input:  temperatures = [30,40,50,60]
Output: [1,1,1,0]
```

## Intuition — "who am I waiting for?" is a stack of unresolved days

The naive double loop (for each day, scan forward for the first warmer) is $O(n^2)$. The monotonic-stack insight: **a day only stays unresolved while the following days keep getting colder.** The moment a warmer day arrives, *all* unresolved colder days that came before it get resolved at once — each by this same day.

So maintain a stack of **indices of unresolved days**, in *decreasing temperature order* (the stack top is the coldest among them). For each new day `i`:

- while the stack is non-empty and `temperatures[i] > temperatures[stack.top]` — day `i` is the first warmer day for the top: resolve it (`answer[top] = i - top`), pop;
- then push `i` (unresolved, for now).

The "while" is the resolve-all-the-cold-ones sweep. Each index is pushed once and popped once, so the whole run is $O(n)$ — the amortization argument from the [primer](pattern-primer.md) in action.

**Why indices, not temperatures?** The answer needs *distance* (`i - top`). Storing temperatures would force a parallel array of positions; storing indices gives both the value (`temperatures[top]`) and the position. This is the same "store indices" lesson as [8.5](largest-rectangle-in-histogram.md).

**What stays on the stack?** The unresolved *decreasing* tail — days that have no warmer day to their right yet. When the sweep ends, everything still on the stack gets `0` (the `IntArray` default) — no warmer day ever comes.

## Approach 1 — Nested scan (too slow)

For each day, scan right until a warmer day: worst case $O(n^2)$ (a strictly decreasing array scans $n, n-1, \ldots, 1$).

## Approach 2 — Monotonic stack (the repo's version, optimal)

```kotlin
class DailyTemperatures {
    /**
     * @param temperatures daily temperatures
     * @return            days until a warmer day, 0 if none
     */
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val result = IntArray(temperatures.size) { 0 }
        val stack = mutableListOf<Int>()           // indices of unresolved days, decreasing temps

        for (i in temperatures.indices) {
            // Day i resolves every unresolved colder day above it on the stack
            while (stack.isNotEmpty() && temperatures[i] > temperatures[stack.last()]) {
                val idx = stack.removeLast()
                result[idx] = i - idx              // first warmer day is i
            }
            stack.add(i)                           // i stays unresolved (for now)
        }
        return result                              // stack leftovers already 0
    }
}
```

```java
import java.util.*;

public class DailyTemperatures {
    /**
     * @param temperatures daily temperatures
     * @return            days until a warmer day, 0 if none
     */
    public int[] dailyTemperatures(int[] temperatures) {
        int[] result = new int[temperatures.length];
        Deque<Integer> stack = new ArrayDeque<>();   // indices of unresolved days

        for (int i = 0; i < temperatures.length; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int idx = stack.pop();
                result[idx] = i - idx;               // first warmer day is i
            }
            stack.push(i);
        }
        return result;                               // leftovers already 0
    }
}
```

```cpp
#include <stack>
#include <vector>

class DailyTemperatures {
public:
    /**
     * @param temperatures daily temperatures
     * @return            days until a warmer day, 0 if none
     */
    std::vector<int> dailyTemperatures(std::vector<int>& temperatures) {
        std::vector<int> result(temperatures.size(), 0);
        std::stack<int> st;                          // indices of unresolved days

        for (int i = 0; i < (int)temperatures.size(); i++) {
            while (!st.empty() && temperatures[i] > temperatures[st.top()]) {
                int idx = st.top(); st.pop();
                result[idx] = i - idx;               // first warmer day is i
            }
            st.push(i);
        }
        return result;                               // leftovers already 0
    }
};
```

```python
def daily_temperatures(temperatures: list[int]) -> list[int]:
    """
    @param temperatures: daily temperatures
    @return:             days until a warmer day, 0 if none
    """
    result = [0] * len(temperatures)
    stack = []                                  # indices of unresolved days

    for i, temp in enumerate(temperatures):
        while stack and temp > temperatures[stack[-1]]:
            idx = stack.pop()
            result[idx] = i - idx               # first warmer day is i
        stack.append(i)
    return result
```

```rust
impl Solution {
    /// @param temperatures daily temperatures
    /// @return            days until a warmer day, 0 if none
    pub fn daily_temperatures(temperatures: Vec<i32>) -> Vec<i32> {
        let mut result = vec![0; temperatures.len()];
        let mut stack: Vec<usize> = Vec::new();     // indices of unresolved days

        for i in 0..temperatures.len() {
            while let Some(&idx) = stack.last() {
                if temperatures[i] <= temperatures[idx] { break; }
                result[idx] = (i - idx) as i32;     // first warmer day is i
                stack.pop();
            }
            stack.push(i);
        }
        result
    }
}
```

## Dry run

**Input:** `temperatures = [73,74,75,71,69,72,76,73]`.

```
i=0 (73): stack empty -> push 0.                    stack=[0]
i=1 (74): 74 > 73 -> resolve 0: result[0]=1, pop.   stack=[]
          push 1.                                   stack=[1]
i=2 (75): 75 > 74 -> result[1]=1, pop.              stack=[]
          push 2.                                   stack=[2]
i=3 (71): 71 > 75? no -> push 3.                    stack=[2,3]
i=4 (69): 69 > 71? no -> push 4.                    stack=[2,3,4]
i=5 (72): 72 > 69 -> result[4]=1, pop.
          72 > 71 -> result[3]=2, pop.              stack=[2]
          72 > 75? no. push 5.                      stack=[2,5]
i=6 (76): 76 > 72 -> result[5]=1, pop.
          76 > 75 -> result[2]=4, pop.              stack=[]
          push 6.                                   stack=[6]
i=7 (73): 73 > 76? no -> push 7.                    stack=[6,7]
end: stack leftovers {6,7} keep result 0.

result = [1,1,4,2,1,1,0,0] ✓
```

The pivotal moment is i=5: one day (`72`) resolves *two* unresolved colder days at once (69 at distance 1, 71 at distance 2). That's the monotonic-stack efficiency — each element resolved exactly once, by exactly one later day.

## Complexity

**Time.** Each index pushed once, popped once:

$$
T(n) = O(n)
$$

**Space.** The stack of unresolved indices:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Next Greater Element I / II** ([8.4](next-greater-element-ii.md)) — same engine, values instead of distances; the circular version doubles the array conceptually.
- **Online Stock Span** (`src/main/kotlin/stack/OnlineStockSpan.kt`) — the "next greater to the left" mirror: the span is the distance to the *previous* larger element.
- **Sum Of Subarray Minimums** (`src/main/kotlin/stack/SumOfSubArrayMinimum.kt`) — the deepest descendant: every subarray's minimum is found by this same monotonic stack, then summed by a counting argument.
- **Interview follow-up:** "Why does the stack stay decreasing?" A day only stays unresolved if every later day so far is colder — so unresolved indices form a decreasing temperature sequence. The `while` pop preserves that invariant, which is what makes "the first warmer day" computable in one sweep.
