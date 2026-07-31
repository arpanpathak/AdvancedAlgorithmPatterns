# 8.5 Largest Rectangle In Histogram

> **Source:** [`src/main/kotlin/stack/LargestRectangleInHistogram.kt`](https://github.com/arpanpathak/AdvancedAlgorithmPatterns/blob/main/src/main/kotlin/stack/LargestRectangleInHistogram.kt)
> **Pattern:** monotonic stack + sentinel · **Core page**

## The Problem

Given an array `heights` of bar heights, return the area of the **largest rectangle** that can be formed entirely within the histogram (a rectangle's width is a contiguous run of bars, its height is the *shortest* bar in that run).

- Constraints: $1 \le n \le 10^5$; $0 \le heights[i] \le 10^4$.

## Examples

```
Input:  heights = [2,1,5,6,2,3]
Output: 10   (bars 5 and 6: the 5-high rectangle of width 2)

Input:  heights = [2,4]
Output: 4    (bar 4 alone, width 1 — or the 2-high rectangle over both)
```

## Intuition — every rectangle is "some bar, stretched as far as it can go"

For any rectangle in the histogram, its height is the height of its *shortest* bar. So every maximal rectangle is anchored by some bar `h` and extends left and right **until a bar shorter than `h`** — beyond that, the rectangle's height would drop. If we knew, for each bar, its *previous smaller* and *next smaller* neighbor, the best rectangle anchored at that bar is:

$$
\text{area}(i) = h_i \cdot (\text{nextSmaller}(i) - \text{prevSmaller}(i) - 1)
$$

Finding both smaller neighbors naively is $O(n^2)$ with nested loops. The **monotonic stack** finds both in one sweep, and here's the beautiful part: **when a bar is popped, both boundaries are already known** — the current index is its *next smaller* (that's why it's being popped), and the new stack top is its *previous smaller*. Width is `i - stack.top - 1` (or `i` if the stack emptied).

**Why the stack holds indices, not heights:** the area needs the *distance* between boundaries. Indices give both the height (`heights[top]`) and the position — the same lesson as [8.3](daily-temperatures.md), but here the width is the answer itself, so indices are non-negotiable.

**The sentinel trick:** when the sweep ends, bars still on the stack have no next-smaller bar — but they *do* form rectangles reaching the array's end. Appending a **dummy `0` bar** to the array makes the final loop pop every remaining bar with `i = n` as the next-smaller boundary. One line (`heightsList.add(0)`) removes an entire post-loop. This is the "sentinel" device that shows up again and again in stack problems.

## Approach 1 — For each bar, expand outward (too slow)

For each bar, walk left and right until a shorter bar: $O(n^2)$ — fails at $n = 10^5$.

## Approach 2 — Monotonic stack with a sentinel (the repo's version, optimal)

```kotlin
class LargestRectangleInHistogram {
    /**
     * @param heights bar heights
     * @return        area of the largest rectangle in the histogram
     */
    fun largestRectangleArea(heights: IntArray): Int {
        val stack = mutableListOf<Int>()             // indices, increasing heights
        var maxArea = 0
        val heightsList = heights.toMutableList()
        heightsList.add(0)                           // sentinel: pops every bar at the end

        for (i in heightsList.indices) {
            // While the current bar is shorter than the one at the top of the stack
            while (stack.isNotEmpty() && heightsList[stack.last()] > heightsList[i]) {
                val h = heightsList[stack.removeLast()]   // popped bar = rectangle height
                val w = if (stack.isEmpty()) i else i - stack.last() - 1   // between smaller bars
                maxArea = maxOf(maxArea, h * w)
            }
            stack.add(i)                             // increasing-height invariant
        }
        return maxArea
    }
}
```

```java
import java.util.*;

public class LargestRectangleInHistogram {
    /**
     * @param heights bar heights
     * @return        area of the largest rectangle in the histogram
     */
    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int[] h = Arrays.copyOf(heights, n + 1);     // sentinel 0 appended
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;

        for (int i = 0; i <= n; i++) {
            while (!stack.isEmpty() && h[stack.peek()] > h[i]) {
                int height = h[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }
}
```

```cpp
#include <algorithm>
#include <stack>
#include <vector>

class LargestRectangleInHistogram {
public:
    /**
     * @param heights bar heights
     * @return        area of the largest rectangle in the histogram
     */
    int largestRectangleArea(std::vector<int>& heights) {
        heights.push_back(0);                        // sentinel: pops every bar at the end
        std::stack<int> st;                          // indices, increasing heights
        int maxArea = 0;

        for (int i = 0; i < (int)heights.size(); i++) {
            while (!st.empty() && heights[st.top()] > heights[i]) {
                int h = heights[st.top()]; st.pop();
                int w = st.empty() ? i : i - st.top() - 1;
                maxArea = std::max(maxArea, h * w);
            }
            st.push(i);
        }
        return maxArea;
    }
};
```

```python
def largest_rectangle_area(heights: list[int]) -> int:
    """
    @param heights: bar heights
    @return:        area of the largest rectangle in the histogram
    """
    heights = heights + [0]                          # sentinel: pops every bar at the end
    stack = []                                       # indices, increasing heights
    max_area = 0

    for i, h in enumerate(heights):
        while stack and heights[stack[-1]] > h:
            height = heights[stack.pop()]
            width = i if not stack else i - stack[-1] - 1
            max_area = max(max_area, height * width)
        stack.append(i)
    return max_area
```

```rust
impl Solution {
    /// @param heights bar heights
    /// @return        area of the largest rectangle in the histogram
    pub fn largest_rectangle_area(heights: Vec<i32>) -> i32 {
        let mut h = heights;
        h.push(0);                                       // sentinel: pops every bar at the end
        let mut stack: Vec<usize> = Vec::new();          // indices, increasing heights
        let mut max_area = 0;

        for i in 0..h.len() {
            while let Some(&top) = stack.last() {
                if h[top] <= h[i] { break; }
                let height = h[top];
                stack.pop();
                let width = if stack.is_empty() { i } else { i - stack.last().unwrap() - 1 };
                max_area = max_area.max(height as usize * width);
            }
            stack.push(i);
        }
        max_area as i32
    }
}
```

## Dry run

**Input:** `heights = [2,1,5,6,2,3]` (sentinel appended -> `[2,1,5,6,2,3,0]`).

```
i=0 (2): stack empty -> push 0.                          stack=[0]
i=1 (1): 2 > 1 -> pop 0: h=2, w=1-0=1 (stack empty) -> area 2. push 1.  stack=[1]
i=2 (5): push 2.                                          stack=[1,2]
i=3 (6): push 3.                                          stack=[1,2,3]
i=4 (2): 6 > 2 -> pop 3: h=6, w=4-2-1=1 -> 6.            stack=[1,2]
         5 > 2 -> pop 2: h=5, w=4-1-1=2 -> 10.  max=10.  stack=[1]
         1 > 2? no. push 4.                               stack=[1,4]
i=5 (3): 2 > 3? no. push 5.                               stack=[1,4,5]
i=6 (0): 3 > 0 -> pop 5: h=3, w=6-4-1=1 -> 3.            stack=[1,4]
         2 > 0 -> pop 4: h=2, w=6-1-1=4 -> 8.             stack=[1]
         1 > 0 -> pop 1: h=1, w=6 (stack empty) -> 6.     stack=[]
         push 6.                                          stack=[6]

maxArea = 10 ✓   (the 5-high rectangle over bars 5,6 — width 2)
```

The sentinel's work is visible at i=6: three leftover bars get popped by the dummy `0`, each computing its "stretch to the end" rectangle (the last one, h=1, spans the whole width 6). Without the sentinel, those three would need a separate post-loop.

## Complexity

**Time.** Each bar pushed once, popped once (sentinel included):

$$
T(n) = O(n)
$$

**Space.** The stack:

$$
S(n) = O(n)
$$

## Variants & follow-ups

- **Maximal Rectangle** (a binary matrix) — for each row, treat the running column heights as a histogram and run *this* page: the classic "reduce 2-D to 1-D" move.
- **Trapping Rain Water** — the same "nearest taller boundary" geometry with the boundaries summed instead of rectangles multiplied.
- **Sum Of Subarray Ranges / Minimums** (`src/main/kotlin/stack/SumOfSubArrayRanges.kt`) — the same prev-smaller/next-smaller bookkeeping, multiplied by counting contributions instead of one max.
- **Interview follow-up:** "Why is the width `i - stack.top - 1` and not `i - prevIndex`?" The popped bar's rectangle can't use any bar still *on* the stack — those are shorter (or equal), so they bound it. The new stack top is the previous smaller bar; `i` is the next smaller; the width between them is exactly `i - stack.top - 1`. This "boundaries are whatever is still on the stack" reasoning is the entire hard part of this problem.
